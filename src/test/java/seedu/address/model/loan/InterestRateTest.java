package seedu.address.model.loan;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class InterestRateTest {

    @Test
    public void constructor_validRate_success() {
        InterestRate rate = new InterestRate(5);
        assertEquals(5.0, rate.getInterest());
    }

    @Test
    public void constructor_invalidRate_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new InterestRate(-1));
        assertThrows(IllegalArgumentException.class, () -> new InterestRate(101));
    }

    @Test
    public void isValidInterestRate() {
        assertTrue(InterestRate.isValidInterestRate(0));
        assertTrue(InterestRate.isValidInterestRate(100));

        assertFalse(InterestRate.isValidInterestRate(-5));
        assertFalse(InterestRate.isValidInterestRate(101));
    }

    @Test
    public void applyInterestRate_correctCalculation() {
        InterestRate rate = new InterestRate(10);

        double result = rate.applyInterestRate(1000, 2);

        // 1000 * (1.1)^2 = 1210
        assertEquals(1210, result, 0.001);
    }

    @Test
    public void equals() {
        InterestRate rate1 = new InterestRate(5);
        InterestRate rate2 = new InterestRate(5);
        InterestRate rate3 = new InterestRate(10);

        assertEquals(rate1, rate2);
        assertNotEquals(rate1, rate3);
        assertNotEquals(rate1, null);
    }
}

