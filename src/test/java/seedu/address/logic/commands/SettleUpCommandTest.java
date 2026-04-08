package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.Transaction;

public class SettleUpCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_unsettledInGroupTransactions_settlesAll() throws Exception {
        Person a = model.getFilteredPersonList().get(0);
        Person b = model.getFilteredPersonList().get(1);
        Person c = model.getFilteredPersonList().get(2);

        Transaction t1 = new Transaction(a, b, 10.0, 0.0, "a->b");
        Transaction t2 = new Transaction(b, c, 6.0, 0.0, "b->c");

        a.appendTransaction(t1);
        b.appendTransaction(t1);
        b.appendTransaction(t2);
        c.appendTransaction(t2);

        SettleUpCommand command = new SettleUpCommand(List.of(
                Index.fromOneBased(1), Index.fromOneBased(2), Index.fromOneBased(3)));

        CommandResult result = command.execute(model);

        assertTrue(t1.isSettled());
        assertTrue(t2.isSettled());
        assertTrue(result.getFeedbackToUser().contains("2 transaction(s) settled"));
    }

    @Test
    public void execute_allAlreadySettled_returnsNothingToSettle() throws Exception {
        Person a = model.getFilteredPersonList().get(0);
        Person b = model.getFilteredPersonList().get(1);
        Person c = model.getFilteredPersonList().get(2);

        Transaction t1 = new Transaction(a, b, 10.0, 0.0, "a->b");
        t1.settleTransaction();
        a.appendTransaction(t1);
        b.appendTransaction(t1);

        SettleUpCommand command = new SettleUpCommand(List.of(
                Index.fromOneBased(1), Index.fromOneBased(2), Index.fromOneBased(3)));

        CommandResult result = command.execute(model);
        assertTrue(result.getFeedbackToUser().contains(SettleUpCommand.MESSAGE_NOTHING_TO_SETTLE));
    }

    @Test
    public void execute_outOfGroupTransactionNotSettled() throws Exception {
        Person a = model.getFilteredPersonList().get(0);
        Person b = model.getFilteredPersonList().get(1);
        Person c = model.getFilteredPersonList().get(2);
        Person d = model.getFilteredPersonList().get(3);

        Transaction t1 = new Transaction(a, b, 10.0, 0.0, "a->b");
        Transaction t2 = new Transaction(a, d, 5.0, 0.0, "a->d");

        a.appendTransaction(t1);
        b.appendTransaction(t1);
        a.appendTransaction(t2);
        d.appendTransaction(t2);

        SettleUpCommand command = new SettleUpCommand(List.of(
                Index.fromOneBased(1), Index.fromOneBased(2), Index.fromOneBased(3)));

        command.execute(model);

        assertTrue(t1.isSettled());
        assertFalse(t2.isSettled());
    }

    @Test
    public void execute_outOfBoundsIndex_throwsCommandException() {
        SettleUpCommand command = new SettleUpCommand(List.of(
                Index.fromOneBased(1),
                Index.fromOneBased(2),
                Index.fromOneBased(model.getFilteredPersonList().size() + 1)));

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_duplicateIndices_throwsCommandException() {
        SettleUpCommand command = new SettleUpCommand(List.of(
                Index.fromOneBased(1),
                Index.fromOneBased(2),
                Index.fromOneBased(2)));

        assertCommandFailure(command, model,
                String.format(SettleUpCommand.MESSAGE_DUPLICATE_PERSON_INDEX, 2));
    }

    @Test
    public void execute_lessThanThreeParticipants_throwsCommandException() {
        SettleUpCommand command = new SettleUpCommand(List.of(
                Index.fromOneBased(1),
                Index.fromOneBased(2)));

        assertCommandFailure(command, model, SettleUpCommand.MESSAGE_MINIMUM_PARTICIPANTS);
    }

    @Test
    public void equals() {
        SettleUpCommand first = new SettleUpCommand(List.of(
                Index.fromOneBased(1), Index.fromOneBased(2), Index.fromOneBased(3)));
        SettleUpCommand second = new SettleUpCommand(List.of(
                Index.fromOneBased(1), Index.fromOneBased(2), Index.fromOneBased(3)));
        SettleUpCommand different = new SettleUpCommand(List.of(
                Index.fromOneBased(1), Index.fromOneBased(2), Index.fromOneBased(4)));

        assertTrue(first.equals(first));
        assertTrue(first.equals(second));
        assertFalse(first.equals(different));
    }
}
