package seedu.address.model.loan;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a generic loan.
 * <p>
 * A loan contains a current outstanding amount, an interest rate,
 * and the date when the loan was last recalculated.
 * Subclasses define how often interest is applied.
 */
public abstract class Loan {

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
    public Loan(double currAmount, InterestRate interestRate, String description) {
        this.currAmount = currAmount;
        this.interestRate = interestRate;
        this.description = description;
        this.lastRecalculatedDate = LocalDate.now();
    }

    /**
     * Updates the loan amount by applying interest
     * based on the time elapsed since the last recalculation.
     */
    public abstract void updateLoanAmount();

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
}
