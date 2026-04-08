package seedu.address.model.transaction;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import seedu.address.model.person.Person;

/**
 * Represents a financial transaction between two persons, where a debtor owes
 * an amount to a creditor.
 *
 * <p>Note: {@link #equals(Object)} and {@link #hashCode()} are based on a unique
 * transaction ID to allow multiple identical-looking transactions between the same
 * pair of persons.
 */
public class Transaction {

    public static final String MESSAGE_CONSTRAINTS =
            "Description must not be empty.";

    /** The current outstanding amount owed by the debtor to the creditor. */
    protected double currAmount;

    /** A human-readable description of what this transaction is for. */
    protected String description;

    /** The person who owes money in this transaction. */
    protected Person debtor;

    /** The person who is owed money in this transaction. */
    protected Person creditor;

    /** Whether this transaction has been fully settled. */
    protected boolean settled;

    /** Unique identifier for each transaction. */
    protected final String transactionId;

    /** The date on which this transaction was created. */
    protected final LocalDate date;

    /**
     * Constructs a {@code Transaction} with the specified debtor, creditor, amount,
     * and description. The date is automatically set to today.
     *
     * @param debtor      the person who owes the amount
     * @param creditor    the person who is owed the amount
     * @param currAmount  the initial outstanding amount
     * @param description a non-empty description of the transaction
     */
    public Transaction(Person debtor, Person creditor, double currAmount, String description) {
        this.transactionId = UUID.randomUUID().toString();
        this.date = LocalDate.now();
        this.debtor = debtor;
        this.creditor = creditor;
        this.currAmount = currAmount;
        this.description = description;
        this.settled = false;
    }

    /**
     * Marks this transaction as fully settled and sets the outstanding amount to zero.
     */
    public void settleTransaction() {
        currAmount = 0;
        settled = true;
    }

    /**
     * Returns the current outstanding amount owed by the debtor.
     *
     * @return the current amount
     */
    public double getCurrAmount() {
        return currAmount;
    }

    /**
     * Returns the description of this transaction.
     *
     * @return a non-empty string describing the purpose of this transaction
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the date on which this transaction was created.
     *
     * @return the creation date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the debtor of this transaction — the person who owes the amount.
     *
     * @return the debtor
     */
    public Person getDebtor() {
        return debtor;
    }

    /**
     * Returns the creditor of this transaction — the person who is owed the amount.
     *
     * @return the creditor
     */
    public Person getCreditor() {
        return creditor;
    }

    /**
     * Returns whether this transaction has been fully settled.
     */
    public boolean isSettled() {
        return settled;
    }

    /**
     * Updates the settled state. Primarily used by storage during deserialisation.
     */
    public void setSettled(boolean settled) {
        this.settled = settled;
    }

    /**
     * Updates the debtor reference to the given person.
     * Used when a Person is edited to keep transaction references consistent.
     */
    public void setDebtor(Person debtor) {
        requireNonNull(debtor);
        this.debtor = debtor;
    }

    /**
     * Updates the creditor reference to the given person.
     * Used when a Person is edited to keep transaction references consistent.
     */
    public void setCreditor(Person creditor) {
        requireNonNull(creditor);
        this.creditor = creditor;
    }

    /**
     * Returns a string representation of this transaction.
     *
     * @return a formatted string summarising the transaction
     */
    @Override
    public String toString() {
        return String.format("[Amount: %.2f, Desc: %s, Date: %s, Debtor: %s, Creditor: %s]",
                currAmount, description, date, debtor.getName(), creditor.getName());
    }

    /**
     * Checks equality based on the unique transaction ID.
     *
     * @param other the object to compare against
     * @return {@code true} if both transactions share the same ID
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Transaction)) {
            return false;
        }
        Transaction o = (Transaction) other;
        return transactionId.equals(o.transactionId);
    }

    /**
     * Computes the hash code from the unique transaction ID.
     *
     * @return the hash code of this transaction
     */
    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
}
