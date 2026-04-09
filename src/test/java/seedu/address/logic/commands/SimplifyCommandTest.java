package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.Transaction;

public class SimplifyCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validParticipants_buildsSettlementPreview() throws Exception {
        Person a = model.getFilteredPersonList().get(0);
        Person b = model.getFilteredPersonList().get(1);
        Person c = model.getFilteredPersonList().get(2);

        Transaction t1 = new Transaction(a, b, 10.0, "a->b");
        Transaction t2 = new Transaction(b, c, 6.0, "b->c");

        a.appendTransaction(t1);
        b.appendTransaction(t1);
        b.appendTransaction(t2);
        c.appendTransaction(t2);

        SimplifyCommand command = new SimplifyCommand(List.of(
                Index.fromOneBased(1), Index.fromOneBased(2), Index.fromOneBased(3)));

        CommandResult result = command.execute(model);

        assertTrue(result.getFeedbackToUser().contains("Simplified settlement plan (3 persons):"));
        assertTrue(result.getFeedbackToUser().contains("pays"));
        assertTrue(result.getFeedbackToUser().contains("$"));
    }

    @Test
    public void execute_groupAlreadyNetted_showsNoPaymentsNeeded() throws Exception {
        Person a = model.getFilteredPersonList().get(0);
        Person b = model.getFilteredPersonList().get(1);
        Person c = model.getFilteredPersonList().get(2);

        Transaction t1 = new Transaction(a, b, 10.0, "a->b");
        Transaction t2 = new Transaction(b, c, 10.0, "b->c");
        Transaction t3 = new Transaction(c, a, 10.0, "c->a");

        a.appendTransaction(t1);
        b.appendTransaction(t1);
        b.appendTransaction(t2);
        c.appendTransaction(t2);
        c.appendTransaction(t3);
        a.appendTransaction(t3);

        SimplifyCommand command = new SimplifyCommand(List.of(
                Index.fromOneBased(1), Index.fromOneBased(2), Index.fromOneBased(3)));

        CommandResult result = command.execute(model);
        assertTrue(result.getFeedbackToUser().contains("No payments needed among selected persons."));
    }

    @Test
    public void execute_outOfBoundsIndex_throwsCommandException() {
        SimplifyCommand command = new SimplifyCommand(List.of(
                Index.fromOneBased(1),
                Index.fromOneBased(2),
                Index.fromOneBased(model.getFilteredPersonList().size() + 1)));

        assertCommandFailure(command, model,
                String.format(SimplifyCommand.MESSAGE_INVALID_PERSON_INDEX,
                        model.getFilteredPersonList().size() + 1));
    }

    @Test
    public void execute_duplicateIndices_throwsCommandException() {
        SimplifyCommand command = new SimplifyCommand(List.of(
                Index.fromOneBased(1),
                Index.fromOneBased(2),
                Index.fromOneBased(2)));

        assertCommandFailure(command, model,
                String.format(SimplifyCommand.MESSAGE_DUPLICATE_PERSON_INDEX, 2));
    }

    @Test
    public void execute_lessThanThreeParticipants_throwsCommandException() {
        SimplifyCommand command = new SimplifyCommand(List.of(
                Index.fromOneBased(1),
                Index.fromOneBased(2)));

        assertCommandFailure(command, model, SimplifyCommand.MESSAGE_MINIMUM_PARTICIPANTS);
    }

    @Test
    public void equals() {
        SimplifyCommand first = new SimplifyCommand(List.of(
                Index.fromOneBased(1), Index.fromOneBased(2), Index.fromOneBased(3)));
        SimplifyCommand second = new SimplifyCommand(List.of(
                Index.fromOneBased(1), Index.fromOneBased(2), Index.fromOneBased(3)));
        SimplifyCommand different = new SimplifyCommand(List.of(
                Index.fromOneBased(1), Index.fromOneBased(2), Index.fromOneBased(4)));

        assertTrue(first.equals(first));
        assertTrue(first.equals(second));
        assertFalse(first.equals(different));
    }
}
