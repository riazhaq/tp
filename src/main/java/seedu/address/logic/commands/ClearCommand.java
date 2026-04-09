package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.util.SampleDataUtil;

/**
 * Clears the address book while retaining the special {@code Me} contact.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Clears all entries in the address book except the Me contact.";
    public static final String MESSAGE_SUCCESS = "Address book has been cleared!";


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setAddressBook(clearKeepingMe(model.getAddressBook()));
        return new CommandResult(MESSAGE_SUCCESS);
    }

    private static AddressBook clearKeepingMe(ReadOnlyAddressBook addressBook) {
        AddressBook cleared = new AddressBook();
        Person meContact = addressBook.getPersonList().stream()
                .filter(ClearCommand::isMeContact)
                .findFirst()
                .map(ClearCommand::withoutTransactions)
                .orElseGet(SampleDataUtil::getMeContact);
        cleared.addPersonAtFront(meContact);
        return cleared;
    }

    private static boolean isMeContact(Person person) {
        return person.getName().fullName.equalsIgnoreCase(SampleDataUtil.ME_NAME);
    }

    private static Person withoutTransactions(Person person) {
        return new Person(
                person.getName(),
                person.getPhone(),
                person.getEmail(),
                person.getAddress(),
                person.getTags(),
                Set.of());
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ClearCommand;
    }
}
