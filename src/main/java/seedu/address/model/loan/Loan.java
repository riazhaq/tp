package seedu.address.model.loan;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Represents a generic loan.
 * <p>
 * A loan contains a current outstanding amount, an interest rate,
 * and the date when the loan was last recalculated.
 * Subclasses define how often interest is applied.
 * <p>
 * Positive amount  -> user owes the person.
 * Negative amount  -> the person owes the user.
 */
public class Loan {

    public static final String MESSAGE_CONSTRAINTS =
            "Loan Details should be in the form '(type) amount, interest rate, description' ";

    /** Current outstanding loan amount. */
    protected double currAmount;

    /** Interest rate applied to the loan. */
    protected final InterestRate interestRate;

    /** Description for the loan. **/
    protected String description;

    /** The last date the loan amount was recalculated. */
    protected LocalDate lastRecalculatedDate;

    /**
     * Constructs a loan.
     *
     * @param currAmount the initial loan amount
     * @param interestRate the interest rate applied to the loan
     * @param description the description for the loan
     */
    public Loan(double currAmount, double interestRate, String description) {
        this.currAmount = currAmount;
        this.interestRate = new InterestRate(interestRate);
        this.description = description;
        this.lastRecalculatedDate = LocalDate.now();
    }

    /**
     * Checks if the trimmedLoanDetails are in a valid format for it to be parsed
     *
     * @param trimmedLoanDetails the {@code String} to be parsed
     */
    public static boolean isValidLoanArguments(String trimmedLoanDetails) {
        String lowercasedLoanDetails = trimmedLoanDetails.toLowerCase();
        String loanDetailsWithoutType = trimmedLoanDetails;

        if (lowercasedLoanDetails.startsWith("m ") || lowercasedLoanDetails.startsWith("y ")) {
            loanDetailsWithoutType = trimmedLoanDetails.substring(2);
        }

        String[] parts = loanDetailsWithoutType.split("\\s*,\\s*", 3);

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
     * Updates the loan amount by applying interest
     * based on the time elapsed since the last recalculation.
     */
    public void updateLoanAmount() {

    }

    /**
     * Pays part of the loan.
     * <p>
     * If the loan has not been updated today, interest will first be applied
     * before deducting the payment.
     *
     * @param amount the amount to pay towards the loan
     */
    public void payLoan(double amount) {
        if (!this.lastRecalculatedDate.equals(LocalDate.now())) {
            this.updateLoanAmount();
        }

        currAmount -= amount;

        updateLastRecalculatedDate();
    }

    /**
     * Returns the number of months since the loan was last recalculated.
     *
     * @return number of months elapsed
     */
    public long getNumberOfMonthsSinceLastPaid() {
        LocalDate currDate = LocalDate.now();
        return ChronoUnit.MONTHS.between(lastRecalculatedDate, currDate);
    }

    /**
     * Returns the number of years since the loan was last recalculated.
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
        if (!(other instanceof Loan)) {
            return false;
        }
        Loan o = (Loan) other;
        return Double.compare(o.currAmount, currAmount) == 0
                && Double.compare(o.getInterest(), getInterest()) == 0
                && description.equals(o.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currAmount, interestRate, description);
    }
}
