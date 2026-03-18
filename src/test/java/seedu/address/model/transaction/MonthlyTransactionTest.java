package seedu.address.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MonthlyTransactionTest {

    @Test
    public void updateTransactionAmount_appliesMonthlyInterest() {
        MonthlyTransaction transaction = new MonthlyTransaction(1000, 10, "test transaction");

        transaction.lastRecalculatedDate = transaction.lastRecalculatedDate.minusMonths(2);

        transaction.updateTransactionAmount();

        // 1000 * (1.1)^2 = 1210
        assertEquals(1210, transaction.currAmount, 0.01);
    }

    @Test
    public void updateTransactionAmount_noMonthsPassed_noChange() {
        MonthlyTransaction transaction = new MonthlyTransaction(1000, 10, "test transaction");

        transaction.updateTransactionAmount();

        assertEquals(1000, transaction.currAmount, 0.01);
    }
}
