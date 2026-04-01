package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.MonthlyTransaction;
import seedu.address.model.transaction.Transaction;
import seedu.address.model.transaction.YearlyTransaction;
import seedu.address.testutil.PersonBuilder;

public class TransactionListPanelTest {

    private static final String ALEX = "Alex Yeoh";
    private static final String BERNICE = "Bernice Yu";

    @BeforeAll
    public static void setUpFxToolkit() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException alreadyStarted) {
            latch.countDown();
        }
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new AssertionError("Timed out starting JavaFX toolkit");
        }
    }

    private static Transaction transaction(double amount, String description) {
        return transaction(person("Debtor"), person("Creditor"), amount, description);
    }

    private static Transaction transaction(Person debtor, Person creditor, double amount, String description) {
        return new Transaction(debtor, creditor, amount, 0, description);
    }

    private static Person person(String name) {
        return new PersonBuilder().withName(name).build();
    }

    private static Person personWithTransactions(String name, Transaction... transactions) {
        return new PersonBuilder().withName(name).withTransactions(transactions).build();
    }

    private static MonthlyTransaction monthlyTransaction(double amount, String description) {
        return new MonthlyTransaction(
                new PersonBuilder().withName("Debtor").build(),
                new PersonBuilder().withName("Creditor").build(),
                amount,
                1.0,
                description);
    }

    private static YearlyTransaction yearlyTransaction(double amount, String description) {
        return new YearlyTransaction(
                new PersonBuilder().withName("Debtor").build(),
                new PersonBuilder().withName("Creditor").build(),
                amount,
                1.0,
                description);
    }

    private static <T> T onFx(ThrowingSupplier<T> supplier) {
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                result.set(supplier.get());
            } catch (Throwable throwable) {
                error.set(throwable);
            } finally {
                latch.countDown();
            }
        });

        try {
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new AssertionError("Timed out waiting for JavaFX operation");
            }
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new AssertionError(interruptedException);
        }

        if (error.get() != null) {
            throw new AssertionError(error.get());
        }
        return result.get();
    }

    private static void onFxRun(ThrowingRunnable runnable) {
        onFx(() -> {
            runnable.run();
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }

    // Basic static helpers

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
    public void compoundingType_recognisesTransactionSubtypes() {
        assertEquals("None", TransactionListPanel.compoundingType(transaction(10, "Dinner")));
        assertEquals("Monthly", TransactionListPanel.compoundingType(monthlyTransaction(10, "Rent")));
        assertEquals("Yearly", TransactionListPanel.compoundingType(yearlyTransaction(10, "Loan")));
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
    public void styleClassForDirection_recognisesOweAndLent() {
        assertEquals("tx-type-owe", TransactionListPanel.styleClassForDirection("Owe"));
        assertEquals("tx-type-owe", TransactionListPanel.styleClassForDirection("owe"));
        assertEquals("tx-type-lent", TransactionListPanel.styleClassForDirection("Lent"));
        assertEquals("tx-type-lent", TransactionListPanel.styleClassForDirection("lent"));
        assertNull(TransactionListPanel.styleClassForDirection(null));
        assertNull(TransactionListPanel.styleClassForDirection("Other"));
    }

    // Display model and list utilities

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
        var person = personWithTransactions(ALEX, transactionLow, transactionHigh);

        TransactionListPanel.DisplayModel model = TransactionListPanel.displayModelFor(person);
        assertEquals("Transactions (2) - Alex Yeoh", model.getTitle());
        assertEquals(List.of(transactionHigh, transactionLow), model.getTransactions());
    }

    // Role-based direction/other-party behavior

    @Test
    public void directionText_noCurrentPerson_returnsEmptyString() {
        TransactionListPanel panel = onFx(TransactionListPanel::new);
        assertEquals("", panel.directionText(transaction(10, "Dinner")));
    }

    @Test
    public void directionText_currentPersonMatchesRole_returnsExpectedLabel() {
        Person currentPerson = person(ALEX);
        Person otherPerson = person(BERNICE);
        Transaction oweTransaction = transaction(currentPerson, otherPerson, 12.5, "Dinner");
        Transaction lentTransaction = transaction(otherPerson, currentPerson, 8.0, "Coffee");
        Person displayedPerson = personWithTransactions(ALEX, oweTransaction, lentTransaction);

        TransactionListPanel panel = onFx(TransactionListPanel::new);
        onFxRun(() -> panel.displayPerson(displayedPerson));

        assertEquals("Owe", panel.directionText(oweTransaction));
        assertEquals("Lent", panel.directionText(lentTransaction));
    }

    @Test
    public void otherPartyName_noCurrentPerson_returnsEmptyString() {
        TransactionListPanel panel = onFx(TransactionListPanel::new);
        assertEquals("", panel.otherPartyName(transaction(10, "Dinner")));
    }

    @Test
    public void otherPartyName_currentPersonMatchesRole_returnsOtherPartyName() {
        Person currentPerson = person(ALEX);
        Person otherPerson = person(BERNICE);
        Transaction oweTransaction = transaction(currentPerson, otherPerson, 12.5, "Dinner");
        Transaction lentTransaction = transaction(otherPerson, currentPerson, 8.0, "Coffee");
        Person displayedPerson = personWithTransactions(ALEX, oweTransaction, lentTransaction);

        TransactionListPanel panel = onFx(TransactionListPanel::new);
        onFxRun(() -> panel.displayPerson(displayedPerson));

        assertEquals("Bernice Yu", panel.otherPartyName(oweTransaction));
        assertEquals("Bernice Yu", panel.otherPartyName(lentTransaction));
    }

    // FXML initialize coverage

    @Test
    public void constructor_andDisplayPerson_initialiseColumnsAndCurrentPerson() {
        Person debtor = person(ALEX);
        Person creditor = person(BERNICE);
        Transaction transaction = transaction(debtor, creditor, 12.5, "Dinner");
        Person displayedPerson = personWithTransactions(ALEX, transaction);

        TransactionListPanel panel = onFx(TransactionListPanel::new);

        TableView<Transaction> transactionTable = getField(panel, "transactionTable");
        TableColumn<Transaction, String> compoundingColumn = getField(panel, "compoundingColumn");
        TableColumn<Transaction, String> directionColumn = getField(panel, "directionColumn");
        TableColumn<Transaction, String> otherPartyColumn = getField(panel, "otherPartyColumn");

        assertSame(TableView.UNCONSTRAINED_RESIZE_POLICY, transactionTable.getColumnResizePolicy());
        assertNotNull(compoundingColumn.getCellValueFactory());
        assertNotNull(directionColumn.getCellValueFactory());
        assertNotNull(directionColumn.getCellFactory());
        assertNotNull(otherPartyColumn.getCellValueFactory());

        onFxRun(() -> panel.displayPerson(displayedPerson));

        assertSame(displayedPerson, getField(panel, "currentPerson"));
        assertEquals(1, transactionTable.getItems().size());
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

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
