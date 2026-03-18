package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.model.transaction.Transaction;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    @Test
    public void formatBalance_noTransactions_showsZeroBalance() {
        Person person = new PersonBuilder().withTransactions().build();
        assertEquals("Balance: $0.00", PersonCard.formatBalance(person));
    }

    @Test
    public void formatBalance_positiveTransaction_showsYouOwe() {
        Person person = new PersonBuilder()
                .withTransactions(new Transaction(12.5, 0, "Dinner"))
                .build();
        assertEquals("You owe: $12.50", PersonCard.formatBalance(person));
    }

    @Test
    public void formatBalance_negativeTransaction_showsTheyOweYou() {
        Person person = new PersonBuilder()
                .withTransactions(new Transaction(-45.0, 0, "Project Lunch"))
                .build();
        assertEquals("They owe you: $45.00", PersonCard.formatBalance(person));
    }

    @Test
    public void formatBalance_multipleTransactions_showsSummedBalance() {
        Person person = new PersonBuilder()
                .withTransactions(
                        new Transaction(20.0, 0, "Lunch"),
                        new Transaction(-5.0, 0, "Coffee"))
                .build();
        assertEquals("You owe: $15.00", PersonCard.formatBalance(person));
    }

    @Test
    public void activeDebtsModel_emptyTransactions_showsZero() {
        Person person = new PersonBuilder().build();
        PersonCard.ActiveDebtsModel model = PersonCard.activeDebtsModelFor(person);
        assertEquals("$0.00", model.getAmountText());
        assertEquals("", model.getSuffixText());
        assertEquals(null, model.getStyleClass());
    }

    @Test
    public void activeDebtsModel_positiveTotal_showsOweRed() {
        Person person = new PersonBuilder()
                .withTransactions(new Transaction(12.5, 0, "Dinner"))
                .build();
        PersonCard.ActiveDebtsModel model = PersonCard.activeDebtsModelFor(person);
        assertEquals("-$12.50", model.getAmountText());
        assertEquals("(Owe)", model.getSuffixText());
        assertEquals("debts-owe", model.getStyleClass());
    }

    @Test
    public void activeDebtsModel_negativeTotal_showsLentGreen() {
        Person person = new PersonBuilder()
                .withTransactions(new Transaction(-45.0, 0, "Project Lunch"))
                .build();
        PersonCard.ActiveDebtsModel model = PersonCard.activeDebtsModelFor(person);
        assertEquals("+$45.00", model.getAmountText());
        assertEquals("(Lent)", model.getSuffixText());
        assertEquals("debts-lent", model.getStyleClass());
    }

    @Test
    public void activeDebtsModel_nearZeroTotal_treatedAsZero() {
        Person person = new PersonBuilder()
                .withTransactions(new Transaction(0.001, 0, "Tiny"))
                .build();
        PersonCard.ActiveDebtsModel model = PersonCard.activeDebtsModelFor(person);
        assertEquals("$0.00", model.getAmountText());
        assertEquals("", model.getSuffixText());
        assertEquals(null, model.getStyleClass());
    }
}
