package seedu.address.model.transaction;

/**
 * Represents a transaction where interest is compounded monthly.
 */
public class MonthlyTransaction extends Transaction {

    /**
     * Constructs a MonthlyTransaction.
     *
     * @param currAmount the initial transaction amount
     * @param interestRate the interest rate applied monthly
     */
    public MonthlyTransaction(double currAmount, double interestRate, String description) {
        super(currAmount, interestRate, description);
    }

    /**
     * Updates the transaction amount by applying interest for each
     * month that has passed since the last recalculation.
     */
    @Override
    public void updateTransactionAmount() {
        long months = getNumberOfMonthsSinceLastPaid();

        currAmount = interestRate.applyInterestRate(currAmount, months);

        updateLastRecalculatedDate();
    }

    @Override
    public String toString() {
        return String.format("[Amount: %.2f, Rate: %.2f%%, Desc: %s, Type: Monthly]",
                currAmount, getInterest(), description);
    }
}

