package seedu.address.model.loan;

/**
 * Represents a loan where interest is compounded monthly.
 */
public class MonthlyLoan extends Loan {

    /**
     * Constructs a MonthlyLoan.
     *
     * @param currAmount the initial loan amount
     * @param interestRate the interest rate applied monthly
     */
    public MonthlyLoan(double currAmount, InterestRate interestRate, String description) {
        super(currAmount, interestRate, description);
    }

    /**
     * Updates the loan amount by applying interest for each
     * month that has passed since the last recalculation.
     */
    @Override
    public void updateLoanAmount() {
        long months = getNumberOfMonthsSinceLastPaid();

        currAmount = interestRate.applyInterestRate(currAmount, months);

        updateLastRecalculatedDate();
    }
}
