package seedu.address.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TransactionTest {

    private static class TestTransaction extends Transaction {

        public TestTransaction(double amount, double rate, String description) {
            super(amount, rate, description);
        }

        @Override
        public void updateTransactionAmount() {
            // simple implementation for testing
            currAmount += 10;
        }
    }

    // ========== payTransaction ==========

    @Test
    public void payTransaction_reducesAmount() {
        Transaction transaction = new TestTransaction(100, 5, "test transaction");
        transaction.payTransaction(20);
        assertEquals(80, transaction.getCurrAmount(), 0.001);
    }

    @Test
    public void payTransaction_updatesTransactionBeforePayment() {
        Transaction transaction = new TestTransaction(100, 5, "test transaction");
        transaction.lastRecalculatedDate = transaction.lastRecalculatedDate.minusDays(1);
        transaction.payTransaction(20);
        // updateTransactionAmount adds 10, then subtract 20
        assertEquals(90, transaction.getCurrAmount(), 0.001);
    }

    @Test
    public void payTransaction_sameDayNoUpdate() {
        // lastRecalculatedDate is today by default, so updateTransactionAmount should NOT be called
        Transaction transaction = new TestTransaction(100, 5, "test transaction");
        transaction.payTransaction(20);
        // no +10 from updateTransactionAmount, just -20
        assertEquals(80, transaction.getCurrAmount(), 0.001);
    }

    @Test
    public void payTransaction_fullAmount_resultIsZero() {
        Transaction transaction = new TestTransaction(100, 5, "test transaction");
        transaction.payTransaction(100);
        assertEquals(0, transaction.getCurrAmount(), 0.001);
    }

    @Test
    public void payTransaction_overpay_resultIsNegative() {
        Transaction transaction = new TestTransaction(100, 5, "test transaction");
        transaction.payTransaction(150);
        assertEquals(-50, transaction.getCurrAmount(), 0.001);
    }

    // ========== getNumberOfMonthsSinceLastPaid ==========

    @Test
    public void getNumberOfMonthsSinceLastPaid_threeMonths() {
        Transaction transaction = new TestTransaction(100, 5, "test transaction");
        transaction.lastRecalculatedDate = transaction.lastRecalculatedDate.minusMonths(3);
        assertEquals(3, transaction.getNumberOfMonthsSinceLastPaid());
    }

    @Test
    public void getNumberOfMonthsSinceLastPaid_today_returnsZero() {
        Transaction transaction = new TestTransaction(100, 5, "test transaction");
        assertEquals(0, transaction.getNumberOfMonthsSinceLastPaid());
    }

    // ========== getNumberOfYearsSinceLastPaid ==========

    @Test
    public void getNumberOfYearsSinceLastPaid_twoYears() {
        Transaction transaction = new TestTransaction(100, 5, "test transaction");
        transaction.lastRecalculatedDate = transaction.lastRecalculatedDate.minusYears(2);
        assertEquals(2, transaction.getNumberOfYearsSinceLastPaid());
    }

    @Test
    public void getNumberOfYearsSinceLastPaid_today_returnsZero() {
        Transaction transaction = new TestTransaction(100, 5, "test transaction");
        assertEquals(0, transaction.getNumberOfYearsSinceLastPaid());
    }

    // ========== Getters ==========

    @Test
    public void getCurrAmount_returnsCorrectAmount() {
        Transaction transaction = new TestTransaction(250.5, 3, "desc");
        assertEquals(250.5, transaction.getCurrAmount(), 0.001);
    }

    @Test
    public void getDescription_returnsCorrectDescription() {
        Transaction transaction = new TestTransaction(100, 5, "my description");
        assertEquals("my description", transaction.getDescription());
    }

    @Test
    public void getInterest_returnsCorrectRate() {
        Transaction transaction = new TestTransaction(100, 7.5, "desc");
        assertEquals(7.5, transaction.getInterest(), 0.001);
    }

    @Test
    public void getLastRecalculatedDate_returnsNonNullDate() {
        Transaction transaction = new TestTransaction(100, 0, "desc");
        assertEquals(LocalDate.now(), transaction.getLastRecalculatedDate());
    }

    // ========== toString ==========

    @Test
    public void toString_correctFormat() {
        Transaction transaction = new TestTransaction(100, 5, "test transaction");
        assertEquals("[Amount: 100.00, Rate: 5.00%, Desc: test transaction]", transaction.toString());
    }

    // ========== equals ==========

    @Test
    public void equals_sameValues_returnsTrue() {
        Transaction a = new TestTransaction(100, 5, "desc");
        Transaction b = new TestTransaction(100, 5, "desc");
        assertEquals(a, b);
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        Transaction a = new TestTransaction(100, 5, "desc");
        assertEquals(a, a);
    }

    @Test
    public void equals_differentAmount_returnsFalse() {
        Transaction a = new TestTransaction(100, 5, "desc");
        Transaction b = new TestTransaction(200, 5, "desc");
        assertNotEquals(a, b);
    }

    @Test
    public void equals_differentRate_returnsFalse() {
        Transaction a = new TestTransaction(100, 5, "desc");
        Transaction b = new TestTransaction(100, 10, "desc");
        assertNotEquals(a, b);
    }

    @Test
    public void equals_differentDescription_returnsFalse() {
        Transaction a = new TestTransaction(100, 5, "desc");
        Transaction b = new TestTransaction(100, 5, "other");
        assertNotEquals(a, b);
    }

    @Test
    public void equals_nullObject_returnsFalse() {
        Transaction a = new TestTransaction(100, 5, "desc");
        assertNotEquals(a, null);
    }

    @Test
    public void equals_differentType_returnsFalse() {
        Transaction a = new TestTransaction(100, 5, "desc");
        assertNotEquals(a, "not a transaction");
    }

    // ========== hashCode ==========

    @Test
    public void hashCode_equalTransactions_samehashCode() {
        Transaction a = new TestTransaction(100, 5, "desc");
        Transaction b = new TestTransaction(100, 5, "desc");
        assertEquals(a.hashCode(), b.hashCode());
    }

    // ========== isValidTransactionArguments ==========

    @Test
    public void isValidTransactionArguments_validGenericTransaction_returnsTrue() {
        assertTrue(Transaction.isValidTransactionArguments("100, 5, my transaction"));
    }

    @Test
    public void isValidTransactionArguments_validMonthlyTransaction_returnsTrue() {
        assertTrue(Transaction.isValidTransactionArguments("m 100, 5, my transaction"));
    }

    @Test
    public void isValidTransactionArguments_validYearlyTransaction_returnsTrue() {
        assertTrue(Transaction.isValidTransactionArguments("y 100, 5, my transaction"));
    }

    @Test
    public void isValidTransactionArguments_uppercaseType_returnsTrue() {
        assertTrue(Transaction.isValidTransactionArguments("M 100, 5, my transaction"));
    }

    @Test
    public void isValidTransactionArguments_zeroRate_returnsTrue() {
        assertTrue(Transaction.isValidTransactionArguments("100, 0, desc"));
    }

    @Test
    public void isValidTransactionArguments_maxRate_returnsTrue() {
        assertTrue(Transaction.isValidTransactionArguments("100, 100, desc"));
    }

    @Test
    public void isValidTransactionArguments_rateAbove100_returnsFalse() {
        assertFalse(Transaction.isValidTransactionArguments("100, 101, desc"));
    }

    @Test
    public void isValidTransactionArguments_negativeRate_returnsFalse() {
        assertFalse(Transaction.isValidTransactionArguments("100, -1, desc"));
    }

    @Test
    public void isValidTransactionArguments_missingDescription_returnsFalse() {
        assertFalse(Transaction.isValidTransactionArguments("100, 5"));
    }

    @Test
    public void isValidTransactionArguments_emptyDescription_returnsFalse() {
        assertFalse(Transaction.isValidTransactionArguments("100, 5, "));
    }

    @Test
    public void isValidTransactionArguments_nonNumericAmount_returnsFalse() {
        assertFalse(Transaction.isValidTransactionArguments("abc, 5, desc"));
    }

    @Test
    public void isValidTransactionArguments_nonNumericRate_returnsFalse() {
        assertFalse(Transaction.isValidTransactionArguments("100, xyz, desc"));
    }

    @Test
    public void isValidTransactionArguments_emptyString_returnsFalse() {
        assertFalse(Transaction.isValidTransactionArguments(""));
    }
}
