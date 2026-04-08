package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.Transaction;

/**
 * Computes a simplified settlement plan among three or more selected persons.
 * This command is a preview only and does not mutate any transactions.
 */
public class SimplifyCommand extends Command {

    public static final String COMMAND_WORD = "simplify";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Computes a simplified debt-settlement plan among three or more persons.\n"
            + "Parameters: PERSON_INDEX [MORE_PERSON_INDEXES...] (all must be positive integers, count >= 3)\n"
            + "Example: " + COMMAND_WORD + " 1 2 3 4";

    public static final String MESSAGE_MINIMUM_PARTICIPANTS =
            "At least 3 distinct person indices are required.";
    public static final String MESSAGE_DUPLICATE_PERSON_INDEX =
            "Duplicate person index detected: %1$d";

    private static final double EPSILON = 1e-6;

    private final List<Index> participantIndices;

    /**
     * Creates a {@code SimplifyCommand} for the provided participant indices.
     */
    public SimplifyCommand(List<Index> participantIndices) {
        requireNonNull(participantIndices);
        this.participantIndices = List.copyOf(participantIndices);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (participantIndices.size() < 3) {
            throw new CommandException(MESSAGE_MINIMUM_PARTICIPANTS);
        }

        List<Person> lastShownList = model.getFilteredPersonList();
        List<Person> participants = resolveParticipants(lastShownList);

        List<SettlementInstruction> instructions = computeSettlementInstructions(participants);
        String feedback = formatPlanFeedback(participants, instructions);
        return new CommandResult(feedback);
    }

    private List<Person> resolveParticipants(List<Person> lastShownList) throws CommandException {
        Set<Integer> seenZeroBased = new HashSet<>();
        List<Person> participants = new ArrayList<>();

        for (Index index : participantIndices) {
            if (index.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }

            int zeroBased = index.getZeroBased();
            if (!seenZeroBased.add(zeroBased)) {
                throw new CommandException(String.format(MESSAGE_DUPLICATE_PERSON_INDEX, index.getOneBased()));
            }

            participants.add(lastShownList.get(zeroBased));
        }

        if (participants.size() < 3) {
            throw new CommandException(MESSAGE_MINIMUM_PARTICIPANTS);
        }

        return participants;
    }

    private static List<SettlementInstruction> computeSettlementInstructions(List<Person> participants) {
        Map<Person, Double> netBalances = new HashMap<>();
        participants.forEach(person -> netBalances.put(person, 0.0));

        Set<Transaction> uniqueTransactions = new HashSet<>();
        for (Person person : participants) {
            uniqueTransactions.addAll(person.getTransactions());
        }

        for (Transaction transaction : uniqueTransactions) {
            if (transaction.isSettled()) {
                continue;
            }

            Person canonicalDebtor = findCanonicalParticipant(participants, transaction.getDebtor());
            Person canonicalCreditor = findCanonicalParticipant(participants, transaction.getCreditor());
            if (canonicalDebtor == null || canonicalCreditor == null) {
                continue;
            }

            double amount = transaction.getCurrAmount();
            if (Math.abs(amount) < EPSILON) {
                continue;
            }

            netBalances.put(canonicalDebtor, netBalances.get(canonicalDebtor) - amount);
            netBalances.put(canonicalCreditor, netBalances.get(canonicalCreditor) + amount);
        }

        List<BalanceNode> debtors = netBalances.entrySet().stream()
                .filter(entry -> entry.getValue() < -EPSILON)
                .map(entry -> new BalanceNode(entry.getKey(), -entry.getValue()))
                .sorted(Comparator.comparingDouble(BalanceNode::amount).reversed())
                .collect(Collectors.toList());

        List<BalanceNode> creditors = netBalances.entrySet().stream()
                .filter(entry -> entry.getValue() > EPSILON)
                .map(entry -> new BalanceNode(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingDouble(BalanceNode::amount).reversed())
                .collect(Collectors.toList());

        List<SettlementInstruction> instructions = new ArrayList<>();
        int debtorPointer = 0;
        int creditorPointer = 0;

        while (debtorPointer < debtors.size() && creditorPointer < creditors.size()) {
            BalanceNode debtor = debtors.get(debtorPointer);
            BalanceNode creditor = creditors.get(creditorPointer);

            double transfer = Math.min(debtor.amount(), creditor.amount());
            if (transfer > EPSILON) {
                instructions.add(new SettlementInstruction(debtor.person(), creditor.person(), transfer));
            }

            debtor.decreaseBy(transfer);
            creditor.decreaseBy(transfer);

            if (debtor.amount() <= EPSILON) {
                debtorPointer++;
            }
            if (creditor.amount() <= EPSILON) {
                creditorPointer++;
            }
        }

        return instructions;
    }

    private static Person findCanonicalParticipant(List<Person> participants, Person candidate) {
        for (Person participant : participants) {
            if (participant.isSamePerson(candidate)) {
                return participant;
            }
        }
        return null;
    }

    private String formatPlanFeedback(List<Person> participants, List<SettlementInstruction> instructions) {
        String names = participants.stream()
                .map(person -> person.getName().fullName)
                .collect(Collectors.joining(", "));

        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Simplified settlement plan (%d persons): %s", participants.size(), names));

        if (instructions.isEmpty()) {
            builder.append(System.lineSeparator())
                    .append("No payments needed among selected persons.");
            return builder.toString();
        }

        for (int i = 0; i < instructions.size(); i++) {
            SettlementInstruction instruction = instructions.get(i);
            builder.append(System.lineSeparator())
                    .append(String.format("%d. %s pays %s $%.2f",
                            i + 1,
                            instruction.payer().getName().fullName,
                            instruction.payee().getName().fullName,
                            instruction.amount()));
        }

        return builder.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SimplifyCommand)) {
            return false;
        }
        SimplifyCommand otherCommand = (SimplifyCommand) other;
        return participantIndices.equals(otherCommand.participantIndices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participantIndices);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("participantIndices", participantIndices)
                .toString();
    }

    private static final class BalanceNode {
        private final Person person;
        private double amount;

        private BalanceNode(Person person, double amount) {
            this.person = person;
            this.amount = amount;
        }

        private Person person() {
            return person;
        }

        private double amount() {
            return amount;
        }

        private void decreaseBy(double deduction) {
            amount -= deduction;
        }
    }

    private static final class SettlementInstruction {
        private final Person payer;
        private final Person payee;
        private final double amount;

        private SettlementInstruction(Person payer, Person payee, double amount) {
            this.payer = payer;
            this.payee = payee;
            this.amount = amount;
        }

        private Person payer() {
            return payer;
        }

        private Person payee() {
            return payee;
        }

        private double amount() {
            return amount;
        }
    }
}
