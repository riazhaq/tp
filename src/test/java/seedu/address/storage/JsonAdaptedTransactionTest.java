package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.Transaction;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedTransactionTest {

    private static final double VALID_AMOUNT = 1000.0;
    private static final String VALID_DESCRIPTION = "Test transaction";

    private static Person debtor() {
        return new PersonBuilder().withName("Debtor").build();
    }

    private static Person creditor() {
        return new PersonBuilder().withName("Creditor").build();
    }

    // ========== JsonCreator ==========

    @Test
    public void jsonCreator_validFields_createsTransaction() throws IllegalValueException {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(
                VALID_AMOUNT, VALID_AMOUNT, VALID_DESCRIPTION,
                new JsonAdaptedPerson(debtor()),
                new JsonAdaptedPerson(creditor()),
                false);

        Transaction transaction = adapted.toModelType();
        assertNotNull(transaction);
    }

    // ========== Round Trip ==========

    @Test
    public void roundTrip_transaction_preservesCoreFields() throws IllegalValueException {
        Person debtor = debtor();
        Person creditor = creditor();

        Transaction original = new Transaction(debtor, creditor, VALID_AMOUNT, VALID_DESCRIPTION);
        original.setSettled(true);

        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(original);
        Transaction restored = adapted.toModelType();

        assertEquals(original.getCurrAmount(), restored.getCurrAmount());
        assertEquals(original.getOriginalAmount(), restored.getOriginalAmount());
        assertEquals(original.getDescription(), restored.getDescription());
        assertEquals(original.getDebtor(), restored.getDebtor());
        assertEquals(original.getCreditor(), restored.getCreditor());
        assertTrue(restored.isSettled());
    }

    @Test
    public void roundTrip_settledTransaction_preservesOriginalAmount() throws IllegalValueException {
        Transaction original = new Transaction(debtor(), creditor(), VALID_AMOUNT, VALID_DESCRIPTION);
        original.settleTransaction(); // currAmount = 0

        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(original);
        Transaction restored = adapted.toModelType();

        assertEquals(0, restored.getCurrAmount(), 0.001);
        assertEquals(VALID_AMOUNT, restored.getOriginalAmount(), 0.001);
        assertTrue(restored.isSettled());
    }

    // ========== Original Amount Handling ==========

    @Test
    public void jsonCreator_nullOriginalAmount_fallsBackToCurrAmount() throws IllegalValueException {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(
                VALID_AMOUNT, null, VALID_DESCRIPTION,
                new JsonAdaptedPerson(debtor()),
                new JsonAdaptedPerson(creditor()),
                false);

        Transaction transaction = adapted.toModelType();

        assertEquals(VALID_AMOUNT, transaction.getOriginalAmount(), 0.001);
    }

    @Test
    public void jsonCreator_explicitOriginalAmount_restoresCorrectly() throws IllegalValueException {
        double currAmount = 0.0;
        double originalAmount = 500.0;

        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(
                currAmount, originalAmount, VALID_DESCRIPTION,
                new JsonAdaptedPerson(debtor()),
                new JsonAdaptedPerson(creditor()),
                true);

        Transaction transaction = adapted.toModelType();

        assertEquals(currAmount, transaction.getCurrAmount(), 0.001);
        assertEquals(originalAmount, transaction.getOriginalAmount(), 0.001);
        assertTrue(transaction.isSettled());
    }

    // ========== Missing Fields ==========

    @Test
    public void toModelType_missingDebtor_throwsIllegalValueException() {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(
                VALID_AMOUNT, VALID_AMOUNT, VALID_DESCRIPTION,
                null,
                new JsonAdaptedPerson(creditor()),
                false);

        assertThrows(IllegalValueException.class, adapted::toModelType);
    }

    @Test
    public void toModelType_missingCreditor_throwsIllegalValueException() {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(
                VALID_AMOUNT, VALID_AMOUNT, VALID_DESCRIPTION,
                new JsonAdaptedPerson(debtor()),
                null,
                false);

        assertThrows(IllegalValueException.class, adapted::toModelType);
    }

    @Test
    public void toModelType_missingDescription_throwsIllegalValueException() {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(
                VALID_AMOUNT, VALID_AMOUNT, null,
                new JsonAdaptedPerson(debtor()),
                new JsonAdaptedPerson(creditor()),
                false);

        assertThrows(IllegalValueException.class, adapted::toModelType);
    }

    // ========== Default Settled ==========

    @Test
    public void jsonCreator_nullSettled_defaultsToFalse() throws IllegalValueException {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(
                VALID_AMOUNT, VALID_AMOUNT, VALID_DESCRIPTION,
                new JsonAdaptedPerson(debtor()),
                new JsonAdaptedPerson(creditor()),
                null);

        Transaction transaction = adapted.toModelType();

        assertFalse(transaction.isSettled());
    }
}
