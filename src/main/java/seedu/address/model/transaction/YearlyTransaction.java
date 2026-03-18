package seedu.address.model.transaction;

/**
 * Represents a transaction where interest is compounded yearly.
 */
public class YearlyTransaction extends Transaction {

    /**
     * Constructs a YearlyTransaction.
     *
     * @param currAmount the initial transaction amount
     * @param interestRate the interest rate applied yearly
     * @param description the description for the transaction
     */
    public YearlyTransaction(double currAmount, double interestRate, String description) {
        super(currAmount, interestRate, description);
    }

    /**
     * Updates the transaction amount by applying interest for each
     * year that has passed since the last recalculation.
     */
    @Override
    public void updateTransactionAmount() {
        long years = getNumberOfYearsSinceLastPaid();

        currAmount = interestRate.applyInterestRate(currAmount, years);

        updateLastRecalculatedDate();
    }

    @Override
    public String toString() {
        return String.format("[Amount: %.2f, Rate: %.2f%%, Desc: %s, Type: Yearly]",
                currAmount, getInterest(), description);
    }
}
