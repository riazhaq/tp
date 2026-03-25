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
     * Constructs a {@code Person} without any transactions.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, address, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
    }

    /**
     * Constructs a {@code Person} with an initial set of transactions.
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

    /**
     * Appends a transaction to this person's transaction set.
     */
    public void appendTransaction(Transaction transaction) {
        requireAllNonNull(transaction);
        transactions.add(transaction);
    }

    /**
     * Removes a transaction from this person's transaction set.
     * Does nothing if the transaction is not present.
     */
    public void deleteTransaction(Transaction transaction) {
        requireAllNonNull(transaction);
        transactions.remove(transaction);
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

    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public Set<Transaction> getTransactions() {
        return Collections.unmodifiableSet(transactions);
    }

    /**
     * Returns true if {@code otherPerson} is considered the same real-world individual
     * as this person, based solely on a case-insensitive, whitespace-normalised name comparison.
     *
     * <p>This is a weaker notion of equality than {@link #equals(Object)}, which requires
     * all identity fields (name, phone, email, address, tags) to match. Use this method
     * to guard against duplicate entries in the address book; use {@link #equals(Object)}
     * for structural equality checks in collections and tests.
     *
     * <p>Name normalisation trims leading/trailing whitespace and collapses any internal
     * sequences of whitespace to a single space, so {@code "John  Doe"} and {@code "john doe"}
     * are considered the same person.
     *
     * @param otherPerson the person to compare against; may be {@code null},
     *                    in which case {@code false} is returned
     * @return {@code true} if both persons share the same normalised, case-insensitive name;
     *         {@code false} otherwise
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

    private String normaliseName(String name) {
        String trimmed = name.trim();
        return MULTIPLE_WHITESPACE.matcher(trimmed).replaceAll(" ");
    }

    /**
     * Equality is based on identity fields only (name, phone, email, address, tags).
     * Transactions are excluded to prevent circular hashCode/equals calls between
     * Person and Transaction.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Person)) {
            return false;
        }
        Person otherPerson = (Person) other;
        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && tags.equals(otherPerson.tags);
    }

    /**
     * Transactions are excluded from hashCode to prevent infinite recursion with
     * Transaction.hashCode(), which references Person.hashCode() via debtor/creditor.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, phone, email, address, tags);
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
