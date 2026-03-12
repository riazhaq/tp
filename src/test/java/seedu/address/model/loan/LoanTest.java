package seedu.address.model.loan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    // ========== payLoan ==========

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
        // updateLoanAmount adds 10, then subtract 20
        assertEquals(90, loan.getCurrAmount(), 0.001);
    }

    @Test
    public void payLoan_sameDayNoUpdate() {
        // lastRecalculatedDate is today by default, so updateLoanAmount should NOT be called
        Loan loan = new TestLoan(100, 5, "test loan");
        loan.payLoan(20);
        // no +10 from updateLoanAmount, just -20
        assertEquals(80, loan.getCurrAmount(), 0.001);
    }

    @Test
    public void payLoan_fullAmount_resultIsZero() {
        Loan loan = new TestLoan(100, 5, "test loan");
        loan.payLoan(100);
        assertEquals(0, loan.getCurrAmount(), 0.001);
    }

    @Test
    public void payLoan_overpay_resultIsNegative() {
        Loan loan = new TestLoan(100, 5, "test loan");
        loan.payLoan(150);
        assertEquals(-50, loan.getCurrAmount(), 0.001);
    }

    // ========== getNumberOfMonthsSinceLastPaid ==========

    @Test
    public void getNumberOfMonthsSinceLastPaid_threeMonths() {
        Loan loan = new TestLoan(100, 5, "test loan");
        loan.lastRecalculatedDate = loan.lastRecalculatedDate.minusMonths(3);
        assertEquals(3, loan.getNumberOfMonthsSinceLastPaid());
    }

    @Test
    public void getNumberOfMonthsSinceLastPaid_today_returnsZero() {
        Loan loan = new TestLoan(100, 5, "test loan");
        assertEquals(0, loan.getNumberOfMonthsSinceLastPaid());
    }

    // ========== getNumberOfYearsSinceLastPaid ==========

    @Test
    public void getNumberOfYearsSinceLastPaid_twoYears() {
        Loan loan = new TestLoan(100, 5, "test loan");
        loan.lastRecalculatedDate = loan.lastRecalculatedDate.minusYears(2);
        assertEquals(2, loan.getNumberOfYearsSinceLastPaid());
    }

    @Test
    public void getNumberOfYearsSinceLastPaid_today_returnsZero() {
        Loan loan = new TestLoan(100, 5, "test loan");
        assertEquals(0, loan.getNumberOfYearsSinceLastPaid());
    }

    // ========== Getters ==========

    @Test
    public void getCurrAmount_returnsCorrectAmount() {
        Loan loan = new TestLoan(250.5, 3, "desc");
        assertEquals(250.5, loan.getCurrAmount(), 0.001);
    }

    @Test
    public void getDescription_returnsCorrectDescription() {
        Loan loan = new TestLoan(100, 5, "my description");
        assertEquals("my description", loan.getDescription());
    }

    @Test
    public void getInterest_returnsCorrectRate() {
        Loan loan = new TestLoan(100, 7.5, "desc");
        assertEquals(7.5, loan.getInterest(), 0.001);
    }

    // ========== toString ==========

    @Test
    public void toString_correctFormat() {
        Loan loan = new TestLoan(100, 5, "test loan");
        assertEquals("[Amount: 100.00, Rate: 5.00%, Desc: test loan]", loan.toString());
    }

    // ========== equals ==========

    @Test
    public void equals_sameValues_returnsTrue() {
        Loan a = new TestLoan(100, 5, "desc");
        Loan b = new TestLoan(100, 5, "desc");
        assertEquals(a, b);
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        Loan a = new TestLoan(100, 5, "desc");
        assertEquals(a, a);
    }

    @Test
    public void equals_differentAmount_returnsFalse() {
        Loan a = new TestLoan(100, 5, "desc");
        Loan b = new TestLoan(200, 5, "desc");
        assertNotEquals(a, b);
    }

    @Test
    public void equals_differentRate_returnsFalse() {
        Loan a = new TestLoan(100, 5, "desc");
        Loan b = new TestLoan(100, 10, "desc");
        assertNotEquals(a, b);
    }

    @Test
    public void equals_differentDescription_returnsFalse() {
        Loan a = new TestLoan(100, 5, "desc");
        Loan b = new TestLoan(100, 5, "other");
        assertNotEquals(a, b);
    }

    @Test
    public void equals_nullObject_returnsFalse() {
        Loan a = new TestLoan(100, 5, "desc");
        assertNotEquals(a, null);
    }

    @Test
    public void equals_differentType_returnsFalse() {
        Loan a = new TestLoan(100, 5, "desc");
        assertNotEquals(a, "not a loan");
    }

    // ========== hashCode ==========

    @Test
    public void hashCode_equalLoans_samehashCode() {
        Loan a = new TestLoan(100, 5, "desc");
        Loan b = new TestLoan(100, 5, "desc");
        assertEquals(a.hashCode(), b.hashCode());
    }

    // ========== isValidLoanArguments ==========

    @Test
    public void isValidLoanArguments_validGenericLoan_returnsTrue() {
        assertTrue(Loan.isValidLoanArguments("100, 5, my loan"));
    }

    @Test
    public void isValidLoanArguments_validMonthlyLoan_returnsTrue() {
        assertTrue(Loan.isValidLoanArguments("m 100, 5, my loan"));
    }

    @Test
    public void isValidLoanArguments_validYearlyLoan_returnsTrue() {
        assertTrue(Loan.isValidLoanArguments("y 100, 5, my loan"));
    }

    @Test
    public void isValidLoanArguments_uppercaseType_returnsTrue() {
        assertTrue(Loan.isValidLoanArguments("M 100, 5, my loan"));
    }

    @Test
    public void isValidLoanArguments_zeroRate_returnsTrue() {
        assertTrue(Loan.isValidLoanArguments("100, 0, desc"));
    }

    @Test
    public void isValidLoanArguments_maxRate_returnsTrue() {
        assertTrue(Loan.isValidLoanArguments("100, 100, desc"));
    }

    @Test
    public void isValidLoanArguments_rateAbove100_returnsFalse() {
        assertFalse(Loan.isValidLoanArguments("100, 101, desc"));
    }

    @Test
    public void isValidLoanArguments_negativeRate_returnsFalse() {
        assertFalse(Loan.isValidLoanArguments("100, -1, desc"));
    }

    @Test
    public void isValidLoanArguments_missingDescription_returnsFalse() {
        assertFalse(Loan.isValidLoanArguments("100, 5"));
    }

    @Test
    public void isValidLoanArguments_emptyDescription_returnsFalse() {
        assertFalse(Loan.isValidLoanArguments("100, 5, "));
    }

    @Test
    public void isValidLoanArguments_nonNumericAmount_returnsFalse() {
        assertFalse(Loan.isValidLoanArguments("abc, 5, desc"));
    }

    @Test
    public void isValidLoanArguments_nonNumericRate_returnsFalse() {
        assertFalse(Loan.isValidLoanArguments("100, xyz, desc"));
    }

    @Test
    public void isValidLoanArguments_emptyString_returnsFalse() {
        assertFalse(Loan.isValidLoanArguments(""));
    }
}
