package seedu.address.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class YearlyTransactionTest {

    @Test
    public void updateTransactionAmount_appliesYearlyInterest() {
        YearlyTransaction transaction = new YearlyTransaction(1000, 10, "test transaction");

        transaction.lastRecalculatedDate = transaction.lastRecalculatedDate.minusYears(2);

        transaction.updateTransactionAmount();

        // 1000 * (1.1)^2 = 1210
        assertEquals(1210, transaction.currAmount, 0.01);
    }

    @Test
    public void updateTransactionAmount_noYearsPassed_noChange() {
        YearlyTransaction transaction = new YearlyTransaction(1000, 10, "test transaction");

        transaction.updateTransactionAmount();

        assertEquals(1000, transaction.currAmount, 0.01);
    }
}
