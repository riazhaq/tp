package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
 * Marks all unsettled in-group transactions as settled in one action for a selected group of persons.
 */
public class SettleUpCommand extends Command {

    public static final String COMMAND_WORD = "settleup";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Settles all unsettled transactions among three or more selected persons.\n"
            + "Parameters: PERSON_INDEX [MORE_PERSON_INDEXES...] (all must be positive integers, count >= 3)\n"
            + "Example: " + COMMAND_WORD + " 1 2 3 4";

    public static final String MESSAGE_MINIMUM_PARTICIPANTS =
            "At least 3 distinct person indices are required.";
    public static final String MESSAGE_DUPLICATE_PERSON_INDEX =
            "Duplicate person index detected: %1$d";
    public static final String MESSAGE_SUCCESS =
            "Settled up group (%1$d persons): %2$s\n%3$d transaction(s) settled. Total amount: $%4$.2f";
    public static final String MESSAGE_NOTHING_TO_SETTLE =
            "No unsettled transactions found among the selected group.";

    private final List<Index> participantIndices;

    /**
     * Creates a {@code SettleUpCommand} for the provided participant indices.
     */
    public SettleUpCommand(List<Index> participantIndices) {
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

        Set<Transaction> uniqueTransactions = new HashSet<>();
        for (Person person : participants) {
            uniqueTransactions.addAll(person.getTransactions());
        }

        int settled = 0;
        double totalAmount = 0.0;

        for (Transaction transaction : uniqueTransactions) {
            if (transaction.isSettled()) {
                continue;
            }

            Person canonicalDebtor = findCanonicalParticipant(participants, transaction.getDebtor());
            Person canonicalCreditor = findCanonicalParticipant(participants, transaction.getCreditor());
            if (canonicalDebtor == null || canonicalCreditor == null) {
                continue;
            }

            totalAmount += transaction.getCurrAmount();
            transaction.settleTransaction();
            settled++;
        }

        String names = participants.stream()
                .map(person -> person.getName().fullName)
                .collect(Collectors.joining(", "));

        if (settled == 0) {
            return new CommandResult(MESSAGE_NOTHING_TO_SETTLE);
        }

        String feedback = String.format(MESSAGE_SUCCESS, participants.size(), names, settled, totalAmount);
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

    private static Person findCanonicalParticipant(List<Person> participants, Person candidate) {
        for (Person participant : participants) {
            if (participant.isSamePerson(candidate)) {
                return participant;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SettleUpCommand)) {
            return false;
        }
        SettleUpCommand otherCommand = (SettleUpCommand) other;
        return participantIndices.equals(otherCommand.participantIndices);
    }

    @Override
    public int hashCode() {
        return participantIndices.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("participantIndices", participantIndices)
                .toString();
    }
}
