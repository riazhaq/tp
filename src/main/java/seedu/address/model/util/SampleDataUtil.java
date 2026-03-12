package seedu.address.model.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.loan.Loan;
import seedu.address.model.loan.MonthlyLoan;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Utility class that provides sample data for populating the application with initial content.
 *
 * <p>This class contains helper methods to generate predefined {@link Person} objects
 * along with their associated {@link Tag} and {@link Loan} information. The sample data
 * is primarily used for initializing a default {@link AddressBook} when the application
 * is first launched or when no existing data file is found.</p>
 *
 * <p>The class also provides helper methods to construct sets of {@link Tag} and
 * {@link Loan} objects from the given inputs to simplify sample data creation.</p>
 *
 * <p>This class should not be instantiated as it only contains static utility methods.</p>
 */
public class SampleDataUtil {

    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com"),
                    new Address("Blk 30 Geylang Street 29, #06-40"),
                    getTagSet("friends"),
                    getLoanSet(new MonthlyLoan(500.0, 5.0, "Rent"))),

            new Person(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com"),
                    new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"),
                    getTagSet("colleagues", "friends"),
                    getLoanSet()), // no loans

            new Person(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com"),
                    new Address("Blk 11 Ang Mo Kio Street 74, #11-04"),
                    getTagSet("neighbours"),
                    getLoanSet()),

            new Person(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com"),
                    new Address("Blk 436 Serangoon Gardens Street 26, #16-43"),
                    getTagSet("family"),
                    getLoanSet(new MonthlyLoan(1000.0, 4.0, "Car"))),

            new Person(new Name("Irfan Ibrahim"), new Phone("92492021"), new Email("irfan@example.com"),
                    new Address("Blk 47 Tampines Street 20, #17-35"),
                    getTagSet("classmates"),
                    getLoanSet()),

            new Person(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"),
                    new Address("Blk 45 Aljunied Street 85, #11-31"),
                    getTagSet("colleagues"),
                    getLoanSet(new MonthlyLoan(150.0, 1.5, "Gym")))
        };
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
     * Returns a loan set containing the list of loans given.
     */
    public static Set<Loan> getLoanSet(Loan... loans) {
        return new HashSet<>(Arrays.asList(loans));
    }
}
