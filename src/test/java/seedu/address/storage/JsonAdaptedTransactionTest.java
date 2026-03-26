package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("m", VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION,
                new JsonAdaptedPerson(debtor()), new JsonAdaptedPerson(creditor()));
        Transaction transaction = adapted.toModelType();
        assertTrue(transaction instanceof MonthlyTransaction);
    }

    @Test
    public void jsonCreator_yearlyType_createsYearlyTransaction() throws IllegalValueException {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("y", VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION,
                new JsonAdaptedPerson(debtor()), new JsonAdaptedPerson(creditor()));
        Transaction transaction = adapted.toModelType();
        assertTrue(transaction instanceof YearlyTransaction);
    }

    @Test
    public void jsonCreator_unknownType_createsGenericTransaction() throws IllegalValueException {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("z", VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION,
                new JsonAdaptedPerson(debtor()), new JsonAdaptedPerson(creditor()));
        Transaction transaction = adapted.toModelType();
        assertEquals(Transaction.class, transaction.getClass());
    }

    @Test
    public void roundTrip_monthlyTransaction_preservesCoreFields() throws IllegalValueException {
        Person debtor = debtor();
        Person creditor = creditor();
        MonthlyTransaction original = new MonthlyTransaction(debtor, creditor, VALID_AMOUNT, VALID_RATE,
                VALID_DESCRIPTION);

        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction(original);
        Transaction restored = adapted.toModelType();

        assertTrue(restored instanceof MonthlyTransaction);
        assertEquals(original.getCurrAmount(), restored.getCurrAmount());
        assertEquals(original.getInterest(), restored.getInterest());
        assertEquals(original.getDescription(), restored.getDescription());
        assertEquals(original.getDebtor(), restored.getDebtor());
        assertEquals(original.getCreditor(), restored.getCreditor());
    }

    @Test
    public void toModelType_missingDebtor_throwsIllegalValueException() {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("m", VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION,
                null, new JsonAdaptedPerson(creditor()));
        assertThrows(IllegalValueException.class, adapted::toModelType);
    }

    @Test
    public void toModelType_missingCreditor_throwsIllegalValueException() {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("m", VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION,
                new JsonAdaptedPerson(debtor()), null);
        assertThrows(IllegalValueException.class, adapted::toModelType);
    }

    @Test
    public void toModelType_missingDescription_throwsIllegalValueException() {
        JsonAdaptedTransaction adapted = new JsonAdaptedTransaction("m", VALID_AMOUNT, VALID_RATE, null,
                new JsonAdaptedPerson(debtor()), new JsonAdaptedPerson(creditor()));
        assertThrows(IllegalValueException.class, adapted::toModelType);
    }
}

