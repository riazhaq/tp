package seedu.address.model.loan;

/**
 * Represents a loan where interest is compounded yearly.
 */
public class YearlyLoan extends Loan {

    /**
     * Constructs a YearlyLoan.
     *
     * @param currAmount the initial loan amount
     * @param interestRate the interest rate applied yearly
     * @param description the description for the loan
     */
    public YearlyLoan(double currAmount, double interestRate, String description) {
        super(currAmount, interestRate, description);
    }

    /**
     * Updates the loan amount by applying interest for each
     * year that has passed since the last recalculation.
     */
    @Override
    public void updateLoanAmount() {
        long years = getNumberOfYearsSinceLastPaid();

        currAmount = interestRate.applyInterestRate(currAmount, years);

        updateLastRecalculatedDate();
    }

    @Override
    public String toString() {
        return String.format("[Amount: %.2f, Rate: %.2f%%, Desc: %s, Type: Yearly]",
                currAmount, getInterest(), description);
    }
}
