package seedu.address.model.loan;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class LoanTest {

    private static class TestLoan extends Loan {

        public TestLoan(double amount, InterestRate rate) {
            super(amount, rate);
        }

        @Override
        public void updateLoanAmount() {
            // simple implementation for testing
            currAmount += 10;
        }
    }

    @Test
    public void payLoan_reducesAmount() {
        Loan loan = new TestLoan(100, new InterestRate(5));

        loan.payLoan(20);

        assertEquals(80, loan.currAmount, 0.001);
    }

    @Test
    public void payLoan_updatesLoanBeforePayment() {
        Loan loan = new TestLoan(100, new InterestRate(5));

        loan.lastRecalculatedDate = loan.lastRecalculatedDate.minusDays(1);

        loan.payLoan(20);

        // updateLoanAmount adds 10 then subtract 20
        assertEquals(90, loan.currAmount, 0.001);
    }

    @Test
    public void getNumberOfMonthsSinceLastPaid() {
        Loan loan = new TestLoan(100, new InterestRate(5));

        loan.lastRecalculatedDate = loan.lastRecalculatedDate.minusMonths(3);

        assertEquals(3, loan.getNumberOfMonthsSinceLastPaid());
    }

    @Test
    public void getNumberOfYearsSinceLastPaid() {
        Loan loan = new TestLoan(100, new InterestRate(5));

        loan.lastRecalculatedDate = loan.lastRecalculatedDate.minusYears(2);

        assertEquals(2, loan.getNumberOfYearsSinceLastPaid());
    }
}
