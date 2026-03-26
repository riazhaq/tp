package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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
 * Deletes a person or a specific transaction using the displayed indexes.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a person or a specific transaction using the displayed indexes.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[t/TRANS_INDEX]\n"
            + "Example: " + COMMAND_WORD + " 1 or " + COMMAND_WORD + " 1 t/2";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    public static final String MESSAGE_DELETE_TRANSACTION_SUCCESS =
            "Deleted Transaction #%1$d: %2$s";
    public static final String MESSAGE_NO_TRANSACTIONS = "No transactions found for %1$s.";
    public static final String MESSAGE_INVALID_TRANSACTION_DISPLAYED_INDEX =
            "The transaction index provided is invalid";

    private final Index targetIndex;
    private final Index targetTransactionIndex;

    public DeleteCommand(Index targetIndex) {
        this(targetIndex, null);
    }

    public DeleteCommand(Index targetIndex, Index targetTransactionIndex) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
        this.targetTransactionIndex = targetTransactionIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToModify = lastShownList.get(targetIndex.getZeroBased());

        if (targetTransactionIndex == null) {
            model.deletePerson(personToModify);
            return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS,
                    Messages.format(personToModify)));
        }

        return deleteTransaction(model, personToModify);
    }

    /**
     * Deletes a specific transaction from BOTH involved persons.
     */
    private CommandResult deleteTransaction(Model model, Person person) throws CommandException {

        List<Transaction> transactions = person.getTransactions().stream()
                .sorted(Comparator.comparingDouble(Transaction::getCurrAmount).reversed())
                .collect(Collectors.toList());

        if (transactions.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_NO_TRANSACTIONS, person.getName()));
        }

        if (targetTransactionIndex.getZeroBased() >= transactions.size()) {
            throw new CommandException(MESSAGE_INVALID_TRANSACTION_DISPLAYED_INDEX);
        }

        Transaction transactionToDelete = transactions.get(targetTransactionIndex.getZeroBased());

        // --- Identify the other person involved ---
        Person debtor = transactionToDelete.getDebtor();
        Person creditor = transactionToDelete.getCreditor();

        Person otherPerson = debtor.equals(person) ? creditor : debtor;

        // --- Create updated persons ---
        Person updatedPerson = createPersonWithoutTransaction(person, transactionToDelete);
        Person updatedOtherPerson = createPersonWithoutTransaction(otherPerson, transactionToDelete);

        // --- Update model ---
        model.setPerson(person, updatedPerson);

        // Avoid double-setting if self-transaction (edge case safety)
        if (!person.equals(otherPerson)) {
            model.setPerson(otherPerson, updatedOtherPerson);
        }

        String transactionDetails = formatTransaction(transactionToDelete);

        return new CommandResult(
                String.format(
                        MESSAGE_DELETE_TRANSACTION_SUCCESS,
                        targetTransactionIndex.getOneBased(),
                        transactionDetails
                ),
                targetIndex.getOneBased()
        );
    }

    /**
     * Returns a user-friendly string representation of a transaction.
     */
    private String formatTransaction(Transaction t) {
        return String.format("$%.2f | %s | %s → %s",
                t.getCurrAmount(),
                t.getDescription(),
                t.getDebtor().getName(),
                t.getCreditor().getName());
    }

    /**
     * Creates and returns a {@code Person} with the given transaction removed.
     */
    private static Person createPersonWithoutTransaction(Person person, Transaction transactionToRemove) {
        Set<Transaction> updatedTransactions = new HashSet<>(person.getTransactions());
        updatedTransactions.remove(transactionToRemove);

        return new Person(
                person.getName(),
                person.getPhone(),
                person.getEmail(),
                person.getAddress(),
                person.getTags(),
                updatedTransactions
        );
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteCommand)) {
            return false;
        }
        DeleteCommand otherDeleteCommand = (DeleteCommand) other;
        return targetIndex.equals(otherDeleteCommand.targetIndex)
                && Objects.equals(targetTransactionIndex, otherDeleteCommand.targetTransactionIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("transactionIndex", targetTransactionIndex)
                .toString();
    }
}
