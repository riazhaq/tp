package seedu.address.model.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.model.transaction.MonthlyTransaction;
import seedu.address.model.transaction.Transaction;

/**
 * Utility class that provides sample data for populating the application with initial content.
 */
public class SampleDataUtil {

    public static Person[] getSamplePersons() {
        // Construct persons first with no transactions
        Person alex = new Person(new Name("Alex Yeoh"), new Phone("87438807"),
                new Email("alexyeoh@example.com"), new Address("Blk 30 Geylang Street 29, #06-40"),
                getTagSet("friends"));

        Person bernice = new Person(new Name("Bernice Yu"), new Phone("99272758"),
                new Email("berniceyu@example.com"), new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"),
                getTagSet("colleagues", "friends"));

        Person charlotte = new Person(new Name("Charlotte Oliveiro"), new Phone("93210283"),
                new Email("charlotte@example.com"), new Address("Blk 11 Ang Mo Kio Street 74, #11-04"),
                getTagSet("neighbours"));

        Person david = new Person(new Name("David Li"), new Phone("91031282"),
                new Email("lidavid@example.com"), new Address("Blk 436 Serangoon Gardens Street 26, #16-43"),
                getTagSet("family"));

        Person irfan = new Person(new Name("Irfan Ibrahim"), new Phone("92492021"),
                new Email("irfan@example.com"), new Address("Blk 47 Tampines Street 20, #17-35"),
                getTagSet("classmates"));

        Person roy = new Person(new Name("Roy Balakrishnan"), new Phone("92624417"),
                new Email("royb@example.com"), new Address("Blk 45 Aljunied Street 85, #11-31"),
                getTagSet("colleagues"));

        // Now construct transactions with both persons available
        Transaction alexOwesbernice = new MonthlyTransaction(alex, bernice, 500.0, 5.0, "Rent");
        alex.appendTransaction(alexOwesbernice);
        bernice.appendTransaction(alexOwesbernice);

        Transaction davidOwesCharlotte = new MonthlyTransaction(david, charlotte, 1000.0, 4.0, "Car");
        david.appendTransaction(davidOwesCharlotte);
        charlotte.appendTransaction(davidOwesCharlotte);

        Transaction royOwesIrfan = new MonthlyTransaction(roy, irfan, 150.0, 1.5, "Gym");
        roy.appendTransaction(royOwesIrfan);
        irfan.appendTransaction(royOwesIrfan);

        return new Person[] { alex, bernice, charlotte, david, irfan, roy };
    }

    /**
     * Returns a {@code Person} representing the user ("me") to always appear
     * first in the address book.
     */
    public static Person getMeContact() {
        return new Person(
                new Name("Me"),
                new Phone("00000000"),
                new Email("me@example.com"),
                new Address("My Address"),
                new HashSet<>(),
                new HashSet<>());
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a transaction set containing the list of transactions given.
     */
    public static Set<Transaction> getTransactionSet(Transaction... transactions) {
        return new HashSet<>(Arrays.asList(transactions));
    }
}
