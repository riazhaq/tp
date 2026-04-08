package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.MonthlyTransaction;
import seedu.address.model.transaction.Transaction;
import seedu.address.model.transaction.YearlyTransaction;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedTransactionTest {

    private static final double VALID_AMOUNT = 1000.0;
    private static final double VALID_RATE = 5.0;
    private static final String VALID_DESCRIPTION = "Test transaction";

    private static Person debtor() {
        return new PersonBuilder().withName("Debtor").build();
    }

    private static Person creditor() {
        return new PersonBuilder().withName("Creditor").build();
    }

    @Test
    public void jsonCreator_monthlyType_createsMonthlyTransaction() throws IllegalValueException {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("m", VALID_AMOUNT, null, VALID_RATE,
                VALID_DESCRIPTION, new JsonAdaptedPerson(debtor()), new JsonAdaptedPerson(creditor()), false);
        Transaction transaction = adapted.toModelType();
        assertTrue(transaction instanceof MonthlyTransaction);
    }

    @Test
    public void jsonCreator_yearlyType_createsYearlyTransaction() throws IllegalValueException {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("y", VALID_AMOUNT, null, VALID_RATE,
                VALID_DESCRIPTION, new JsonAdaptedPerson(debtor()), new JsonAdaptedPerson(creditor()), false);
        Transaction transaction = adapted.toModelType();
        assertTrue(transaction instanceof YearlyTransaction);
    }

    @Test
    public void jsonCreator_unknownType_createsGenericTransaction() throws IllegalValueException {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("z", VALID_AMOUNT, null, VALID_RATE,
                VALID_DESCRIPTION, new JsonAdaptedPerson(debtor()), new JsonAdaptedPerson(creditor()), false);
        Transaction transaction = adapted.toModelType();
        assertEquals(Transaction.class, transaction.getClass());
    }

    @Test
    public void roundTrip_monthlyTransaction_preservesCoreFields() throws IllegalValueException {
        Person debtor = debtor();
        Person creditor = creditor();
        MonthlyTransaction original = new MonthlyTransaction(debtor, creditor, VALID_AMOUNT, VALID_RATE,
                VALID_DESCRIPTION);
        original.setSettled(true);

        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(original);
        Transaction restored = adapted.toModelType();

        assertTrue(restored instanceof MonthlyTransaction);
        assertEquals(original.getCurrAmount(), restored.getCurrAmount());
        assertEquals(original.getOriginalAmount(), restored.getOriginalAmount());
        assertEquals(original.getInterest(), restored.getInterest());
        assertEquals(original.getDescription(), restored.getDescription());
        assertEquals(original.getDebtor(), restored.getDebtor());
        assertEquals(original.getCreditor(), restored.getCreditor());
        assertTrue(restored.isSettled());
    }

    @Test
    public void roundTrip_settledTransaction_preservesOriginalAmount() throws IllegalValueException {
        Person debtor = debtor();
        Person creditor = creditor();
        Transaction original = new Transaction(debtor, creditor, VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        original.settleTransaction(); // currAmount becomes 0, originalAmount stays VALID_AMOUNT

        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(original);
        Transaction restored = adapted.toModelType();

        assertEquals(0, restored.getCurrAmount(), 0.001);
        assertEquals(VALID_AMOUNT, restored.getOriginalAmount(), 0.001);
        assertTrue(restored.isSettled());
    }

    @Test
    public void jsonCreator_nullOriginalAmount_fallsBackToCurrAmount() throws IllegalValueException {
        // Simulates loading an old save file that has no originalAmount field
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("", VALID_AMOUNT, null, VALID_RATE,
                VALID_DESCRIPTION, new JsonAdaptedPerson(debtor()), new JsonAdaptedPerson(creditor()), false);
        Transaction transaction = adapted.toModelType();
        assertEquals(VALID_AMOUNT, transaction.getOriginalAmount(), 0.001);
    }

    @Test
    public void jsonCreator_explicitOriginalAmount_restoresCorrectly() throws IllegalValueException {
        double settledCurrAmount = 0.0;
        double originalAmount = 500.0;
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("", settledCurrAmount, originalAmount,
                VALID_RATE, VALID_DESCRIPTION, new JsonAdaptedPerson(debtor()), new JsonAdaptedPerson(creditor()),
                true);
        Transaction transaction = adapted.toModelType();
        assertEquals(settledCurrAmount, transaction.getCurrAmount(), 0.001);
        assertEquals(originalAmount, transaction.getOriginalAmount(), 0.001);
        assertTrue(transaction.isSettled());
    }

    @Test
    public void toModelType_missingDebtor_throwsIllegalValueException() {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("m", VALID_AMOUNT, null, VALID_RATE,
                VALID_DESCRIPTION, null, new JsonAdaptedPerson(creditor()), false);
        assertThrows(IllegalValueException.class, adapted::toModelType);
    }

    @Test
    public void toModelType_missingCreditor_throwsIllegalValueException() {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("m", VALID_AMOUNT, null, VALID_RATE,
                VALID_DESCRIPTION, new JsonAdaptedPerson(debtor()), null, false);
        assertThrows(IllegalValueException.class, adapted::toModelType);
    }

    @Test
    public void toModelType_missingDescription_throwsIllegalValueException() {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("m", VALID_AMOUNT, null, VALID_RATE, null,
                new JsonAdaptedPerson(debtor()), new JsonAdaptedPerson(creditor()), false);
        assertThrows(IllegalValueException.class, adapted::toModelType);
    }

    @Test
    public void jsonCreator_nullSettled_defaultsToFalse() throws IllegalValueException {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("", VALID_AMOUNT, null, VALID_RATE,
                VALID_DESCRIPTION, new JsonAdaptedPerson(debtor()), new JsonAdaptedPerson(creditor()), null);

        Transaction transaction = adapted.toModelType();

        assertFalse(transaction.isSettled());
    }
}

