package seedu.address.model.transaction;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents an Amount in a transaction (debt or loan) in IOU.
 * Guarantees: immutable; is valid as declared in {@link #isValidAmount(String)}
 */
public class Amount {

    public static final String MESSAGE_CONSTRAINTS =
            "Amount must be a positive number with up to 2 decimal places (e.g., 5.00).";

    /*
     * Amount must be a positive number with up to 2 decimal places.
     * No currency symbols allowed.
     */
    public static final String VALIDATION_REGEX = "^\\d+(\\.\\d{1,2})?$";

    public final double value;

    /**
     * Constructs an {@code Amount}.
     *
     * @param amount A valid amount string.
     */
    public Amount(String amount) {
        requireNonNull(amount);
        checkArgument(isValidAmount(amount), MESSAGE_CONSTRAINTS);
        this.value = Double.parseDouble(amount);
    }

    /**
     * Returns true if a given string is a valid amount.
     * Must be a positive number with up to 2 decimal places and no currency symbols.
     */
    public static boolean isValidAmount(String test) {
        if (test == null || !test.matches(VALIDATION_REGEX)) {
            return false;
        }
        double parsed = Double.parseDouble(test);
        return parsed > 0;
    }

    /**
     * Returns the amount formatted as a currency string (e.g., "$12.50").
     */
    @Override
    public String toString() {
        return String.format("$%.2f", value);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Amount)) {
            return false;
        }

        Amount otherAmount = (Amount) other;
        return Double.compare(value, otherAmount.value) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }

}
