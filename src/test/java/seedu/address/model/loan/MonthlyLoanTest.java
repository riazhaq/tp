package seedu.address.model.loan;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MonthlyLoanTest {

    @Test
    public void updateLoanAmount_appliesMonthlyInterest() {
        MonthlyLoan loan = new MonthlyLoan(1000, new InterestRate(10), "test loan");

        loan.lastRecalculatedDate = loan.lastRecalculatedDate.minusMonths(2);

        loan.updateLoanAmount();

        // 1000 * (1.1)^2 = 1210
        assertEquals(1210, loan.currAmount, 0.01);
    }

    @Test
    public void updateLoanAmount_noMonthsPassed_noChange() {
        MonthlyLoan loan = new MonthlyLoan(1000, new InterestRate(10), "test loan");

        loan.updateLoanAmount();

        assertEquals(1000, loan.currAmount, 0.01);
    }
}
