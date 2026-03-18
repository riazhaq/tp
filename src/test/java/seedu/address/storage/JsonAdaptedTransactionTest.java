package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.transaction.MonthlyTransaction;
import seedu.address.model.transaction.Transaction;
import seedu.address.model.transaction.YearlyTransaction;

public class JsonAdaptedTransactionTest {

    private static final double VALID_AMOUNT = 1000.0;
    private static final double VALID_RATE = 5.0;
    private static final String VALID_DESCRIPTION = "Test transaction";

    // ========== Constructor from primitives (@JsonCreator) ==========

    @Test
    public void jsonCreator_validMonthlyTransactionType_success() throws IllegalValueException {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("m", VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        Transaction transaction = adapted.toModelType();
        assertTrue(transaction instanceof MonthlyTransaction);
    }

    @Test
    public void jsonCreator_validYearlyTransactionType_success() throws IllegalValueException {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("y", VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        Transaction transaction = adapted.toModelType();
        assertTrue(transaction instanceof YearlyTransaction);
    }

    @Test
    public void jsonCreator_emptyTransactionType_returnsGenericTransaction() throws IllegalValueException {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("", VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        Transaction transaction = adapted.toModelType();
        // Should be a generic Transaction, not a subclass
        assertEquals(Transaction.class, transaction.getClass());
    }

    @Test
    public void jsonCreator_nullTransactionType_treatedAsEmptyReturnsGenericTransaction() throws IllegalValueException {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(null, VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        assertEquals("", adapted.getTransactionType());
        Transaction transaction = adapted.toModelType();
        assertEquals(Transaction.class, transaction.getClass());
    }

    @Test
    public void jsonCreator_unknownTransactionType_returnsGenericTransaction() throws IllegalValueException {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("z", VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        Transaction transaction = adapted.toModelType();
        assertEquals(Transaction.class, transaction.getClass());
    }

    // ========== Constructor from Transaction model object ==========

    @Test
    public void fromTransaction_monthlyTransaction_setsTransactionTypeToM() {
        MonthlyTransaction source = new MonthlyTransaction(VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(source);
        assertEquals("m", adapted.getTransactionType());
    }

    @Test
    public void fromTransaction_yearlyTransaction_setsTransactionTypeToY() {
        YearlyTransaction source = new YearlyTransaction(VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(source);
        assertEquals("y", adapted.getTransactionType());
    }

    @Test
    public void fromTransaction_genericTransaction_setsTransactionTypeToEmpty() {
        Transaction source = new Transaction(VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(source);
        assertEquals("", adapted.getTransactionType());
    }

    // ========== Field preservation round-trip ==========

    @Test
    public void fromTransaction_monthlyTransaction_preservesAllFields() throws IllegalValueException {
        MonthlyTransaction source = new MonthlyTransaction(VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(source);

        assertEquals(VALID_AMOUNT, adapted.getAmount());
        assertEquals(VALID_RATE, adapted.getRate());
        assertEquals(VALID_DESCRIPTION, adapted.getDescription());
    }

    @Test
    public void fromTransaction_yearlyTransaction_preservesAllFields() throws IllegalValueException {
        YearlyTransaction source = new YearlyTransaction(VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(source);

        assertEquals(VALID_AMOUNT, adapted.getAmount());
        assertEquals(VALID_RATE, adapted.getRate());
        assertEquals(VALID_DESCRIPTION, adapted.getDescription());
    }

    @Test
    public void roundTrip_monthlyTransaction_preservesData() throws IllegalValueException {
        MonthlyTransaction original = new MonthlyTransaction(VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(original);
        Transaction restored = adapted.toModelType();

        assertTrue(restored instanceof MonthlyTransaction);
        assertEquals(original.getCurrAmount(), restored.getCurrAmount());
        assertEquals(original.getInterest(), restored.getInterest());
        assertEquals(original.getDescription(), restored.getDescription());
    }

    @Test
    public void roundTrip_yearlyTransaction_preservesData() throws IllegalValueException {
        YearlyTransaction original = new YearlyTransaction(VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(original);
        Transaction restored = adapted.toModelType();

        assertTrue(restored instanceof YearlyTransaction);
        assertEquals(original.getCurrAmount(), restored.getCurrAmount());
        assertEquals(original.getInterest(), restored.getInterest());
        assertEquals(original.getDescription(), restored.getDescription());
    }

    // ========== toModelType edge cases ==========

    @Test
    public void toModelType_zeroAmount_success() throws IllegalValueException {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("m", 0.0, VALID_RATE, VALID_DESCRIPTION);
        Transaction transaction = adapted.toModelType();
        assertEquals(0.0, transaction.getCurrAmount());
    }

    @Test
    public void toModelType_zeroRate_success() throws IllegalValueException {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("y", VALID_AMOUNT, 0.0, VALID_DESCRIPTION);
        Transaction transaction = adapted.toModelType();
        assertEquals(0.0, transaction.getInterest());
    }

    @Test
    public void toModelType_invalidData_throwsIllegalValueException() {
        // Negative amount — adjust this test to match what your Transaction constructor actually rejects
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("m", -500.0, 1000, VALID_DESCRIPTION);
        assertThrows(IllegalValueException.class, adapted::toModelType);
    }
}
