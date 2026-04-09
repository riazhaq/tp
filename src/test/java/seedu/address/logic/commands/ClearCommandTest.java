package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.Transaction;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.testutil.PersonBuilder;

public class ClearCommandTest {

    @Test
    public void execute_emptyAddressBook_success() {
        Model model = new ModelManager();
        AddressBook clearedAddressBook = new AddressBook();
        clearedAddressBook.addPersonAtFront(SampleDataUtil.getMeContact());
        Model expectedModel = new ModelManager(clearedAddressBook, new UserPrefs());

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyAddressBook_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        AddressBook clearedAddressBook = new AddressBook();
        clearedAddressBook.addPersonAtFront(SampleDataUtil.getMeContact());
        Model expectedModel = new ModelManager(clearedAddressBook, new UserPrefs());

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_meContactWithTransactions_keepsMeWithoutTransactions() throws Exception {
        Person me = SampleDataUtil.getMeContact();
        Person alice = new PersonBuilder().withName("Alice Tan").build();
        Transaction transaction = new Transaction(me, alice, 10.0, "Lunch");
        me.appendTransaction(transaction);
        alice.appendTransaction(transaction);

        AddressBook addressBook = new AddressBook();
        addressBook.addPersonAtFront(me);
        addressBook.addPerson(alice);

        Model model = new ModelManager(addressBook, new UserPrefs());

        CommandResult result = new ClearCommand().execute(model);

        assertEquals(ClearCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(1, model.getAddressBook().getPersonList().size());

        Person remainingMe = model.getAddressBook().getPersonList().get(0);
        assertEquals(SampleDataUtil.getMeContact(), remainingMe);
        assertEquals(Set.of(), remainingMe.getTransactions());
    }

}
