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

    // ========== toString ==========

    @Test
    public void toString_correctFormat() {
        Transaction transaction = new Transaction(debtor(), creditor(), 100, "test");

        String expected = String.format(
                "[Amount: %.2f, Desc: %s, Date: %s, Debtor: %s, Creditor: %s]",
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
