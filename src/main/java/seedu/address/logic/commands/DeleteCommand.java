package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
    public static final String MESSAGE_DELETE_TRANSACTION_SUCCESS = "Deleted Transaction #%1$d.";
    public static final String MESSAGE_NO_TRANSACTIONS = "No transactions found for %1$s.";
    public static final String MESSAGE_INVALID_TRANSACTION_DISPLAYED_INDEX =
            "The transaction index provided is invalid";

    private final Index targetIndex;
    private final Index targetTransactionIndex;

    public DeleteCommand(Index targetIndex) {
        this(targetIndex, null);
    }

    /**
     * Creates a DeleteCommand to delete the person at the specified {@code targetIndex}
     * and their transaction at the specified {@code targetTransactionIndex}.
     */
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
     * Deletes a specific transaction from the given {@code person}.
     */
    private CommandResult deleteTransaction(Model model, Person person) throws CommandException {
        List<Transaction> transactions = new ArrayList<>(person.getTransactions());
        if (transactions.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_NO_TRANSACTIONS, person.getName()));
        }

        if (targetTransactionIndex.getZeroBased() >= transactions.size()) {
            throw new CommandException(MESSAGE_INVALID_TRANSACTION_DISPLAYED_INDEX);
        }

        Transaction transactionToDelete = transactions.get(targetTransactionIndex.getZeroBased());
        Person updatedPerson = createPersonWithoutTransaction(person, transactionToDelete);

        model.setPerson(person, updatedPerson);
        return new CommandResult(String.format(MESSAGE_DELETE_TRANSACTION_SUCCESS,
                targetTransactionIndex.getOneBased()));
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code person}
     * but without {@code transactionToRemove}.
     */
    private static Person createPersonWithoutTransaction(Person person, Transaction transactionToRemove) {
        Set<Transaction> updatedTransactions = new HashSet<>(person.getTransactions());
        updatedTransactions.remove(transactionToRemove);

        return new Person(person.getName(), person.getPhone(), person.getEmail(), person.getAddress(),
                person.getTags(), updatedTransactions);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand)) {
            return false;
        }

        DeleteCommand otherDeleteCommand = (DeleteCommand) other;
        return targetIndex.equals(otherDeleteCommand.targetIndex) && Objects.equals(targetTransactionIndex,
                otherDeleteCommand.targetTransactionIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("transactionIndex", targetTransactionIndex)
                .toString();
    }
}
