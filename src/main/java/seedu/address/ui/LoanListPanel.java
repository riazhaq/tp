package seedu.address.ui;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import seedu.address.model.loan.Loan;
import seedu.address.model.person.Person;

/**
 * Panel that displays transactions (loans) for the currently selected person.
 */
public class LoanListPanel extends UiPart<Region> {

    private static final String FXML = "LoanListPanel.fxml";

    private static final String NO_SELECTION_TITLE = "Transactions - Select a person";
    private static final String STATUS_PENDING = "Pending";
    private static final String TYPE_OWE = "Owe";
    private static final String TYPE_LENT = "Lent";
    private static final String STYLE_TX_OWE = "tx-type-owe";
    private static final String STYLE_TX_LENT = "tx-type-lent";

    @FXML
    private Label title;

    @FXML
    private TableView<Loan> loanTable;

    @FXML
    private TableColumn<Loan, Number> indexColumn;

    @FXML
    private TableColumn<Loan, String> typeColumn;

    @FXML
    private TableColumn<Loan, String> amountColumn;

    @FXML
    private TableColumn<Loan, String> descriptionColumn;

    @FXML
    private TableColumn<Loan, String> statusColumn;

    @FXML
    private TableColumn<Loan, String> dateColumn;

    /**
     * Creates a loan list panel showing no selection initially.
     */
    public LoanListPanel() {
        super(FXML);
        showNoSelection();
    }

    @FXML
    private void initialize() {
        indexColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(loanTable.getItems().indexOf(cellData.getValue()) + 1));

        typeColumn.setCellValueFactory(cellData -> {
            double amount = cellData.getValue().getCurrAmount();
            return new ReadOnlyStringWrapper(typeText(amount));
        });
        typeColumn.setCellFactory(col -> new TransactionTypeCell());

        amountColumn.setCellValueFactory(cellData -> {
            double amount = cellData.getValue().getCurrAmount();
            return new ReadOnlyStringWrapper(amountText(amount));
        });

        descriptionColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getDescription()));

        statusColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(statusText()));

        dateColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(dateText(cellData.getValue().getLastRecalculatedDate().toString())));
    }

    /**
     * Displays the transactions (loans) belonging to the given person.
     *
     * @param person The selected person. If null, the panel resets to the no-selection state.
     */
    public void displayPerson(Person person) {
        if (person == null) {
            showNoSelection();
            return;
        }

        title.setText(UiMessages.transactionsTitle(person.getLoans().size(), person.getName().fullName));

        ObservableList<Loan> items = FXCollections.observableArrayList();
        sortedLoans(person.getLoans()).forEach(items::add);
        loanTable.setItems(items);
    }

    private void showNoSelection() {
        title.setText(noSelectionTitle());
        loanTable.setItems(FXCollections.observableArrayList());
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
     * Returns the display text for a loan type based on the current amount.
     */
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
     * Returns the date text shown in the table.
     */
    static String dateText(String dateString) {
        return Objects.requireNonNull(dateString);
    }

    /**
     * Sorts loans by current amount in descending order.
     */
    static List<Loan> sortedLoans(Iterable<Loan> loans) {
        Objects.requireNonNull(loans);
        return StreamSupport.stream(loans.spliterator(), false)
                .sorted(Comparator.comparingDouble(Loan::getCurrAmount).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Maps a type label to the corresponding style class.
     *
     * @return style class name, or null if the label is not recognised.
     */
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

    private static class TransactionTypeCell extends TableCell<Loan, String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            getStyleClass().removeAll(STYLE_TX_OWE, STYLE_TX_LENT);

            if (empty || item == null) {
                setText(null);
                return;
            }

            setText(item);
            String styleClass = styleClassForType(item);
            if (styleClass != null) {
                getStyleClass().add(styleClass);
            }
        }
    }
}
