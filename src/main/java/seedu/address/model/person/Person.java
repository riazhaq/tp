package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;
import seedu.address.model.transaction.Transaction;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {
    private static final Pattern MULTIPLE_WHITESPACE = Pattern.compile("\\s+");

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();
    private final Set<Transaction> transactions = new HashSet<>();

    /**
     * Constructs a {@code Person} with the specified details.
     *
     * @param name The person's name.
     * @param phone The person's phone number.
     * @param email The person's email address.
     * @param address The person's address.
     * @param tags The set of tags associated with the person.
     * @param transactions The set of transactions associated with the person.
     * @throws NullPointerException if any argument is null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags, Set<Transaction> transactions) {
        requireAllNonNull(name, phone, email, address, tags, transactions);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        this.transactions.addAll(transactions);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns an immutable transaction set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Transaction> getTransactions() {
        return Collections.unmodifiableSet(transactions);
    }

    /**
     * Returns an unmodifiable view of the transactions set.
     * Attempts to modify the returned set will result in an {@code UnsupportedOperationException}.
     *
     * @return An unmodifiable set of transactions.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        if (otherPerson == null) {
            return false;
        }

        String thisNameNormalised = normaliseName(getName().toString());
        String otherNameNormalised = normaliseName(otherPerson.getName().toString());

        return thisNameNormalised.equalsIgnoreCase(otherNameNormalised);
    }

    /**
     * Normalises a name for identity comparison by trimming leading and trailing whitespace
     * and collapsing multiple internal whitespace characters into a single space.
     *
     * @param name The name string to normalise.
     * @return The normalised name string.
     */
    private String normaliseName(String name) {
        String trimmed = name.trim();
        return MULTIPLE_WHITESPACE.matcher(trimmed).replaceAll(" ");
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     *
     * @param other The object to compare against.
     * @return True if both persons are equal, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person)) {
            return false;
        }

        Person otherPerson = (Person) other;
        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && tags.equals(otherPerson.tags)
                && transactions.equals(otherPerson.transactions);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, tags, transactions);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .add("transactions", transactions)
                .toString();
    }

}
