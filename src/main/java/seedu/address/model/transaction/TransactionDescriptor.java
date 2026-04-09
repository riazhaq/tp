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

    private final double amount;
    private final String description;

    /**
     * Constructs a TransactionDescriptor.
     *
     * @param amount      the transaction amount
     * @param description the transaction description
     */
    public TransactionDescriptor(double amount, String description) {
        this.amount = amount;
        this.description = description;
    }

    public double getAmount() {
        return amount;
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
        return Double.compare(amount, o.amount) == 0
                && description.equals(o.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, description);
    }

    @Override
    public String toString() {
        return String.format("[Value: %.2f, Desc: %s]", amount, description);
    }
}
