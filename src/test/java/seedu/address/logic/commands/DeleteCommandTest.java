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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
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
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validTransactionIndexUnfilteredList_success() throws Exception {
        // Get first two persons in the address book
        Person personToModify = model.getFilteredPersonList().get(0);
        Person otherPerson = model.getFilteredPersonList().get(1);

        // Seed a transaction between them
        Transaction seedTransaction = new MonthlyTransaction(personToModify, otherPerson, 10.0, 0.0, "Test Transaction");
        personToModify.appendTransaction(seedTransaction);
        otherPerson.appendTransaction(seedTransaction);

        // Use index 0 for person and transaction
        Index personIndex = Index.fromZeroBased(0);
        Index transactionIndex = Index.fromZeroBased(0);

        // Sort transactions the same way DeleteCommand does
        List<Transaction> transactions = personToModify.getTransactions().stream()
                .sorted(Comparator.comparingDouble(Transaction::getCurrAmount).reversed())
                .collect(Collectors.toList());

        Transaction transactionToDelete = transactions.get(transactionIndex.getZeroBased());

        // Create the delete command
        DeleteCommand deleteCommand = new DeleteCommand(personIndex, transactionIndex);

        // Prepare expected message
        String transactionDetails = String.format("$%.2f | %s | %s → %s",
                transactionToDelete.getCurrAmount(),
                transactionToDelete.getDescription(),
                transactionToDelete.getDebtor().getName(),
                transactionToDelete.getCreditor().getName());

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_TRANSACTION_SUCCESS,
                transactionIndex.getOneBased(), transactionDetails);

        // Prepare expected model
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Person expectedPerson = DeleteCommand.createPersonWithoutTransaction(personToModify, transactionToDelete);
        Person expectedOtherPerson = DeleteCommand.createPersonWithoutTransaction(otherPerson, transactionToDelete);

        expectedModel.setPerson(personToModify, expectedPerson);
        expectedModel.setPerson(otherPerson, expectedOtherPerson);

        // Execute command
        CommandResult commandResult = deleteCommand.execute(model);

        // Assertions
        assertEquals(expectedMessage, commandResult.getFeedbackToUser());
        assertTrue(commandResult.getPersonIndexToRefresh().isPresent());
        assertEquals(personIndex.getOneBased(), commandResult.getPersonIndexToRefresh().getAsInt());

        // Check that transaction is removed from both persons
        Person updatedPerson = model.getFilteredPersonList().get(0);
        Person updatedOtherPerson = model.getFilteredPersonList().get(1);

        assertFalse(updatedPerson.getTransactions().contains(transactionToDelete));
        assertFalse(updatedOtherPerson.getTransactions().contains(transactionToDelete));
    }

    @Test
    public void execute_invalidTransactionIndex_throwsCommandException() {
        Person personToModify = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
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

        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        DeleteCommand deleteFirstTransactionCommandCopy =
                new DeleteCommand(INDEX_FIRST_PERSON, Index.fromOneBased(1));
        assertTrue(deleteFirstTransactionCommand.equals(deleteFirstTransactionCommandCopy));

        assertFalse(deleteFirstCommand.equals(1));
        assertFalse(deleteFirstCommand.equals(null));
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
        assertFalse(deleteFirstTransactionCommand.equals(deleteSecondTransactionCommand));
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
