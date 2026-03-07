package seedu.address.model.loan;

import java.time.LocalDate;
import java.util.Objects;

public class YearlyLoan extends Loan{
    public YearlyLoan(double currAmount, InterestRate interestRate) {
        super(currAmount, interestRate);
    }

    @Override
    public void updateLoanAmount() {
        long years = getNumberOfYearsSinceLastPaid();

        currAmount = interestRate.applyInterestRate(currAmount, years);

        updateLastRecalculatedDate();
    }

}
