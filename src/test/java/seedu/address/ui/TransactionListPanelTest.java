package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.Transaction;
import seedu.address.testutil.PersonBuilder;

public class TransactionListPanelTest {

    private static final String ALEX = "Alex Yeoh";
    private static final String BERNICE = "Bernice Yu";
    private static boolean isFxToolkitAvailable = true;

    @BeforeAll
    public static void setUpFxToolkit() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (UnsupportedOperationException unsupportedEnvironment) {
            isFxToolkitAvailable = false;
            return;
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
        return new Transaction(debtor, creditor, amount, description);
    }

    private static Person person(String name) {
        if (name.equals(ALEX)) {
            return new PersonBuilder().withName(name).withPhone("91111111")
                    .withEmail("alex@example.com").withAddress("Alex Street").build();
        } else if (name.equals(BERNICE)) {
            return new PersonBuilder().withName(name).withPhone("92222222")
                    .withEmail("bernice@example.com").withAddress("Bernice Street").build();
        } else {
            return new PersonBuilder().build();
        }
    }

    private static Person personWithTransactions(String name, Transaction... transactions) {
        return new PersonBuilder().withName(name).withTransactions(transactions).build();
    }

    private static <T> T onFx(ThrowingSupplier<T> supplier) {
        Assumptions.assumeTrue(isFxToolkitAvailable,
                "Skipping JavaFX-dependent test because toolkit is unavailable in this environment");

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
    public void statusText_transactionPending_returnsPending() {
        Transaction transaction = transaction(10, "Dinner");
        assertEquals("Pending", TransactionListPanel.statusText(transaction));
    }

    @Test
    public void statusText_transactionSettled_returnsSettled() {
        Transaction transaction = transaction(10, "Dinner");
        transaction.setSettled(true);
        assertEquals("Settled", TransactionListPanel.statusText(transaction));
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
    public void dateText_returnsDate() {
        Transaction transaction = transaction(10, "Dinner");
        assertEquals(transaction.getDate().toString(), TransactionListPanel.dateText(transaction));
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

    @Test
    public void directionTextAndOtherPartyName_sameIdentityDifferentInstance_consistent() {
        Person debtorAlice = person(ALEX);
        Person creditorBernice = person(BERNICE);

        Person selectedAlice = new PersonBuilder()
                .withName(debtorAlice.getName().toString())
                .withPhone(debtorAlice.getPhone().toString())
                .withEmail(debtorAlice.getEmail().toString())
                .withAddress(debtorAlice.getAddress().toString())
                .build();

        Transaction owesTransaction = transaction(debtorAlice, creditorBernice, 12.5, "Dinner");
        Person displayedPerson = personWithTransactions(ALEX, owesTransaction);

        TransactionListPanel panel = onFx(TransactionListPanel::new);
        onFxRun(() -> panel.displayPerson(selectedAlice));

        assertEquals("Owe", panel.directionText(owesTransaction));
        assertEquals("Bernice Yu", panel.otherPartyName(owesTransaction));

        onFxRun(() -> panel.displayPerson(displayedPerson));
        assertEquals("Owe", panel.directionText(owesTransaction));
        assertEquals("Bernice Yu", panel.otherPartyName(owesTransaction));
    }

    @Test
    public void directionTextAndOtherPartyName_personNotInTransaction_emptyString() {
        Person notInTransaction = person("Charlotte Oliveiro");
        Person debtor = person(ALEX);
        Person creditor = person(BERNICE);
        Transaction transaction = transaction(debtor, creditor, 10.0, "Snack");

        TransactionListPanel panel = onFx(TransactionListPanel::new);
        onFxRun(() -> panel.displayPerson(notInTransaction));

        assertEquals("", panel.directionText(transaction));
        assertEquals("", panel.otherPartyName(transaction));
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
        TableColumn<Transaction, Number> indexColumn = getField(panel, "indexColumn");
        TableColumn<Transaction, String> directionColumn = getField(panel, "directionColumn");
        TableColumn<Transaction, String> otherPartyColumn = getField(panel, "otherPartyColumn");
        TableColumn<Transaction, String> amountColumn = getField(panel, "amountColumn");

        assertSame(TableView.UNCONSTRAINED_RESIZE_POLICY, transactionTable.getColumnResizePolicy());
        assertFalse(indexColumn.isSortable());
        assertEquals(TableColumn.SortType.DESCENDING, amountColumn.getSortType());
        assertTrue(amountColumn.getComparator().compare("$2.00", "$10.00") < 0);
        assertNotNull(directionColumn.getCellValueFactory());
        assertNotNull(directionColumn.getCellFactory());
        assertNotNull(otherPartyColumn.getCellValueFactory());

        onFxRun(() -> panel.displayPerson(displayedPerson));

        assertSame(displayedPerson, getField(panel, "currentPerson"));
        assertEquals(1, transactionTable.getItems().size());
    }

    @Test
    public void directionCellModel_setsStyleClassForRecognisedDirections() {
        TransactionListPanel.TypeCellModel owe = TransactionListPanel.directionCellModel("Owe", false);
        assertEquals("Owe", owe.getText());
        assertEquals("tx-type-owe", owe.getStyleClass());

        TransactionListPanel.TypeCellModel lent = TransactionListPanel.directionCellModel("Lent", false);
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

    @Test
    public void indexCellValue_wrapsOneBasedIndex() {
        Transaction t1 = transaction(5, "a");
        Transaction t2 = transaction(3, "b");
        List<Transaction> items = List.of(t1, t2);
        assertEquals(1, TransactionListPanel.indexCellValue(items, null, t1).getValue().intValue());
        assertEquals(2, TransactionListPanel.indexCellValue(items, null, t2).getValue().intValue());
        assertEquals(0,
                TransactionListPanel.indexCellValue(items, null, transaction(1, "missing")).getValue().intValue());
    }

    @Test
    public void indexCellValue_usesComparatorOrdering() {
        Transaction first = transaction(5, "bravo");
        Transaction second = transaction(3, "alpha");
        List<Transaction> items = List.of(first, second);
        Comparator<Transaction> byDescription = Comparator.comparing(Transaction::getDescription);

        assertEquals(2, TransactionListPanel.indexCellValue(items, byDescription, first).getValue().intValue());
        assertEquals(1, TransactionListPanel.indexCellValue(items, byDescription, second).getValue().intValue());
    }

    @Test
    public void statusCell_updateItem_settledAppliesStyle() {
        TransactionListPanel panel = onFx(TransactionListPanel::new);
        TableColumn<Transaction, String> statusColumn = getField(panel, "statusColumn");

        javafx.scene.control.TableCell<Transaction, String> cell =
                onFx(() -> statusColumn.getCellFactory().call(statusColumn));
        onFxRun(() -> invokeUpdateItem(cell, String.class, "Settled", false));

        assertEquals("Settled", onFx(cell::getText));
        assertTrue(onFx(() -> cell.getStyleClass().contains("tx-status-settled")));

        onFxRun(() -> invokeUpdateItem(cell, String.class, null, true));
        assertNull(onFx(cell::getText));
    }

    @Test
    public void transactionRow_updateItem_settledAppliesStyle() {
        TransactionListPanel panel = onFx(TransactionListPanel::new);
        TableView<Transaction> transactionTable = getField(panel, "transactionTable");
        Transaction transaction = transaction(10, "Dinner");
        transaction.setSettled(true);

        TableRow<Transaction> row = onFx(() -> transactionTable.getRowFactory().call(transactionTable));
        onFxRun(() -> invokeUpdateItem(row, Transaction.class, transaction, false));

        assertTrue(onFx(() -> row.getStyleClass().contains("tx-row-settled")));

        onFxRun(() -> invokeUpdateItem(row, Transaction.class, null, true));
        assertFalse(onFx(() -> row.getStyleClass().contains("tx-row-settled")));
    }

    @Test
    public void directionCell_updateItem_appliesStyleAndClearsOnEmpty() {
        TransactionListPanel panel = onFx(TransactionListPanel::new);
        TableColumn<Transaction, String> directionColumn = getField(panel, "directionColumn");
        javafx.scene.control.TableCell<Transaction, String> cell =
                onFx(() -> directionColumn.getCellFactory().call(directionColumn));

        onFxRun(() -> invokeUpdateItem(cell, String.class, "Owe", false));
        assertEquals("Owe", onFx(cell::getText));
        assertTrue(onFx(() -> cell.getStyleClass().contains("tx-type-owe")));
        assertFalse(onFx(() -> cell.getStyleClass().contains("tx-type-lent")));

        onFxRun(() -> invokeUpdateItem(cell, String.class, "Lent", false));
        assertEquals("Lent", onFx(cell::getText));
        assertTrue(onFx(() -> cell.getStyleClass().contains("tx-type-lent")));
        assertFalse(onFx(() -> cell.getStyleClass().contains("tx-type-owe")));

        onFxRun(() -> invokeUpdateItem(cell, String.class, null, true));
        assertNull(onFx(cell::getText));
    }

    @Test
    public void cellValueFactories_invokedForFirstTableItem() {
        Person debtor = person(ALEX);
        Person creditor = person(BERNICE);
        Transaction oweTransaction = transaction(debtor, creditor, 12.5, "Dinner");
        Person displayedPerson = personWithTransactions(ALEX, oweTransaction);
        TransactionListPanel panel = onFx(TransactionListPanel::new);
        onFxRun(() -> panel.displayPerson(displayedPerson));

        TableColumn<Transaction, Number> indexColumn = getField(panel, "indexColumn");
        TableColumn<Transaction, String> directionColumn = getField(panel, "directionColumn");
        TableColumn<Transaction, String> otherPartyColumn = getField(panel, "otherPartyColumn");
        TableColumn<Transaction, String> amountColumn = getField(panel, "amountColumn");
        TableColumn<Transaction, String> descriptionColumn = getField(panel, "descriptionColumn");
        TableColumn<Transaction, String> statusColumn = getField(panel, "statusColumn");
        TableColumn<Transaction, String> dateColumn = getField(panel, "dateColumn");

        assertEquals(1, onFx(() -> indexColumn.getCellObservableValue(0)).getValue().intValue());
        assertEquals("Owe", onFx(() -> directionColumn.getCellObservableValue(0)).getValue());
        assertEquals(BERNICE, onFx(() -> otherPartyColumn.getCellObservableValue(0)).getValue());
        assertEquals("$12.50", onFx(() -> amountColumn.getCellObservableValue(0)).getValue());
        assertEquals("Dinner", onFx(() -> descriptionColumn.getCellObservableValue(0)).getValue());
        assertEquals("Pending", onFx(() -> statusColumn.getCellObservableValue(0)).getValue());
        assertNotNull(onFx(() -> dateColumn.getCellObservableValue(0)).getValue());
    }

    @Test
    public void amountColumn_settledTransaction_showsOriginalAmount() {
        Person debtor = person(ALEX);
        Person creditor = person(BERNICE);
        Transaction settledTransaction = transaction(debtor, creditor, 75.0, "Settled debt");
        settledTransaction.settleTransaction(); // currAmount → 0, originalAmount stays 75.0
        Person displayedPerson = personWithTransactions(ALEX, settledTransaction);

        TransactionListPanel panel = onFx(TransactionListPanel::new);
        onFxRun(() -> panel.displayPerson(displayedPerson));

        TableColumn<Transaction, String> amountColumn = getField(panel, "amountColumn");
        assertEquals("$75.00", onFx(() -> amountColumn.getCellObservableValue(0)).getValue());
    }

    private static void invokeUpdateItem(Object target, Class<?> parameterType, Object item, boolean empty) {
        try {
            Method method = target.getClass().getDeclaredMethod("updateItem", parameterType, boolean.class);
            method.setAccessible(true);
            method.invoke(target, item, empty);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
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
