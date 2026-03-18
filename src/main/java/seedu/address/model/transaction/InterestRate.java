package seedu.address.model.transaction;

/**
 * Represents the interest rate applied to a transaction.
 * <p>
 * The interest rate must be between 0 and 100 (inclusive).
 * It is used to calculate compounded interest on a transaction amount.
 */
public class InterestRate {

    /** Message shown when the interest rate is invalid. */
    public static final String MESSAGE_CONSTRAINTS =
            "Interest rate should be a number between 0 and 100.";

    /** The interest rate value represented as a percentage. */
    private final double rate;

    /**
     * Constructs an {@code InterestRate}.
     *
     * @param rate Interest rate percentage between 0 and 100.
     * @throws IllegalArgumentException if the interest rate is invalid.
     */
    public InterestRate(double rate) {

        if (!isValidInterestRate(rate)) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }

        this.rate = rate;
    }
    /**
     * Returns the interest rate value.
     *
     * @return interest rate as a percentage.
     */
    public double getInterest() {
        return rate;
    }

    /**
     * Returns true if a given integer is a valid interest rate.
     *
     * @param test value to test
     * @return true if between 0 and 100 inclusive
     */
    public static boolean isValidInterestRate(double test) {
        return test >= 0 && test <= 100;
    }

    /**
     * Applies compound interest to a principal amount.
     *
     * @param principal the original transaction amount
     * @param numberOfTimes the number of times interest is compounded
     * @return the updated amount after interest is applied
     */
    public double applyInterestRate(double principal, long numberOfTimes) {
        return principal * Math.pow((1.0 + rate / 100), numberOfTimes);
    }

    /**
     * Returns the interest rate as a string with a percentage sign.
     *
     * @return formatted interest rate
     */
    @Override
    public String toString() {
        return rate + "%";
    }

    /**
     * Returns true if the given object has the same interest rate value.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof InterestRate otherRate)) {
            return false;
        }

        return Double.compare(rate, otherRate.rate) == 0;
    }

    /**
     * Returns the hash code of the interest rate.
     */
    @Override
    public int hashCode() {
        return Double.hashCode(rate);
    }
}
