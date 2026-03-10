package seedu.address.model.loan;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class YearlyLoanTest {

    @Test
    public void updateLoanAmount_appliesYearlyInterest() {
        YearlyLoan loan = new YearlyLoan(1000, new InterestRate(10));

        loan.lastRecalculatedDate = loan.lastRecalculatedDate.minusYears(2);

        loan.updateLoanAmount();

        // 1000 * (1.1)^2 = 1210
        assertEquals(1210, loan.currAmount, 0.01);
    }

    @Test
    public void updateLoanAmount_noYearsPassed_noChange() {
        YearlyLoan loan = new YearlyLoan(1000, new InterestRate(10));

        loan.updateLoanAmount();

        assertEquals(1000, loan.currAmount, 0.01);
    }
}
