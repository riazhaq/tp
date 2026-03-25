package seedu.address.model.transaction;

import seedu.address.model.person.Person;

/**
 * Represents a transaction where interest is compounded yearly.
 */
public class YearlyTransaction extends Transaction {

    /**
     * Constructs a YearlyTransaction.
     *
     * @param debtor       the person who owes money
     * @param creditor     the person who lent the money
     * @param currAmount   the initial transaction amount
     * @param interestRate the interest rate applied yearly
     * @param description  the description for the transaction
     */
    public YearlyTransaction(Person debtor, Person creditor,
                             double currAmount, double interestRate, String description) {
        super(debtor, creditor, currAmount, interestRate, description);
    }

    /**
     * Updates the transaction amount by applying compound interest for each
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
        return String.format("[Amount: %.2f, Rate: %.2f%%, Desc: %s, Debtor: %s, Creditor: %s, Type: Yearly]",
                currAmount, getInterest(), description, debtor.getName(), creditor.getName());
    }
}
