package seedu.address.model.transaction;

import java.util.Objects;

/**
 * Carries the parsed fields of a transaction before the debtor and creditor
 * {@code Person} objects have been resolved from the model.
 *
 * <p>This is a pure data holder used by the parser layer. The command layer
 * is responsible for looking up the actual {@code Person} instances using the
 * indices supplied to {@code AddTransactionCommand}, then constructing a
 * {@code Transaction} from this descriptor.
 */
public class TransactionDescriptor {

    /** Whether interest compounds monthly ("m"), yearly ("y"), or not at all. */
    public enum CompoundingType { NONE, MONTHLY, YEARLY }

    private final CompoundingType compoundingType;
    private final double amount;
    private final double rate;
    private final String description;

    /**
     * Constructs a TransactionDescriptor.
     *
     * @param compoundingType the compounding type (NONE, MONTHLY, or YEARLY)
     * @param amount          the transaction amount
     * @param rate            the interest rate (0–100)
     * @param description     the transaction description
     */
    public TransactionDescriptor(CompoundingType compoundingType, double amount,
                                 double rate, String description) {
        this.compoundingType = compoundingType;
        this.amount = amount;
        this.rate = rate;
        this.description = description;
    }

    public CompoundingType getCompoundingType() {
        return compoundingType;
    }

    public double getAmount() {
        return amount;
    }

    public double getRate() {
        return rate;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof TransactionDescriptor)) {
            return false;
        }
        TransactionDescriptor o = (TransactionDescriptor) other;
        return compoundingType == o.compoundingType
                && Double.compare(amount, o.amount) == 0
                && Double.compare(rate, o.rate) == 0
                && description.equals(o.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compoundingType, amount, rate, description);
    }

    @Override
    public String toString() {
        return String.format("[Type: %s, Amount: %.2f, Rate: %.2f%%, Desc: %s]",
                compoundingType, amount, rate, description);
    }
}
