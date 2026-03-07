package seedu.address.model.loan;

import java.time.LocalDate;

public class MonthlyLoan extends Loan{
    public MonthlyLoan(double currAmount, InterestRate interestRate) {
        super(currAmount, interestRate);
    }

    @Override
    public void updateLoanAmount() {
        long months = getNumberOfMonthsSinceLastPaid();

        currAmount = interestRate.applyInterestRate(currAmount, months);

        updateLastRecalculatedDate();
    }

}
