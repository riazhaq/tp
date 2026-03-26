package seedu.address.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.util.SampleDataUtil;

public class YearlyTransactionTest {

    @Test
    public void updateTransactionAmount_appliesYearlyInterest() {
        Person debtor = new Person(new Name("Debtor"), new Phone("11111111"), new Email("d@example.com"),
                new Address("debtor address"), SampleDataUtil.getTagSet());
        Person creditor = new Person(new Name("Creditor"), new Phone("22222222"), new Email("c@example.com"),
                new Address("creditor address"), SampleDataUtil.getTagSet());
        YearlyTransaction transaction = new YearlyTransaction(debtor, creditor, 1000, 10, "test transaction");

        transaction.lastRecalculatedDate = transaction.lastRecalculatedDate.minusYears(2);

        transaction.updateTransactionAmount();

        // 1000 * (1.1)^2 = 1210
        assertEquals(1210, transaction.currAmount, 0.01);
    }

    @Test
    public void updateTransactionAmount_noYearsPassed_noChange() {
        Person debtor = new Person(new Name("Debtor"), new Phone("11111111"), new Email("d@example.com"),
                new Address("debtor address"), SampleDataUtil.getTagSet());
        Person creditor = new Person(new Name("Creditor"), new Phone("22222222"), new Email("c@example.com"),
                new Address("creditor address"), SampleDataUtil.getTagSet());
        YearlyTransaction transaction = new YearlyTransaction(debtor, creditor, 1000, 10, "test transaction");

        transaction.updateTransactionAmount();

        assertEquals(1000, transaction.currAmount, 0.01);
    }
}
