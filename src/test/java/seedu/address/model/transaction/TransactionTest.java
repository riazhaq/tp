package seedu.address.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class TransactionTest {

    private static Person debtor() {
        return new PersonBuilder().withName("Debtor").build();
    }

    private static Person creditor() {
        return new PersonBuilder().withName("Creditor").build();
    }

    private static class TestTransaction extends Transaction {

        public TestTransaction(double amount, double rate, String description) {
            super(debtor(), creditor(), amount, rate, description);
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
        assertTrue(transaction.isSettled());
    }

    @Test
    public void payTransaction_overpay_resultIsNegative() {
        Transaction transaction = new TestTransaction(100, 5, "test transaction");
        transaction.payTransaction(150);
        assertEquals(-50, transaction.getCurrAmount(), 0.001);
        assertFalse(transaction.isSettled());
    }

    @Test
    public void settleTransaction_setsAmountToZeroAndMarksSettled() {
        Transaction transaction = new TestTransaction(100, 5, "test transaction");

        transaction.settleTransaction();

        assertEquals(0, transaction.getCurrAmount(), 0.001);
        assertTrue(transaction.isSettled());
    }

    @Test
    public void settleTransaction_updatesTransactionBeforeSettlement() {
        Transaction transaction = new TestTransaction(100, 5, "test transaction");
        transaction.lastRecalculatedDate = transaction.lastRecalculatedDate.minusDays(1);

        transaction.settleTransaction();

        assertEquals(0, transaction.getCurrAmount(), 0.001);
        assertTrue(transaction.isSettled());
    }

    @Test
    public void setSettled_updatesState() {
        Transaction transaction = new TestTransaction(100, 5, "test transaction");

        transaction.setSettled(true);
        assertTrue(transaction.isSettled());

        transaction.setSettled(false);
        assertFalse(transaction.isSettled());
    }

    @Test
    public void setDebtorAndCreditor_updatesReferences() {
        Transaction transaction = new TestTransaction(100, 5, "test transaction");
        Person replacementDebtor = new PersonBuilder().withName("Replacement Debtor").build();
        Person replacementCreditor = new PersonBuilder().withName("Replacement Creditor").build();

        transaction.setDebtor(replacementDebtor);
        transaction.setCreditor(replacementCreditor);

        assertSame(replacementDebtor, transaction.getDebtor());
        assertSame(replacementCreditor, transaction.getCreditor());
    }

    @Test
    public void setDebtor_null_throwsNullPointerException() {
        Transaction transaction = new TestTransaction(100, 5, "test transaction");
        assertThrows(NullPointerException.class, () -> transaction.setDebtor(null));
    }

    @Test
    public void setCreditor_null_throwsNullPointerException() {
        Transaction transaction = new TestTransaction(100, 5, "test transaction");
        assertThrows(NullPointerException.class, () -> transaction.setCreditor(null));
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
        assertEquals("[Amount: 100.00, Rate: 5.00%, Desc: test transaction, Debtor: Debtor, Creditor: Creditor,"
                + " Type: None]", transaction.toString());
    }

    // ========== equals ==========

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
    public void equals_differentSettledState_returnsFalse() {
        Transaction a = new TestTransaction(100, 5, "desc");
        Transaction b = new TestTransaction(100, 5, "desc");
        b.setSettled(true);
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

}
