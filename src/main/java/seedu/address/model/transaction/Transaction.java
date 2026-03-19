package seedu.address.model.transaction;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Represents a financial transaction associated with a person.
 * A transaction consists of a current amount, an interest rate, a description,
 * and the date when the transaction was last recalculated.
 *
 * <p>
 * The transaction supports basic operations such as validating input format,
 * applying payments, and tracking elapsed time since the last update.
 * </p>
 */
public class Transaction {

    public static final String MESSAGE_CONSTRAINTS =
            "Transaction Details should be in the form '(type) amount, interest rate, description' ";

    /** Current outstanding transaction amount. */
    protected double currAmount;

    /** Interest rate applied to the transaction. */
    protected final InterestRate interestRate;

    /** Description for the transaction. **/
    protected String description;

    /** The last date the transaction amount was recalculated. */
    protected LocalDate lastRecalculatedDate;

    /**
     * Constructs a transaction.
     *
     * @param currAmount the initial transaction amount
     * @param interestRate the interest rate applied to the transaction
     * @param description the description for the transaction
     */
    public Transaction(double currAmount, double interestRate, String description) {
        this.currAmount = currAmount;
        this.interestRate = new InterestRate(interestRate);
        this.description = description;
        this.lastRecalculatedDate = LocalDate.now();
    }

    /**
     * Returns true if the given string is in a valid format for a transaction.
     * The expected format is "(type) amount, interest rate, description".
     *
     * @param trimmedTransactionDetails The string to validate.
     * @return True if the input is valid, false otherwise.
     */
    public static boolean isValidTransactionArguments(String trimmedTransactionDetails) {
        String lowercasedTransactionDetails = trimmedTransactionDetails.toLowerCase();
        String transactionDetailsWithoutType = trimmedTransactionDetails;

        if (lowercasedTransactionDetails.startsWith("m ") || lowercasedTransactionDetails.startsWith("y ")) {
            transactionDetailsWithoutType = trimmedTransactionDetails.substring(2);
        }

        String[] parts = transactionDetailsWithoutType.split("\\s*,\\s*", 3);

        if (parts.length != 3) {
            return false;
        }

        try {
            double amount = Double.parseDouble(parts[0]);
            double rate = Double.parseDouble(parts[1]);
            String description = parts[2];

            return rate <= 100 && rate >= 0 && !description.isEmpty();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Updates the transaction amount by applying interest
     * based on the time elapsed since the last recalculation.
     */
    public void updateTransactionAmount() {

    }

    /**
     * Pays part of the transaction.
     * <p>
     * If the transaction has not been updated today, interest will first be applied
     * before deducting the payment.
     *
     * @param amount the amount to pay towards the transaction
     */
    public void payTransaction(double amount) {
        if (!this.lastRecalculatedDate.equals(LocalDate.now())) {
            this.updateTransactionAmount();
        }

        currAmount -= amount;

        updateLastRecalculatedDate();
    }

    /**
     * Returns the number of months since the transaction was last recalculated.
     *
     * @return number of months elapsed
     */
    public long getNumberOfMonthsSinceLastPaid() {
        LocalDate currDate = LocalDate.now();
        return ChronoUnit.MONTHS.between(lastRecalculatedDate, currDate);
    }

    /**
     * Returns the number of years since the transaction was last recalculated.
     *
     * @return number of years elapsed
     */
    public long getNumberOfYearsSinceLastPaid() {
        LocalDate currDate = LocalDate.now();
        return ChronoUnit.YEARS.between(lastRecalculatedDate, currDate);
    }

    /**
     * Updates the last recalculated date to the current date.
     */
    protected void updateLastRecalculatedDate() {
        this.lastRecalculatedDate = LocalDate.now();
    }

    public double getCurrAmount() {
        return currAmount;
    }

    public String getDescription() {
        return description;
    }

    public double getInterest() {
        return interestRate.getInterest();
    }

    public LocalDate getLastRecalculatedDate() {
        return lastRecalculatedDate;
    }

    @Override
    public String toString() {
        return String.format("[Amount: %.2f, Rate: %.2f%%, Desc: %s]",
                currAmount, getInterest(), description);
    }

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
                && description.equals(o.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currAmount, interestRate, description);
    }
}

