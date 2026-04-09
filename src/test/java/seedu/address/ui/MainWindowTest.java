package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.Transaction;
import seedu.address.testutil.PersonBuilder;

public class MainWindowTest {

    @BeforeAll
    public static void setUpFxToolkit() throws InterruptedException {
        FxTestUtil.setUpFxToolkit();
    }

    @Test
    public void successUpdate_marksSuccessAndFormatsFeedback() {
        MainWindow.ResultDisplayUpdate update = MainWindow.successUpdate("done");
        assertTrue(update.isSuccess());
        assertEquals("[SUCCESS] done", update.feedback());
    }

    @Test
    public void errorUpdate_marksFailureAndFormatsFeedback() {
        MainWindow.ResultDisplayUpdate update = MainWindow.errorUpdate("bad");
        assertFalse(update.isSuccess());
        assertEquals("[ERROR] bad", update.feedback());
    }

    @Test
    public void applyResultUpdate_updatesSink() {
        FakeSink sink = new FakeSink();
        MainWindow.applyResultUpdate(sink, MainWindow.successUpdate("ok"));
        assertTrue(sink.success);
        assertEquals("[SUCCESS] ok", sink.feedback);
    }

    @Test
    public void fillInnerParts_registersTransactionSortListenerAndUpdatesOnSortChange() {
        FakeLogic logic = new FakeLogic();
        MainWindow window = FxTestUtil.onFx(() -> new MainWindow(new Stage(), logic));

        FxTestUtil.onFxRun(window::fillInnerParts);

        TransactionListPanel transactionListPanel = getField(window, "transactionListPanel");
        assertNotNull(transactionListPanel);
        assertEquals(Transaction.descendingByCurrentAmount(), logic.transactionComparator);

        Person debtor = new PersonBuilder().withName("Debtor").build();
        Person creditor = new PersonBuilder().withName("Creditor").build();
        Transaction alphaTransaction = new Transaction(debtor, creditor, 5.0, "alpha");
        Transaction betaTransaction = new Transaction(debtor, creditor, 10.0, "beta");
        Person displayedPerson = new PersonBuilder(debtor).withTransactions(alphaTransaction, betaTransaction).build();

        FxTestUtil.onFxRun(() -> transactionListPanel.displayPerson(displayedPerson));

        TableView<Transaction> transactionTable = getField(transactionListPanel, "transactionTable");
        TableColumn<Transaction, String> descriptionColumn = getField(transactionListPanel, "descriptionColumn");

        FxTestUtil.onFxRun(() -> {
            transactionTable.getSortOrder().setAll(descriptionColumn);
            transactionTable.sort();
        });

        assertTrue(logic.transactionComparator.compare(alphaTransaction, betaTransaction) < 0);
    }

    private static <T> T getField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            T value = (T) field.get(target);
            return value;
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }

    private static final class FakeSink implements MainWindow.ResultDisplaySink {
        private boolean success;
        private String feedback;

        @Override
        public void setCommandSuccess(boolean isSuccess) {
            success = isSuccess;
        }

        @Override
        public void setFeedbackToUser(String feedbackToUser) {
            feedback = feedbackToUser;
        }
    }

    private static final class FakeLogic implements Logic {
        private final ObservableList<Person> filteredPersons = FXCollections.observableArrayList();
        private final ReadOnlyAddressBook addressBook = new AddressBook();
        private final Path addressBookFilePath = Paths.get("addressBook.json");
        private GuiSettings guiSettings = new GuiSettings();
        private Comparator<Transaction> transactionComparator = Transaction.descendingByCurrentAmount();

        @Override
        public CommandResult execute(String commandText) throws CommandException, ParseException {
            throw new UnsupportedOperationException("Not used in this test.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return addressBook;
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            return filteredPersons;
        }

        @Override
        public void setTransactionComparator(Comparator<Transaction> comparator) {
            transactionComparator = comparator;
        }

        @Override
        public Path getAddressBookFilePath() {
            return addressBookFilePath;
        }

        @Override
        public GuiSettings getGuiSettings() {
            return guiSettings;
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            this.guiSettings = guiSettings;
        }
    }

}
