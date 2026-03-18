package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;

import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.model.transaction.Transaction;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to build {@link Person} objects for tests.
 * <p>
 * This builder provides a convenient way to construct {@code Person} instances
 * with default values that can be customised using the provided {@code withX()}
 * methods. It is mainly used in test cases to reduce boilerplate code when
 * creating {@code Person} objects.
 */
public class PersonBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";

    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private Set<Tag> tags;
    private Set<Transaction> transactions;

    /**
     * Creates a {@code PersonBuilder} with default details.
     */
    public PersonBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        tags = new HashSet<>();
        transactions = new HashSet<>(); // default empty
    }

    /**
     * Initializes the {@code PersonBuilder} with the data of the given {@code Person}.
     *
     * @param personToCopy The person whose data will be used to initialise the builder.
     */
    public PersonBuilder(Person personToCopy) {
        name = personToCopy.getName();
        phone = personToCopy.getPhone();
        email = personToCopy.getEmail();
        address = personToCopy.getAddress();
        tags = new HashSet<>(personToCopy.getTags());
        transactions = new HashSet<>(personToCopy.getTransactions());
    }

    /**
     * Sets the {@link Name} of the {@code Person} to be built.
     *
     * @param name The name of the person.
     * @return This builder instance for chaining.
     */
    public PersonBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Sets the {@link Phone} of the {@code Person} to be built.
     *
     * @param phone The phone number of the person.
     * @return This builder instance for chaining.
     */
    public PersonBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@link Email} of the {@code Person} to be built.
     *
     * @param email The email address of the person.
     * @return This builder instance for chaining.
     */
    public PersonBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Sets the {@link Address} of the {@code Person} to be built.
     *
     * @param address The address of the person.
     * @return This builder instance for chaining.
     */
    public PersonBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Parses the provided tags and sets them for the {@code Person} to be built.
     *
     * @param tags The tags associated with the person.
     * @return This builder instance for chaining.
     */
    public PersonBuilder withTags(String... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@link Transaction}s associated with the {@code Person}.
     *
     * @param transactions The transactions to assign to the person.
     * @return This builder instance for chaining.
     */
    public PersonBuilder withTransactions(Transaction... transactions) {
        this.transactions = new HashSet<>();
        for (Transaction transaction : transactions) {
            this.transactions.add(transaction);
        }
        return this;
    }

    /**
     * Builds and returns a {@link Person} instance using the current state of the builder.
     *
     * @return A new {@code Person} object.
     */
    public Person build() {
        return new Person(name, phone, email, address, tags, transactions);
    }
}
