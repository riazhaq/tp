package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.model.transaction.Transaction;
import seedu.address.testutil.PersonBuilder;

public class TransactionListPanelTest {

    private static Transaction transaction(double amount, String description) {
        Person debtor = new PersonBuilder().withName("Debtor").build();
        Person creditor = new PersonBuilder().withName("Creditor").build();
        return new Transaction(debtor, creditor, amount, 0, description);
    }

    @Test
    public void noSelectionTitle_matchesExpected() {
        assertEquals("Transactions - Select a person", TransactionListPanel.noSelectionTitle());
    }

    @Test
    public void statusText_isPending() {
        assertEquals("Pending", TransactionListPanel.statusText());
    }

    @Test
    public void typeText_amountPositive_isOwe() {
        assertEquals("Owe", TransactionListPanel.typeText(12.34));
        assertEquals("Owe", TransactionListPanel.typeText(0));
    }

    @Test
    public void typeText_amountNegative_isLent() {
        assertEquals("Lent", TransactionListPanel.typeText(-0.01));
    }

    @Test
    public void amountText_formatsAbsoluteCurrency() {
        assertEquals("$12.50", TransactionListPanel.amountText(12.5));
        assertEquals("$12.50", TransactionListPanel.amountText(-12.5));
        assertEquals("$0.00", TransactionListPanel.amountText(0));
    }

    @Test
    public void descriptionText_returnsTransactionDescription() {
        Transaction transaction = transaction(1, "Dinner");
        assertEquals("Dinner", TransactionListPanel.descriptionText(transaction));
    }

    @Test
    public void descriptionText_null_throws() {
        assertThrows(NullPointerException.class, () -> TransactionListPanel.descriptionText(null));
    }

    @Test
    public void dateText_null_throws() {
        assertThrows(NullPointerException.class, () -> TransactionListPanel.dateText(null));
    }

    @Test
    public void styleClassForType_null_returnsNull() {
        assertNull(TransactionListPanel.styleClassForType(null));
    }

    @Test
    public void styleClassForType_recognisesOweAndLent() {
        assertEquals("tx-type-owe", TransactionListPanel.styleClassForType("Owe"));
        assertEquals("tx-type-owe", TransactionListPanel.styleClassForType("owe"));
        assertEquals("tx-type-lent", TransactionListPanel.styleClassForType("Lent"));
        assertEquals("tx-type-lent", TransactionListPanel.styleClassForType("lent"));
        assertNull(TransactionListPanel.styleClassForType("Other"));
    }

    @Test
    public void sortedTransactions_sortsDescendingByAmount() {
        Transaction transactionHigh = transaction(10, "high");
        Transaction transactionMid = transaction(7, "mid");
        Transaction transactionLow = transaction(-5, "low");

        List<Transaction> sorted = TransactionListPanel.sortedTransactions(
                Set.of(transactionLow, transactionMid, transactionHigh));
        assertEquals(List.of(transactionHigh, transactionMid, transactionLow), sorted);
    }

    @Test
    public void displayModelFor_null_returnsNoSelection() {
        TransactionListPanel.DisplayModel model = TransactionListPanel.displayModelFor(null);
        assertEquals("Transactions - Select a person", model.getTitle());
        assertEquals(List.of(), model.getTransactions());
    }

    @Test
    public void displayModelFor_person_buildsTitleAndSortedTransactions() {
        Transaction transactionHigh = transaction(10, "high");
        Transaction transactionLow = transaction(-5, "low");
        var person = new PersonBuilder().withName("Alex Yeoh")
                .withTransactions(transactionLow, transactionHigh).build();

        TransactionListPanel.DisplayModel model = TransactionListPanel.displayModelFor(person);
        assertEquals("Transactions (2) - Alex Yeoh", model.getTitle());
        assertEquals(List.of(transactionHigh, transactionLow), model.getTransactions());
    }

    @Test
    public void typeCellModel_emptyOrNull_returnsNulls() {
        TransactionListPanel.TypeCellModel model = TransactionListPanel.typeCellModel(null, true);
        assertNull(model.getText());
        assertNull(model.getStyleClass());
    }

    @Test
    public void typeCellModel_setsStyleClassForRecognisedTypes() {
        TransactionListPanel.TypeCellModel owe = TransactionListPanel.typeCellModel("Owe", false);
        assertEquals("Owe", owe.getText());
        assertEquals("tx-type-owe", owe.getStyleClass());

        TransactionListPanel.TypeCellModel lent = TransactionListPanel.typeCellModel("Lent", false);
        assertEquals("Lent", lent.getText());
        assertEquals("tx-type-lent", lent.getStyleClass());
    }

    @Test
    public void oneBasedIndexOf_followsIndexOfSemantics() {
        List<String> items = List.of("a", "b", "c");
        assertEquals(1, TransactionListPanel.oneBasedIndexOf(items, "a"));
        assertEquals(3, TransactionListPanel.oneBasedIndexOf(items, "c"));
        assertEquals(0, TransactionListPanel.oneBasedIndexOf(items, "x"));
    }

    @Test
    public void oneBasedIndexOf_nullItems_throws() {
        assertThrows(NullPointerException.class, () -> TransactionListPanel.oneBasedIndexOf(null, "x"));
    }
}
