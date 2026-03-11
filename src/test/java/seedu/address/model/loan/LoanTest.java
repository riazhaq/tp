package seedu.address.model.loan;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LoanTest {

    private static class TestLoan extends Loan {

        public TestLoan(double amount, double rate, String description) {
            super(amount, rate, description);
        }

        @Override
        public void updateLoanAmount() {
            // simple implementation for testing
            currAmount += 10;
        }
    }

    @Test
    public void payLoan_reducesAmount() {
        Loan loan = new TestLoan(100, 5, "test loan");

        loan.payLoan(20);

        assertEquals(80, loan.getCurrAmount(), 0.001);
    }

    @Test
    public void payLoan_updatesLoanBeforePayment() {
        Loan loan = new TestLoan(100, 5, "test loan");

        loan.lastRecalculatedDate = loan.lastRecalculatedDate.minusDays(1);

        loan.payLoan(20);

        // updateLoanAmount adds 10 then subtract 20
        assertEquals(90, loan.getCurrAmount(), 0.001);
    }

    @Test
    public void getNumberOfMonthsSinceLastPaid() {
        Loan loan = new TestLoan(100, 5, "test loan");

        loan.lastRecalculatedDate = loan.lastRecalculatedDate.minusMonths(3);

        assertEquals(3, loan.getNumberOfMonthsSinceLastPaid());
    }

    @Test
    public void getNumberOfYearsSinceLastPaid() {
        Loan loan = new TestLoan(100, 5, "test loan");

        loan.lastRecalculatedDate = loan.lastRecalculatedDate.minusYears(2);

        assertEquals(2, loan.getNumberOfYearsSinceLastPaid());
    }
}
