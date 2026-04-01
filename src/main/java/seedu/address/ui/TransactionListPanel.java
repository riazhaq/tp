package seedu.address.ui;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.MonthlyTransaction;
import seedu.address.model.transaction.Transaction;
import seedu.address.model.transaction.YearlyTransaction;

/**
 * Panel that displays transactions (transactions) for the currently selected person.
 */
public class TransactionListPanel extends UiPart<Region> {

    private static final String FXML = "TransactionListPanel.fxml";

    private static final String NO_SELECTION_TITLE = "Transactions - Select a person";
    private static final String STATUS_PENDING = "Pending";
    private static final String COMPOUNDING_NONE = "None";
    private static final String COMPOUNDING_MONTHLY = "Monthly";
    private static final String COMPOUNDING_YEARLY = "Yearly";
    private static final String DIRECTION_OWE = "Owe";
    private static final String DIRECTION_LENT = "Lent";
    private static final String TYPE_OWE = "Owe";
    private static final String TYPE_LENT = "Lent";
    private static final String STYLE_TX_OWE = "tx-type-owe";
    private static final String STYLE_TX_LENT = "tx-type-lent";

    static final class DisplayModel {
        private final String title;
        private final List<Transaction> transactions;

        private DisplayModel(String title, List<Transaction> transactions) {
            this.title = title;
            this.transactions = transactions;
        }

        String getTitle() {
            return title;
        }

        List<Transaction> getTransactions() {
            return transactions;
        }
    }

    static final class TypeCellModel {
        private final String text;
        private final String styleClass;

        private TypeCellModel(String text, String styleClass) {
            this.text = text;
            this.styleClass = styleClass;
        }

        String getText() {
            return text;
        }

        String getStyleClass() {
            return styleClass;
        }
    }

    @FXML
    private Label title;

    @FXML
    private TableView<Transaction> transactionTable;

    @FXML
    private TableColumn<Transaction, Number> indexColumn;

    @FXML
    private TableColumn<Transaction, String> compoundingColumn;

    @FXML
    private TableColumn<Transaction, String> directionColumn;

    @FXML
    private TableColumn<Transaction, String> otherPartyColumn;

    @FXML
    private TableColumn<Transaction, String> amountColumn;

    @FXML
    private TableColumn<Transaction, String> descriptionColumn;

    @FXML
    private TableColumn<Transaction, String> statusColumn;

    @FXML
    private TableColumn<Transaction, String> dateColumn;

    private Person currentPerson;

    /**
     * Creates a transaction list panel showing no selection initially.
     */
    public TransactionListPanel() {
        super(FXML);
        showNoSelection();
    }

    @FXML
    private void initialize() {
        transactionTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        indexColumn.setCellValueFactory(cellData ->
                indexCellValue(transactionTable.getItems(), cellData.getValue()));

        compoundingColumn.setCellValueFactory(cellData -> {
            return new ReadOnlyStringWrapper(compoundingType(cellData.getValue()));
        });

        directionColumn.setCellValueFactory(cellData -> {
            return new ReadOnlyStringWrapper(directionText(cellData.getValue()));
        });
        directionColumn.setCellFactory(col -> new DirectionCell());

        otherPartyColumn.setCellValueFactory(cellData -> {
            return new ReadOnlyStringWrapper(otherPartyName(cellData.getValue()));
        });

        amountColumn.setCellValueFactory(cellData -> {
            return new ReadOnlyStringWrapper(amountText(cellData.getValue().getCurrAmount()));
        });

        descriptionColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(descriptionText(cellData.getValue())));

        statusColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(statusText()));

        dateColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(dateText(cellData.getValue())));
    }

    /**
     * Displays the transactions (transactions) belonging to the given person.
     *
     * @param person The selected person. If null, the panel resets to the no-selection state.
     */
    public void displayPerson(Person person) {
        this.currentPerson = person;
        DisplayModel model = displayModelFor(person);
        title.setText(model.getTitle());
        transactionTable.setItems(FXCollections.observableArrayList(model.getTransactions()));
    }

    private void showNoSelection() {
        DisplayModel model = displayModelFor(null);
        title.setText(model.getTitle());
        transactionTable.setItems(FXCollections.observableArrayList(model.getTransactions()));
    }

    /**
     * Returns the title shown when no person is selected.
     */
    static String noSelectionTitle() {
        return NO_SELECTION_TITLE;
    }

    /**
     * Returns the label shown in the status column.
     */
    static String statusText() {
        return STATUS_PENDING;
    }

    /**
     * Returns the display text for a transaction type based on the current amount.
     * @deprecated Use directionText() instead for role-based direction display.
     */
    @Deprecated
    static String typeText(double amount) {
        return amount >= 0 ? TYPE_OWE : TYPE_LENT;
    }

    /**
     * Formats the amount as an absolute currency string (e.g. "$12.50").
     */
    static String amountText(double amount) {
        return String.format("$%.2f", Math.abs(amount));
    }

    /**
     * Returns the description text shown in the table.
     */
    static String descriptionText(Transaction transaction) {
        Objects.requireNonNull(transaction);
        return transaction.getDescription();
    }

    /**
     * Returns the date text shown in the table.
     */
    static String dateText(Transaction transaction) {
        Objects.requireNonNull(transaction);
        return transaction.getLastRecalculatedDate().toString();
    }

    /**
     * Sorts transactions by current amount in descending order.
     */
    static List<Transaction> sortedTransactions(Iterable<Transaction> transactions) {
        Objects.requireNonNull(transactions);
        return StreamSupport.stream(transactions.spliterator(), false)
                .sorted(Comparator.comparingDouble(Transaction::getCurrAmount).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Builds the display model for the given person.
     * If {@code person} is null, returns the no-selection state.
     */
    static DisplayModel displayModelFor(Person person) {
        if (person == null) {
            return new DisplayModel(noSelectionTitle(), List.of());
        }

        String displayTitle = UiMessages.transactionsTitle(person.getTransactions().size(), person.getName().fullName);
        List<Transaction> transactions = sortedTransactions(person.getTransactions());
        return new DisplayModel(displayTitle, transactions);
    }

    /**
     * Computes the 1-based index of {@code value} within {@code items}, using {@code List#indexOf}.
     * Returns 0 if not found.
     */
    static int oneBasedIndexOf(List<?> items, Object value) {
        Objects.requireNonNull(items);
        return items.indexOf(value) + 1;
    }

    static ReadOnlyObjectWrapper<Number> indexCellValue(List<Transaction> items, Transaction value) {
        return new ReadOnlyObjectWrapper<>(oneBasedIndexOf(items, value));
    }

    /**
     * Returns the display text for compounding type based on transaction type.
     */
    static String compoundingType(Transaction transaction) {
        if (transaction instanceof MonthlyTransaction) {
            return COMPOUNDING_MONTHLY;
        } else if (transaction instanceof YearlyTransaction) {
            return COMPOUNDING_YEARLY;
        } else {
            return COMPOUNDING_NONE;
        }
    }

    /**
     * Returns the display text for direction based on person's role in the transaction.
     */
    String directionText(Transaction transaction) {
        if (currentPerson == null) {
            return "";
        }
        if (transaction.getDebtor().equals(currentPerson)) {
            return DIRECTION_OWE;
        } else {
            return DIRECTION_LENT;
        }
    }

    /**
     * Returns the name of the other party in the transaction.
     */
    String otherPartyName(Transaction transaction) {
        if (currentPerson == null) {
            return "";
        }
        if (transaction.getDebtor().equals(currentPerson)) {
            return transaction.getCreditor().getName().fullName;
        } else {
            return transaction.getDebtor().getName().fullName;
        }
    }

    /**
     * Maps a direction label to the corresponding style class.
     *
     * @return style class name, or null if the label is not recognised.
     */
    static String styleClassForDirection(String directionLabel) {
        if (directionLabel == null) {
            return null;
        }
        if (DIRECTION_OWE.equalsIgnoreCase(directionLabel)) {
            return STYLE_TX_OWE;
        }
        if (DIRECTION_LENT.equalsIgnoreCase(directionLabel)) {
            return STYLE_TX_LENT;
        }
        return null;
    }

    /**
     * Maps a type label to the corresponding style class.
     * @return style class name, or null if the label is not recognised.
     * @deprecated Use styleClassForDirection() instead.
     */
    @Deprecated
    static String styleClassForType(String typeLabel) {
        if (typeLabel == null) {
            return null;
        }
        if (TYPE_OWE.equalsIgnoreCase(typeLabel)) {
            return STYLE_TX_OWE;
        }
        if (TYPE_LENT.equalsIgnoreCase(typeLabel)) {
            return STYLE_TX_LENT;
        }
        return null;
    }

    static TypeCellModel directionCellModel(String item, boolean empty) {
        if (empty || item == null) {
            return new TypeCellModel(null, null);
        }
        return new TypeCellModel(item, styleClassForDirection(item));
    }

    private class DirectionCell extends TableCell<Transaction, String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            getStyleClass().removeAll(STYLE_TX_OWE, STYLE_TX_LENT);

            TypeCellModel model = directionCellModel(item, empty);
            setText(model.getText());
            if (model.getStyleClass() != null) {
                getStyleClass().add(model.getStyleClass());
            }
        }
    }

    /**
     * @deprecated Use directionCellModel() instead.
     */
    @Deprecated
    static TypeCellModel typeCellModel(String item, boolean empty) {
        if (empty || item == null) {
            return new TypeCellModel(null, null);
        }
        return new TypeCellModel(item, styleClassForType(item));
    }

    /**
     * @deprecated Use DirectionCell instead.
     */
    @Deprecated
    private static class TransactionTypeCell extends TableCell<Transaction, String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            getStyleClass().removeAll(STYLE_TX_OWE, STYLE_TX_LENT);

            TypeCellModel model = typeCellModel(item, empty);
            setText(model.getText());
            if (model.getStyleClass() != null) {
                getStyleClass().add(model.getStyleClass());
            }
        }
    }
}
