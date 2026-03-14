package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.loan.Loan;
import seedu.address.model.loan.MonthlyLoan;
import seedu.address.model.loan.YearlyLoan;

public class JsonAdaptedLoanTest {

    private static final double VALID_AMOUNT = 1000.0;
    private static final double VALID_RATE = 5.0;
    private static final String VALID_DESCRIPTION = "Test loan";

    // ========== Constructor from primitives (@JsonCreator) ==========

    @Test
    public void jsonCreator_validMonthlyLoanType_success() throws IllegalValueException {
        JsonAdaptedLoan adapted = new JsonAdaptedLoan("m", VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        Loan loan = adapted.toModelType();
        assertTrue(loan instanceof MonthlyLoan);
    }

    @Test
    public void jsonCreator_validYearlyLoanType_success() throws IllegalValueException {
        JsonAdaptedLoan adapted = new JsonAdaptedLoan("y", VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        Loan loan = adapted.toModelType();
        assertTrue(loan instanceof YearlyLoan);
    }

    @Test
    public void jsonCreator_emptyLoanType_returnsGenericLoan() throws IllegalValueException {
        JsonAdaptedLoan adapted = new JsonAdaptedLoan("", VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        Loan loan = adapted.toModelType();
        // Should be a generic Loan, not a subclass
        assertEquals(Loan.class, loan.getClass());
    }

    @Test
    public void jsonCreator_nullLoanType_treatedAsEmptyReturnsGenericLoan() throws IllegalValueException {
        JsonAdaptedLoan adapted = new JsonAdaptedLoan(null, VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        assertEquals("", adapted.getLoanType());
        Loan loan = adapted.toModelType();
        assertEquals(Loan.class, loan.getClass());
    }

    @Test
    public void jsonCreator_unknownLoanType_returnsGenericLoan() throws IllegalValueException {
        JsonAdaptedLoan adapted = new JsonAdaptedLoan("z", VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        Loan loan = adapted.toModelType();
        assertEquals(Loan.class, loan.getClass());
    }

    // ========== Constructor from Loan model object ==========

    @Test
    public void fromLoan_monthlyLoan_setsLoanTypeToM() {
        MonthlyLoan source = new MonthlyLoan(VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        JsonAdaptedLoan adapted = new JsonAdaptedLoan(source);
        assertEquals("m", adapted.getLoanType());
    }

    @Test
    public void fromLoan_yearlyLoan_setsLoanTypeToY() {
        YearlyLoan source = new YearlyLoan(VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        JsonAdaptedLoan adapted = new JsonAdaptedLoan(source);
        assertEquals("y", adapted.getLoanType());
    }

    @Test
    public void fromLoan_genericLoan_setsLoanTypeToEmpty() {
        Loan source = new Loan(VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        JsonAdaptedLoan adapted = new JsonAdaptedLoan(source);
        assertEquals("", adapted.getLoanType());
    }

    // ========== Field preservation round-trip ==========

    @Test
    public void fromLoan_monthlyLoan_preservesAllFields() throws IllegalValueException {
        MonthlyLoan source = new MonthlyLoan(VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        JsonAdaptedLoan adapted = new JsonAdaptedLoan(source);

        assertEquals(VALID_AMOUNT, adapted.getAmount());
        assertEquals(VALID_RATE, adapted.getRate());
        assertEquals(VALID_DESCRIPTION, adapted.getDescription());
    }

    @Test
    public void fromLoan_yearlyLoan_preservesAllFields() throws IllegalValueException {
        YearlyLoan source = new YearlyLoan(VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        JsonAdaptedLoan adapted = new JsonAdaptedLoan(source);

        assertEquals(VALID_AMOUNT, adapted.getAmount());
        assertEquals(VALID_RATE, adapted.getRate());
        assertEquals(VALID_DESCRIPTION, adapted.getDescription());
    }

    @Test
    public void roundTrip_monthlyLoan_preservesData() throws IllegalValueException {
        MonthlyLoan original = new MonthlyLoan(VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        JsonAdaptedLoan adapted = new JsonAdaptedLoan(original);
        Loan restored = adapted.toModelType();

        assertTrue(restored instanceof MonthlyLoan);
        assertEquals(original.getCurrAmount(), restored.getCurrAmount());
        assertEquals(original.getInterest(), restored.getInterest());
        assertEquals(original.getDescription(), restored.getDescription());
    }

    @Test
    public void roundTrip_yearlyLoan_preservesData() throws IllegalValueException {
        YearlyLoan original = new YearlyLoan(VALID_AMOUNT, VALID_RATE, VALID_DESCRIPTION);
        JsonAdaptedLoan adapted = new JsonAdaptedLoan(original);
        Loan restored = adapted.toModelType();

        assertTrue(restored instanceof YearlyLoan);
        assertEquals(original.getCurrAmount(), restored.getCurrAmount());
        assertEquals(original.getInterest(), restored.getInterest());
        assertEquals(original.getDescription(), restored.getDescription());
    }

    // ========== toModelType edge cases ==========

    @Test
    public void toModelType_zeroAmount_success() throws IllegalValueException {
        JsonAdaptedLoan adapted = new JsonAdaptedLoan("m", 0.0, VALID_RATE, VALID_DESCRIPTION);
        Loan loan = adapted.toModelType();
        assertEquals(0.0, loan.getCurrAmount());
    }

    @Test
    public void toModelType_zeroRate_success() throws IllegalValueException {
        JsonAdaptedLoan adapted = new JsonAdaptedLoan("y", VALID_AMOUNT, 0.0, VALID_DESCRIPTION);
        Loan loan = adapted.toModelType();
        assertEquals(0.0, loan.getInterest());
    }

    @Test
    public void toModelType_invalidData_throwsIllegalValueException() {
        // Negative amount — adjust this test to match what your Loan constructor actually rejects
        JsonAdaptedLoan adapted = new JsonAdaptedLoan("m", -500.0, 1000, VALID_DESCRIPTION);
        assertThrows(IllegalValueException.class, adapted::toModelType);
    }
}
