package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.TransactionDescriptor;
import seedu.address.model.transaction.TransactionDescriptor.CompoundingType;

public class AddTransactionCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_sameCommandRepeated_feedbackShowsIncrementingTransactionNumber() throws Exception {
        Person debtor = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person creditor = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        TransactionDescriptor descriptor = new TransactionDescriptor(CompoundingType.NONE, 12.50, 0.0, "Dinner");

        AddTransactionCommand first = new AddTransactionCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON, descriptor);
        CommandResult firstResult = first.execute(model);
        assertEquals(String.format(AddTransactionCommand.MESSAGE_SUCCESS_WITH_TRANSACTION_NUMBER,
                1L, Messages.format(debtor), Messages.format(creditor), descriptor.getDescription()),
                firstResult.getFeedbackToUser());

        AddTransactionCommand second = new AddTransactionCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON, descriptor);
        CommandResult secondResult = second.execute(model);
        assertEquals(String.format(AddTransactionCommand.MESSAGE_SUCCESS_WITH_TRANSACTION_NUMBER,
                2L, Messages.format(debtor), Messages.format(creditor), descriptor.getDescription()),
                secondResult.getFeedbackToUser());
    }
}

