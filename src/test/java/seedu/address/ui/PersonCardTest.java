package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.loan.Loan;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    @Test
    public void formatBalance_noLoans_showsZeroBalance() {
        Person person = new PersonBuilder().withLoans().build();
        assertEquals("Balance: $0.00", PersonCard.formatBalance(person));
    }

    @Test
    public void formatBalance_positiveLoan_showsYouOwe() {
        Person person = new PersonBuilder()
                .withLoans(new Loan(12.5, 0, "Dinner"))
                .build();
        assertEquals("You owe: $12.50", PersonCard.formatBalance(person));
    }

    @Test
    public void formatBalance_negativeLoan_showsTheyOweYou() {
        Person person = new PersonBuilder()
                .withLoans(new Loan(-45.0, 0, "Project Lunch"))
                .build();
        assertEquals("They owe you: $45.00", PersonCard.formatBalance(person));
    }

    @Test
    public void formatBalance_multipleLoans_showsSummedBalance() {
        Person person = new PersonBuilder()
                .withLoans(
                        new Loan(20.0, 0, "Lunch"),
                        new Loan(-5.0, 0, "Coffee"))
                .build();
        assertEquals("You owe: $15.00", PersonCard.formatBalance(person));
    }
}
