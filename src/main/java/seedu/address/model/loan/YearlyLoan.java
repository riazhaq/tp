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
     */
    public YearlyLoan(double currAmount, InterestRate interestRate) {
        super(currAmount, interestRate);
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
}
