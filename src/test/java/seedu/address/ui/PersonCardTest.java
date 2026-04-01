package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.model.transaction.Transaction;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    private static Transaction transaction(Person person, double amount, String description, boolean isDebtor) {
        Person debtor = isDebtor ? person : new PersonBuilder().withName("Other").build();
        Person creditor = isDebtor ? new PersonBuilder().withName("Other").build() : person;
        return new Transaction(debtor, creditor, Math.abs(amount), 0, description);
    }

    private static Transaction transaction(double amount, String description) {
        Person debtor = new PersonBuilder().withName("Debtor").build();
        Person creditor = new PersonBuilder().withName("Creditor").build();
        return new Transaction(debtor, creditor, amount, 0, description);
    }

    @Test
    public void formatBalance_noTransactions_showsZeroBalance() {
        Person person = new PersonBuilder().withTransactions().build();
        assertEquals("Balance: $0.00", PersonCard.formatBalance(person));
    }

    @Test
    public void formatBalance_positiveTransaction_showsYouOwe() {
        Person person = new PersonBuilder().build();
        Transaction tx = transaction(person, 12.5, "Dinner", true); // person is debtor
        person = new PersonBuilder().withTransactions(tx).build();
        assertEquals("You owe: $12.50", PersonCard.formatBalance(person));
    }

    @Test
    public void formatBalance_negativeTransaction_showsTheyOweYou() {
        Person person = new PersonBuilder().build();
        Transaction tx = transaction(person, 45.0, "Project Lunch", false); // person is creditor
        person = new PersonBuilder().withTransactions(tx).build();
        assertEquals("They owe you: $45.00", PersonCard.formatBalance(person));
    }

    @Test
    public void formatBalance_multipleTransactions_showsSummedBalance() {
        Person person = new PersonBuilder().build();
        Transaction tx1 = transaction(person, 20.0, "Lunch", true); // person owes 20
        Transaction tx2 = transaction(person, 5.0, "Coffee", false); // person is owed 5
        person = new PersonBuilder().withTransactions(tx1, tx2).build();
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
        Person person = new PersonBuilder().build();
        Transaction tx = transaction(person, 12.5, "Dinner", true); // person is debtor
        person = new PersonBuilder().withTransactions(tx).build();
        PersonCard.ActiveDebtsModel model = PersonCard.activeDebtsModelFor(person);
        assertEquals("-$12.50", model.getAmountText());
        assertEquals("(Owe)", model.getSuffixText());
        assertEquals("debts-owe", model.getStyleClass());
    }

    @Test
    public void activeDebtsModel_negativeTotal_showsLentGreen() {
        Person person = new PersonBuilder().build();
        Transaction tx = transaction(person, 45.0, "Project Lunch", false); // person is creditor
        person = new PersonBuilder().withTransactions(tx).build();
        PersonCard.ActiveDebtsModel model = PersonCard.activeDebtsModelFor(person);
        assertEquals("+$45.00", model.getAmountText());
        assertEquals("(Lent)", model.getSuffixText());
        assertEquals("debts-lent", model.getStyleClass());
    }

    @Test
    public void activeDebtsModel_nearZeroTotal_treatedAsZero() {
        Person person = new PersonBuilder()
                .withTransactions(transaction(0.001, "Tiny"))
                .build();
        PersonCard.ActiveDebtsModel model = PersonCard.activeDebtsModelFor(person);
        assertEquals("$0.00", model.getAmountText());
        assertEquals("", model.getSuffixText());
        assertEquals(null, model.getStyleClass());
    }
}
