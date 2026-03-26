package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.MonthlyTransaction;
import seedu.address.model.transaction.Transaction;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validTransactionIndexUnfilteredList_success() {
        Index transactionIndex = Index.fromOneBased(1);
        Person personToModify = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        // Ensure there is at least one transaction to delete.
        Person otherPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Transaction seedTransaction = new MonthlyTransaction(personToModify, otherPerson, 10.0, 0.0, "seed");
        personToModify.appendTransaction(seedTransaction);
        otherPerson.appendTransaction(seedTransaction);

        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON, transactionIndex);


        List<Transaction> transactions = new ArrayList<>(personToModify.getTransactions());
        Transaction transactionToDelete = transactions.get(transactionIndex.getZeroBased());
        Set<Transaction> updatedTransactions = new HashSet<>(personToModify.getTransactions());
        updatedTransactions.remove(transactionToDelete);
        Person updatedPerson = new Person(personToModify.getName(), personToModify.getPhone(),
                personToModify.getEmail(), personToModify.getAddress(), personToModify.getTags(), updatedTransactions);


        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToModify, updatedPerson);


        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_TRANSACTION_SUCCESS,
                transactionIndex.getOneBased());


        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }


    @Test
    public void execute_invalidTransactionIndex_throwsCommandException() {
        Person personToModify = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        // Ensure there is at least one transaction, so this test targets an out-of-range index
        // rather than the "no transactions found" case.
        Person otherPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Transaction seedTransaction = new MonthlyTransaction(personToModify, otherPerson, 10.0, 0.0, "seed");
        personToModify.appendTransaction(seedTransaction);
        otherPerson.appendTransaction(seedTransaction);

        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON, Index.fromOneBased(3));
        assertCommandFailure(deleteCommand, model, DeleteCommand.MESSAGE_INVALID_TRANSACTION_DISPLAYED_INDEX);
    }


    @Test
    public void execute_noTransactionsFound_throwsCommandException() {
        Index personIndex = Index.fromOneBased(5);
        DeleteCommand deleteCommand = new DeleteCommand(personIndex, Index.fromOneBased(1));
        Person person = model.getFilteredPersonList().get(personIndex.getZeroBased());
        String expectedMessage = String.format(DeleteCommand.MESSAGE_NO_TRANSACTIONS, person.getName());


        assertCommandFailure(deleteCommand, model, expectedMessage);
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_PERSON);

        DeleteCommand deleteFirstTransactionCommand = new DeleteCommand(INDEX_FIRST_PERSON, Index.fromOneBased(1));
        DeleteCommand deleteSecondTransactionCommand = new DeleteCommand(INDEX_FIRST_PERSON, Index.fromOneBased(2));

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        DeleteCommand deleteFirstTransactionCommandCopy =
                new DeleteCommand(INDEX_FIRST_PERSON, Index.fromOneBased(1));

        assertTrue(deleteFirstTransactionCommand.equals(deleteFirstTransactionCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));

        // different transaction index -> returns false
        assertFalse(deleteFirstTransactionCommand.equals(deleteSecondTransactionCommand));

        // different command type -> returns false
        assertFalse(deleteFirstCommand.equals(deleteFirstTransactionCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteCommand deleteCommand = new DeleteCommand(targetIndex);
        String expected = DeleteCommand.class.getCanonicalName()
                + "{targetIndex=" + targetIndex + ", transactionIndex=null}";
        assertEquals(expected, deleteCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
