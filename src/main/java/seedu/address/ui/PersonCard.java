package seedu.address.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.Transaction;

/**
 * An UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";
    private static final double NEAR_ZERO_THRESHOLD = 0.005;
    private static final String STYLE_DEBTS_OWE = "debts-owe";
    private static final String STYLE_DEBTS_LENT = "debts-lent";

    static final class ActiveDebtsModel {
        private final String amountText;
        private final String suffixText;
        private final String styleClass;

        private ActiveDebtsModel(String amountText, String suffixText, String styleClass) {
            this.amountText = amountText;
            this.suffixText = suffixText;
            this.styleClass = styleClass;
        }

        String getAmountText() {
            return amountText;
        }

        String getSuffixText() {
            return suffixText;
        }

        String getStyleClass() {
            return styleClass;
        }
    }

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label debtsPrefix;
    @FXML
    private Label debtsAmount;
    @FXML
    private Label debtsSuffix;
    @FXML
    private FlowPane tags;

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        address.setText(person.getAddress().value);
        email.setText(person.getEmail().value);
        setActiveDebts(person);
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
    }

    static String formatBalance(Person person) {
        double amountOwed = 0.0;
        double amountOwedTo = 0.0;

        for (Transaction transaction : person.getTransactions()) {
            if (transaction.getDebtor().equals(person)) {
                // Person is the debtor, so they owe this amount
                amountOwed += transaction.getCurrAmount();
            } else if (transaction.getCreditor().equals(person)) {
                // Person is the creditor, so this amount is owed to them
                amountOwedTo += transaction.getCurrAmount();
            }
        }

        double netBalance = amountOwedTo - amountOwed;

        if (person.getTransactions().isEmpty() || Math.abs(netBalance) < NEAR_ZERO_THRESHOLD) {
            return "Balance: $0.00";
        }

        if (netBalance < 0) {
            return String.format("You owe: $%.2f", Math.abs(netBalance));
        }

        return String.format("They owe you: $%.2f", netBalance);
    }

    private void setActiveDebts(Person person) {
        ActiveDebtsModel model = activeDebtsModelFor(person);
        debtsAmount.getStyleClass().removeAll(STYLE_DEBTS_OWE, STYLE_DEBTS_LENT);
        debtsAmount.setText(model.getAmountText());
        debtsSuffix.setText(model.getSuffixText());
        if (model.getStyleClass() != null) {
            debtsAmount.getStyleClass().add(model.getStyleClass());
        }
    }

    static ActiveDebtsModel activeDebtsModelFor(Person person) {
        double amountOwed = 0.0;
        double amountOwedTo = 0.0;

        for (Transaction transaction : person.getTransactions()) {
            if (transaction.getDebtor().equals(person)) {
                // Person is the debtor, so they owe this amount
                amountOwed += transaction.getCurrAmount();
            } else if (transaction.getCreditor().equals(person)) {
                // Person is the creditor, so this amount is owed to them
                amountOwedTo += transaction.getCurrAmount();
            }
        }

        double netBalance = amountOwedTo - amountOwed;

        if (person.getTransactions().isEmpty() || Math.abs(netBalance) < NEAR_ZERO_THRESHOLD) {
            return new ActiveDebtsModel("$0.00", "", null);
        }

        if (netBalance < 0) {
            // Person owes money
            return new ActiveDebtsModel(String.format("-$%.2f", Math.abs(netBalance)), "(Owe)", STYLE_DEBTS_OWE);
        }

        // Person is owed money
        return new ActiveDebtsModel(String.format("+$%.2f", netBalance), "(Lent)", STYLE_DEBTS_LENT);
    }
}
