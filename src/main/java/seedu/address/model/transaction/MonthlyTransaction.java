package seedu.address.model.transaction;

import seedu.address.model.person.Person;

/**
 * Represents a transaction where interest is compounded monthly.
 */
public class MonthlyTransaction extends Transaction {

    /**
     * Constructs a MonthlyTransaction.
     *
     * @param debtor       the person who owes money
     * @param creditor     the person who lent the money
     * @param currAmount   the initial transaction amount
     * @param interestRate the interest rate applied monthly
     * @param description  the description for the transaction
     */
    public MonthlyTransaction(Person debtor, Person creditor,
                              double currAmount, double interestRate, String description) {
        super(debtor, creditor, currAmount, interestRate, description);
    }

    /**
     * Updates the transaction amount by applying compound interest for each
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
        return String.format("[Amount: %.2f, Rate: %.2f%%, Desc: %s, Debtor: %s, Creditor: %s, Type: Monthly]",
                currAmount, getInterest(), description, debtor.getName(), creditor.getName());
    }
}
