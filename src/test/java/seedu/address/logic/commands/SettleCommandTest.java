package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
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
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.Transaction;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests for {@code SettleCommand}.
 */
public class SettleCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validTransaction_success() throws Exception {
        Person personToModify = model.getFilteredPersonList().get(0);
        Person otherPerson = model.getFilteredPersonList().get(1);

        Transaction seedTransaction = new Transaction(
                personToModify, otherPerson, 25.0, "Lunch");
        personToModify.appendTransaction(seedTransaction);
        otherPerson.appendTransaction(seedTransaction);

        Index personIndex = Index.fromOneBased(1);
        Index transactionIndex = Index.fromOneBased(1);

        SettleCommand settleCommand = new SettleCommand(personIndex, transactionIndex);
        CommandResult result = settleCommand.execute(model);

        List<Transaction> transactions = model.getFilteredPersonList().get(0).getTransactions().stream()
                .sorted(model.getTransactionComparator())
                .collect(Collectors.toList());

        Transaction settledTransaction = transactions.get(0);

        assertEquals(0.0, settledTransaction.getCurrAmount());
        assertTrue(settledTransaction.isSettled());
        assertTrue(result.getPersonIndexToRefresh().isPresent());
        assertEquals(personIndex.getOneBased(), result.getPersonIndexToRefresh().getAsInt());
        assertEquals("Settled Transaction #1: $25.00 | Lunch | "
                        + settledTransaction.getDebtor().getName() + " -> "
                        + settledTransaction.getCreditor().getName(),
                result.getFeedbackToUser());
    }

    @Test
    public void execute_respectsCustomTransactionSortComparator_success() throws Exception {
        Person personToModify = model.getFilteredPersonList().get(0);
        Person otherPerson = model.getFilteredPersonList().get(1);

        Transaction higherAmountLaterInDescription = new Transaction(
                personToModify, otherPerson, 20.0, "Zulu");
        Transaction lowerAmountEarlierInDescription = new Transaction(
                personToModify, otherPerson, 10.0, "Alpha");
        personToModify.appendTransaction(higherAmountLaterInDescription);
        personToModify.appendTransaction(lowerAmountEarlierInDescription);
        otherPerson.appendTransaction(higherAmountLaterInDescription);
        otherPerson.appendTransaction(lowerAmountEarlierInDescription);

        model.setTransactionComparator(Comparator.comparing(Transaction::getDescription));

        SettleCommand settleCommand = new SettleCommand(Index.fromOneBased(1), Index.fromOneBased(1));
        CommandResult result = settleCommand.execute(model);

        assertTrue(lowerAmountEarlierInDescription.isSettled());
        assertFalse(higherAmountLaterInDescription.isSettled());
        assertTrue(result.getFeedbackToUser().contains("Settled Transaction #1"));
        assertTrue(result.getFeedbackToUser().contains("Alpha"));
    }

    @Test
    public void execute_invalidPersonIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        SettleCommand settleCommand = new SettleCommand(outOfBoundIndex, Index.fromOneBased(1));

        assertCommandFailure(settleCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidTransactionIndex_throwsCommandException() {
        Person personToModify = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person otherPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Transaction seedTransaction = new Transaction(personToModify, otherPerson, 10.0, "seed");
        personToModify.appendTransaction(seedTransaction);
        otherPerson.appendTransaction(seedTransaction);

        SettleCommand settleCommand = new SettleCommand(INDEX_FIRST_PERSON, Index.fromOneBased(3));
        assertCommandFailure(settleCommand, model, SettleCommand.MESSAGE_INVALID_TRANSACTION_DISPLAYED_INDEX);
    }

    @Test
    public void execute_noTransactions_throwsCommandException() {
        Index personIndex = Index.fromOneBased(5);
        SettleCommand settleCommand = new SettleCommand(personIndex, Index.fromOneBased(1));
        Person person = model.getFilteredPersonList().get(personIndex.getZeroBased());
        String expectedMessage = String.format(SettleCommand.MESSAGE_NO_TRANSACTIONS, person.getName());

        assertCommandFailure(settleCommand, model, expectedMessage);
    }

    @Test
    public void execute_alreadySettledTransaction_throwsCommandException() throws Exception {
        Person personToModify = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person otherPerson = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());

        Transaction seedTransaction = new Transaction(personToModify, otherPerson, 10.0, "seed");
        seedTransaction.settleTransaction();
        personToModify.appendTransaction(seedTransaction);
        otherPerson.appendTransaction(seedTransaction);

        SettleCommand settleCommand = new SettleCommand(INDEX_FIRST_PERSON, Index.fromOneBased(1));
        assertCommandFailure(settleCommand, model, SettleCommand.MESSAGE_ALREADY_SETTLED);
    }

    @Test
    public void execute_validTransactionFilteredList_success() throws CommandException {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToModify = model.getFilteredPersonList().get(0);
        Person otherPerson = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Transaction seedTransaction = new Transaction(personToModify, otherPerson, 20.0, "Filtered");
        personToModify.appendTransaction(seedTransaction);
        otherPerson.appendTransaction(seedTransaction);

        SettleCommand settleCommand = new SettleCommand(Index.fromOneBased(1), Index.fromOneBased(1));
        CommandResult result = settleCommand.execute(model);

        assertTrue(result.getPersonIndexToRefresh().isPresent());
        assertEquals(1, result.getPersonIndexToRefresh().getAsInt());
    }

    @Test
    public void execute_transactionWithDetachedParties_transactionRetainedAfterSettle() throws Exception {
        Person livePerson = model.getFilteredPersonList().get(0);
        Person liveOther = model.getFilteredPersonList().get(1);

        Person detachedDebtor = new PersonBuilder()
                .withName(livePerson.getName().fullName)
                .withPhone(livePerson.getPhone().value)
                .withEmail(livePerson.getEmail().value)
                .withAddress(livePerson.getAddress().value)
                .build();
        Person detachedCreditor = new PersonBuilder()
                .withName(liveOther.getName().fullName)
                .withPhone(liveOther.getPhone().value)
                .withEmail(liveOther.getEmail().value)
                .withAddress(liveOther.getAddress().value)
                .build();

        Transaction storageLikeTransaction = new Transaction(
                detachedDebtor, detachedCreditor, 40.0, "Detached");
        livePerson.appendTransaction(storageLikeTransaction);
        liveOther.appendTransaction(storageLikeTransaction);

        SettleCommand settleCommand = new SettleCommand(Index.fromOneBased(1), Index.fromOneBased(1));
        settleCommand.execute(model);

        assertEquals(1, model.getFilteredPersonList().get(0).getTransactions().size());
        Transaction retained = model.getFilteredPersonList().get(0).getTransactions().iterator().next();
        assertTrue(retained.isSettled());
        assertEquals(0.0, retained.getCurrAmount());
    }

    @Test
    public void equals() {
        SettleCommand settleFirstCommand = new SettleCommand(INDEX_FIRST_PERSON, Index.fromOneBased(1));
        SettleCommand settleSecondCommand = new SettleCommand(INDEX_SECOND_PERSON, Index.fromOneBased(1));
        SettleCommand settleDifferentTransaction = new SettleCommand(INDEX_FIRST_PERSON, Index.fromOneBased(2));

        assertTrue(settleFirstCommand.equals(settleFirstCommand));
        assertTrue(settleFirstCommand.equals(new SettleCommand(INDEX_FIRST_PERSON, Index.fromOneBased(1))));

        assertFalse(settleFirstCommand.equals(1));
        assertFalse(settleFirstCommand.equals(null));
        assertFalse(settleFirstCommand.equals(settleSecondCommand));
        assertFalse(settleFirstCommand.equals(settleDifferentTransaction));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        Index transactionIndex = Index.fromOneBased(2);
        SettleCommand settleCommand = new SettleCommand(targetIndex, transactionIndex);
        String expected = SettleCommand.class.getCanonicalName()
                + "{targetIndex=" + targetIndex + ", transactionIndex=" + transactionIndex + "}";
        assertEquals(expected, settleCommand.toString());
    }
}
