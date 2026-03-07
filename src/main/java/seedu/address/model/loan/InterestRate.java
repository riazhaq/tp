package seedu.address.model.loan;

public class InterestRate {

    public static final String MESSAGE_CONSTRAINTS =
            "Interest rate should be a number between 0 and 100.";

    private final double rate;

    public InterestRate(int rate) {

        if (!isValidInterestRate(rate)) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }

        this.rate = rate;
    }

    public double getInterest() {
        return rate;
    }

    public static boolean isValidInterestRate(int test) {
        return test >= 0 && test <= 100;
    }

    public double applyInterestRate(double principal, long numberOfTimes) {
        return principal * Math.pow((1.0 + rate / 100), numberOfTimes);
    }

    @Override
    public String toString() {
        return rate + "%";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof InterestRate)) {
            return false;
        }

        InterestRate otherRate = (InterestRate) other;
        return Double.compare(rate, otherRate.rate) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(rate);
    }
}