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

        public TestTransaction(double amount, String description) {
            super(debtor(), creditor(), amount, description);
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
        Transaction transaction = new TestTransaction(100, "test transaction");
        transaction.payTransaction(20);
        assertEquals(80, transaction.getCurrAmount(), 0.001);
    }
    // ========== Constructor & Getters ==========

    @Test
    public void constructor_validInputs_success() {
        Transaction transaction = new Transaction(debtor(), creditor(), 100, "test");

        assertEquals(100, transaction.getCurrAmount(), 0.001);
        assertEquals("test", transaction.getDescription());
        assertEquals(debtor(), transaction.getDebtor());
        assertEquals(creditor(), transaction.getCreditor());
        assertFalse(transaction.isSettled());
        assertEquals(LocalDate.now(), transaction.getDate());
    }

    // ========== settleTransaction ==========

    @Test
    public void settleTransaction_setsAmountToZeroAndMarksSettled() {
        Transaction transaction = new Transaction(debtor(), creditor(), 100, "test");

        transaction.settleTransaction();

        assertEquals(0, transaction.getCurrAmount(), 0.001);
        assertTrue(transaction.isSettled());
    }

    // ========== setSettled ==========

    @Test
    public void setSettled_updatesState() {
        Transaction transaction = new Transaction(debtor(), creditor(), 100, "test");

        transaction.setSettled(true);
        assertTrue(transaction.isSettled());

        transaction.setSettled(false);
        assertFalse(transaction.isSettled());
    }

    // ========== setDebtor / setCreditor ==========

    @Test
    public void setDebtorAndCreditor_updatesReferences() {
        Transaction transaction = new Transaction(debtor(), creditor(), 100, "test");

        Person newDebtor = new PersonBuilder().withName("New Debtor").build();
        Person newCreditor = new PersonBuilder().withName("New Creditor").build();

        transaction.setDebtor(newDebtor);
        transaction.setCreditor(newCreditor);

        assertSame(newDebtor, transaction.getDebtor());
        assertSame(newCreditor, transaction.getCreditor());
    }

    @Test
    public void setDebtor_null_throwsNullPointerException() {
        Transaction transaction = new Transaction(debtor(), creditor(), 100, "test");
        assertThrows(NullPointerException.class, () -> transaction.setDebtor(null));
    }

    @Test
    public void setCreditor_null_throwsNullPointerException() {
        Transaction transaction = new Transaction(debtor(), creditor(), 100, "test");
        assertThrows(NullPointerException.class, () -> transaction.setCreditor(null));
    }

    // ========== originalAmount ==========

    @Test
    public void getOriginalAmount_matchesInitialAmount() {
        Transaction transaction = new TestTransaction(250.5, "desc");
        assertEquals(250.5, transaction.getOriginalAmount(), 0.001);
    }

    @Test
    public void getOriginalAmount_unchangedAfterPayment() {
        Transaction transaction = new TestTransaction(100, "test transaction");
        transaction.payTransaction(60);
        assertEquals(100, transaction.getOriginalAmount(), 0.001);
        assertEquals(40, transaction.getCurrAmount(), 0.001);
    }

    @Test
    public void getOriginalAmount_unchangedAfterFullSettlement() {
        Transaction transaction = new TestTransaction(100, "test transaction");
        transaction.settleTransaction();
        assertEquals(100, transaction.getOriginalAmount(), 0.001);
        assertEquals(0, transaction.getCurrAmount(), 0.001);
        assertTrue(transaction.isSettled());
    }

    @Test
    public void setOriginalAmount_updatesStoredValue() {
        Transaction transaction = new TestTransaction(100, "desc");
        transaction.setOriginalAmount(999.9);
        assertEquals(999.9, transaction.getOriginalAmount(), 0.001);
    }

    // ========== Getters ==========

    @Test
    public void getCurrAmount_returnsCorrectAmount() {
        Transaction transaction = new TestTransaction(250.5, "desc");
        assertEquals(250.5, transaction.getCurrAmount(), 0.001);
    }

    @Test
    public void getDescription_returnsCorrectDescription() {
        Transaction transaction = new TestTransaction(100, "my description");
        assertEquals("my description", transaction.getDescription());
    }

    // ========== toString ==========

    @Test
    public void toString_correctFormat() {
        Transaction transaction = new Transaction(debtor(), creditor(), 100, "test");

        String expected = String.format(
                "[Outstanding: %.2f, Desc: %s, Date: %s, Debtor: %s, Creditor: %s]",
                100.00,
                "test",
                transaction.getDate(),
                "Debtor",
                "Creditor"
        );

        assertEquals(expected, transaction.toString());
    }

    // ========== equals ==========

    @Test
    public void equals_sameObject_returnsTrue() {
        Transaction t = new Transaction(debtor(), creditor(), 100, "test");
        assertEquals(t, t);
    }

    @Test
    public void equals_differentObjectsSameValues_returnsFalse() {
        Transaction a = new Transaction(debtor(), creditor(), 100, "test");
        Transaction b = new Transaction(debtor(), creditor(), 100, "test");

        // Different IDs → should NOT be equal
        assertNotEquals(a, b);
    }

    @Test
    public void equals_null_returnsFalse() {
        Transaction t = new Transaction(debtor(), creditor(), 100, "test");
        assertNotEquals(t, null);
    }

    @Test
    public void equals_differentType_returnsFalse() {
        Transaction t = new Transaction(debtor(), creditor(), 100, "test");
        assertNotEquals(t, "not a transaction");
    }
}
