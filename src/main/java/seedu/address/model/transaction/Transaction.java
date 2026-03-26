package seedu.address.model.transaction;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import seedu.address.model.person.Person;

/**
 * Represents a financial transaction between two persons, where a debtor owes
 * an amount to a creditor. Supports interest accumulation and partial payments.
 *
 * <p>The transaction tracks the current outstanding amount, an optional interest rate,
 * a description, and the date it was last recalculated. Subclasses may override
 * {@link #updateTransactionAmount()} to apply interest logic (e.g. simple or compound).
 *
 * <p>Note: {@link #equals(Object)} and {@link #hashCode()} reference {@link Person}
 * objects, but {@code Person.equals()} and {@code Person.hashCode()} intentionally
 * exclude transactions to avoid circular references.
 */
public class Transaction {

    /**
     * Error message shown when transaction field validation fails.
     * The interest rate must be between 0 and 100, and the description must be non-empty.
     */
    public static final String MESSAGE_CONSTRAINTS =
            "Interest rate must be between 0 and 100, and description must not be empty.";

    /** The current outstanding amount owed by the debtor to the creditor. */
    protected double currAmount;

    /** The interest rate applied to this transaction. */
    protected final InterestRate interestRate;

    /** A human-readable description of what this transaction is for. */
    protected String description;

    /**
     * The date on which the transaction amount was last recalculated or paid.
     * Used to determine how much interest has accrued since the last update.
     */
    protected LocalDate lastRecalculatedDate;

    /** The person who owes money in this transaction. */
    protected Person debtor;

    /** The person who is owed money in this transaction. */
    protected Person creditor;

    /**
     * Constructs a {@code Transaction} with the specified debtor, creditor, amount,
     * interest rate, and description. The last recalculated date is set to today.
     *
     * @param debtor       the person who owes the amount
     * @param creditor     the person who is owed the amount
     * @param currAmount   the initial outstanding amount
     * @param interestRate the annual interest rate as a percentage (0–100)
     * @param description  a non-empty description of the transaction
     */
    public Transaction(Person debtor, Person creditor, double currAmount, double interestRate, String description) {
        this.debtor = debtor;
        this.creditor = creditor;
        this.currAmount = currAmount;
        this.interestRate = new InterestRate(interestRate);
        this.description = description;
        this.lastRecalculatedDate = LocalDate.now();
    }

    /**
     * Updates the transaction's outstanding amount to account for accrued interest.
     *
     * <p>The base implementation is a no-op. Subclasses should override this method
     * to apply their specific interest model (e.g. simple interest, compound interest).
     */
    public void updateTransactionAmount() {
    }

    /**
     * Applies a payment to reduce the outstanding amount. If the transaction has not
     * been recalculated today, interest is first applied via {@link #updateTransactionAmount()}
     * before deducting the payment. The last recalculated date is then updated to today.
     *
     * @param amount the payment amount to deduct from the outstanding balance
     */
    public void payTransaction(double amount) {
        if (!this.lastRecalculatedDate.equals(LocalDate.now())) {
            this.updateTransactionAmount();
        }
        currAmount -= amount;
        updateLastRecalculatedDate();
    }

    /**
     * Returns the number of complete months elapsed since the transaction was
     * last recalculated or paid.
     *
     * @return the number of whole months between {@code lastRecalculatedDate} and today
     */
    public long getNumberOfMonthsSinceLastPaid() {
        return ChronoUnit.MONTHS.between(lastRecalculatedDate, LocalDate.now());
    }

    /**
     * Returns the number of complete years elapsed since the transaction was
     * last recalculated or paid.
     *
     * @return the number of whole years between {@code lastRecalculatedDate} and today
     */
    public long getNumberOfYearsSinceLastPaid() {
        return ChronoUnit.YEARS.between(lastRecalculatedDate, LocalDate.now());
    }

    /**
     * Sets {@code lastRecalculatedDate} to today's date.
     * Called after a payment or an interest recalculation to mark the transaction as current.
     */
    protected void updateLastRecalculatedDate() {
        this.lastRecalculatedDate = LocalDate.now();
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
     * Returns the interest rate associated with this transaction.
     *
     * @return the interest rate as a percentage (0–100)
     */
    public double getInterest() {
        return interestRate.getInterest();
    }

    /**
     * Returns the date on which the outstanding amount was last recalculated or paid.
     *
     * @return the last recalculated date
     */
    public LocalDate getLastRecalculatedDate() {
        return lastRecalculatedDate;
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
     * Returns a string representation of this transaction, including the current amount,
     * interest rate, description, debtor name, and creditor name.
     *
     * @return a formatted string summarising the transaction
     */
    @Override
    public String toString() {
        return String.format("[Amount: %.2f, Rate: %.2f%%, Desc: %s, Debtor: %s, Creditor: %s, Type: None]",
                currAmount, getInterest(), description, debtor.getName(), creditor.getName());
    }

    /**
     * Checks equality based on the outstanding amount, interest rate, description,
     * last recalculated date, debtor identity, and creditor identity.
     *
     * <p>Including {@code lastRecalculatedDate} ensures that two transactions which are
     * otherwise identical but were recorded on different dates are treated as distinct
     * entries, preventing incorrect removal from a {@code HashSet} when multiple
     * same-amount transactions exist for the same pair of persons.
     *
     * <p>Debtor and creditor use their own {@code equals()} implementations, which
     * intentionally exclude transactions to prevent circular references.
     *
     * @param other the object to compare against
     * @return {@code true} if both transactions have the same amount, rate,
     *         description, last recalculated date, debtor, and creditor; {@code false} otherwise
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
        return Double.compare(o.currAmount, currAmount) == 0
                && Double.compare(o.getInterest(), getInterest()) == 0
                && description.equals(o.description)
                && lastRecalculatedDate.equals(o.lastRecalculatedDate)
                && debtor.equals(o.debtor)
                && creditor.equals(o.creditor);
    }

    /**
     * Computes the hash code from the outstanding amount, interest rate, description,
     * last recalculated date, debtor, and creditor.
     *
     * <p>Debtor and creditor use their own {@code hashCode()} implementations, which
     * intentionally exclude transactions to prevent circular references.
     *
     * @return the hash code of this transaction
     */
    @Override
    public int hashCode() {
        return Objects.hash(currAmount, interestRate, description, lastRecalculatedDate, debtor, creditor);
    }
}
