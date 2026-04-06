package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
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
    public void constructor_withCustomGuiSettings_appliesWindowSizeAndCoordinates() {
        ObservableList<Person> persons = FXCollections.observableArrayList();
        FakeLogic logic = new FakeLogic(persons);
        logic.guiSettings = new GuiSettings(900, 700, 40, 60);

        MainWindow window = FxTestUtil.onFx(() -> new MainWindow(new Stage(), logic));
        Stage stage = window.getPrimaryStage();

        assertEquals(900, stage.getWidth(), 0.001);
        assertEquals(700, stage.getHeight(), 0.001);
        assertEquals(40, stage.getX(), 0.001);
        assertEquals(60, stage.getY(), 0.001);
    }

    @Test
    public void applyResultUpdate_updatesSink() {
        FakeSink sink = new FakeSink();
        MainWindow.applyResultUpdate(sink, MainWindow.successUpdate("ok"));
        assertTrue(sink.success);
        assertEquals("[SUCCESS] ok", sink.feedback);
    }

    @Test
    public void refreshTransactionPanelFromSelection_nullSelection_displaysNull() {
        ObservableList<Person> persons = FXCollections.observableArrayList();
        FakeLogic logic = new FakeLogic(persons);
        MainWindow window = FxTestUtil.onFx(() -> new MainWindow(new Stage(), logic));
        StubPersonListPanel personListPanel = FxTestUtil.onFx(() -> new StubPersonListPanel(persons));
        TrackingTransactionListPanel transactionPanel = FxTestUtil.onFx(TrackingTransactionListPanel::new);
        setField(window, "personListPanel", personListPanel);
        setField(window, "transactionListPanel", transactionPanel);

        FxTestUtil.onFxRun(() -> invoke(window, "refreshTransactionPanelFromSelection"));

        assertNull(transactionPanel.lastDisplayedPerson);
    }

    @Test
    public void refreshTransactionPanelFromSelection_rebindsToCanonicalPerson() {
        Person live = new PersonBuilder().withName("Alice Pauline").build();
        ObservableList<Person> persons = FXCollections.observableArrayList(live);
        FakeLogic logic = new FakeLogic(persons);
        MainWindow window = FxTestUtil.onFx(() -> new MainWindow(new Stage(), logic));
        StubPersonListPanel personListPanel = FxTestUtil.onFx(() -> new StubPersonListPanel(persons));
        TrackingTransactionListPanel transactionPanel = FxTestUtil.onFx(TrackingTransactionListPanel::new);
        Person detached = new PersonBuilder()
                .withName("Alice Pauline")
                .withPhone("99998888")
                .withEmail("other@example.com")
                .withAddress("Detached")
                .build();
        personListPanel.selectedPerson = detached;
        setField(window, "personListPanel", personListPanel);
        setField(window, "transactionListPanel", transactionPanel);

        FxTestUtil.onFxRun(() -> invoke(window, "refreshTransactionPanelFromSelection"));

        assertSame(live, transactionPanel.lastDisplayedPerson);
    }

    @Test
    public void executeCommand_refreshesTransactionPanelFromSelection() throws Exception {
        Person live = new PersonBuilder().withName("Alice Pauline").build();
        ObservableList<Person> persons = FXCollections.observableArrayList(live);
        FakeLogic logic = new FakeLogic(persons);
        logic.resultToReturn = new CommandResult("done");
        MainWindow window = FxTestUtil.onFx(() -> new MainWindow(new Stage(), logic));
        StubPersonListPanel personListPanel = FxTestUtil.onFx(() -> new StubPersonListPanel(persons));
        TrackingTransactionListPanel transactionPanel = FxTestUtil.onFx(TrackingTransactionListPanel::new);
        ResultDisplay resultDisplay = FxTestUtil.onFx(ResultDisplay::new);
        personListPanel.selectedPerson = live;
        setField(window, "personListPanel", personListPanel);
        setField(window, "transactionListPanel", transactionPanel);
        setField(window, "resultDisplay", resultDisplay);

        CommandResult result = FxTestUtil.onFx(() -> (CommandResult) invoke(window, "executeCommand", "settle 1 t/1"));

        assertEquals("done", result.getFeedbackToUser());
        assertSame(live, transactionPanel.lastDisplayedPerson);
        assertEquals("[SUCCESS] done", getResultText(resultDisplay));
        assertEquals("settle 1 t/1", logic.lastCommandText);
    }

    @Test
    public void executeCommand_personIndexToRefresh_displaysIndexedPerson() {
        Person first = new PersonBuilder().withName("Alice Pauline").build();
        Person second = new PersonBuilder().withName("Bob Choo").build();
        ObservableList<Person> persons = FXCollections.observableArrayList(first, second);
        FakeLogic logic = new FakeLogic(persons);
        logic.resultToReturn = new CommandResult("done", 2);
        MainWindow window = FxTestUtil.onFx(() -> new MainWindow(new Stage(), logic));
        StubPersonListPanel personListPanel = FxTestUtil.onFx(() -> new StubPersonListPanel(persons));
        TrackingTransactionListPanel transactionPanel = FxTestUtil.onFx(TrackingTransactionListPanel::new);
        ResultDisplay resultDisplay = FxTestUtil.onFx(ResultDisplay::new);
        setField(window, "personListPanel", personListPanel);
        setField(window, "transactionListPanel", transactionPanel);
        setField(window, "resultDisplay", resultDisplay);

        FxTestUtil.onFxRun(() -> invoke(window, "executeCommand", "settle 2 t/1"));

        assertSame(second, transactionPanel.lastDisplayedPerson);
        assertEquals("[SUCCESS] done", getResultText(resultDisplay));
    }

    @Test
    public void executeCommand_personIndexToRefreshOutOfBounds_keepsSelectionRefreshOnly() {
        Person first = new PersonBuilder().withName("Alice Pauline").build();
        ObservableList<Person> persons = FXCollections.observableArrayList(first);
        FakeLogic logic = new FakeLogic(persons);
        logic.resultToReturn = new CommandResult("done", 3);
        MainWindow window = FxTestUtil.onFx(() -> new MainWindow(new Stage(), logic));
        StubPersonListPanel personListPanel = FxTestUtil.onFx(() -> new StubPersonListPanel(persons));
        TrackingTransactionListPanel transactionPanel = FxTestUtil.onFx(TrackingTransactionListPanel::new);
        ResultDisplay resultDisplay = FxTestUtil.onFx(ResultDisplay::new);
        personListPanel.selectedPerson = first;
        setField(window, "personListPanel", personListPanel);
        setField(window, "transactionListPanel", transactionPanel);
        setField(window, "resultDisplay", resultDisplay);

        FxTestUtil.onFxRun(() -> invoke(window, "executeCommand", "settle 3 t/1"));

        assertSame(first, transactionPanel.lastDisplayedPerson);
        assertEquals("[SUCCESS] done", getResultText(resultDisplay));
    }

    @Test
    public void executeCommand_exitResult_hidesHelpWindowAndStageAndPersistsGuiSettings() {
        Person live = new PersonBuilder().withName("Alice Pauline").build();
        ObservableList<Person> persons = FXCollections.observableArrayList(live);
        FakeLogic logic = new FakeLogic(persons);
        logic.resultToReturn = new CommandResult("bye", false, true);
        Stage stage = FxTestUtil.onFx(Stage::new);
        MainWindow window = FxTestUtil.onFx(() -> new MainWindow(stage, logic));
        StubPersonListPanel personListPanel = FxTestUtil.onFx(() -> new StubPersonListPanel(persons));
        TrackingTransactionListPanel transactionPanel = FxTestUtil.onFx(TrackingTransactionListPanel::new);
        ResultDisplay resultDisplay = FxTestUtil.onFx(ResultDisplay::new);
        personListPanel.selectedPerson = live;
        setField(window, "personListPanel", personListPanel);
        setField(window, "transactionListPanel", transactionPanel);
        setField(window, "resultDisplay", resultDisplay);

        double expectedWidth = FxTestUtil.onFx(() -> {
            stage.setWidth(810);
            stage.setHeight(620);
            stage.setX(25);
            stage.setY(35);
            stage.show();
            return stage.getWidth();
        });
        double expectedHeight = FxTestUtil.onFx(stage::getHeight);
        int expectedX = FxTestUtil.onFx(() -> (int) stage.getX());
        int expectedY = FxTestUtil.onFx(() -> (int) stage.getY());

        FxTestUtil.onFxRun(() -> invoke(window, "executeCommand", "exit"));

        assertFalse(FxTestUtil.onFx(stage::isShowing));
        assertEquals(new GuiSettings(expectedWidth, expectedHeight, expectedX, expectedY), logic.guiSettings);
    }

    @Test
    public void executeCommand_commandException_updatesErrorDisplayAndRethrows() {
        Person live = new PersonBuilder().withName("Alice Pauline").build();
        ObservableList<Person> persons = FXCollections.observableArrayList(live);
        FakeLogic logic = new FakeLogic(persons);
        logic.commandExceptionToThrow = new CommandException("bad command");
        MainWindow window = FxTestUtil.onFx(() -> new MainWindow(new Stage(), logic));
        StubPersonListPanel personListPanel = FxTestUtil.onFx(() -> new StubPersonListPanel(persons));
        TrackingTransactionListPanel transactionPanel = FxTestUtil.onFx(TrackingTransactionListPanel::new);
        ResultDisplay resultDisplay = FxTestUtil.onFx(ResultDisplay::new);
        personListPanel.selectedPerson = live;
        setField(window, "personListPanel", personListPanel);
        setField(window, "transactionListPanel", transactionPanel);
        setField(window, "resultDisplay", resultDisplay);

        Executable executeBadCommand = () -> runExecuteCommandExpectingException(window, "bad");
        AssertionError thrown = assertThrows(AssertionError.class, executeBadCommand);

        assertTrue(thrown.getCause() instanceof CommandException);
        assertEquals("bad command", thrown.getCause().getMessage());
        assertEquals("[ERROR] bad command", getResultText(resultDisplay));
        assertNull(transactionPanel.lastDisplayedPerson);
    }

    @Test
    public void executeCommand_parseException_updatesErrorDisplayAndRethrows() {
        Person live = new PersonBuilder().withName("Alice Pauline").build();
        ObservableList<Person> persons = FXCollections.observableArrayList(live);
        FakeLogic logic = new FakeLogic(persons);
        logic.parseExceptionToThrow = new ParseException("bad parse");
        MainWindow window = FxTestUtil.onFx(() -> new MainWindow(new Stage(), logic));
        StubPersonListPanel personListPanel = FxTestUtil.onFx(() -> new StubPersonListPanel(persons));
        TrackingTransactionListPanel transactionPanel = FxTestUtil.onFx(TrackingTransactionListPanel::new);
        ResultDisplay resultDisplay = FxTestUtil.onFx(ResultDisplay::new);
        personListPanel.selectedPerson = live;
        setField(window, "personListPanel", personListPanel);
        setField(window, "transactionListPanel", transactionPanel);
        setField(window, "resultDisplay", resultDisplay);

        Executable executeBadCommand = () -> runExecuteCommandExpectingException(window, "bad parse");
        AssertionError thrown = assertThrows(AssertionError.class, executeBadCommand);

        assertTrue(thrown.getCause() instanceof ParseException);
        assertEquals("bad parse", thrown.getCause().getMessage());
        assertEquals("[ERROR] bad parse", getResultText(resultDisplay));
        assertNull(transactionPanel.lastDisplayedPerson);
    }

    @Test
    public void fillInnerParts_populatesUiAndUpdatesActiveContacts() {
        Person first = new PersonBuilder().withName("Alice Pauline").build();
        ObservableList<Person> persons = FXCollections.observableArrayList(first);
        FakeLogic logic = new FakeLogic(persons);
        MainWindow window = FxTestUtil.onFx(() -> new MainWindow(new Stage(), logic));

        FxTestUtil.onFxRun(window::fillInnerParts);

        javafx.scene.control.Label activeContactsLabel = getField(window, "activeContactsLabel");

        assertNotNull(getField(window, "personListPanel"));
        assertNotNull(getField(window, "transactionListPanel"));
        assertNotNull(getField(window, "resultDisplay"));
        assertEquals("Active Contacts (1)", activeContactsLabel.getText());

        Person second = new PersonBuilder().withName("Bob Choo").build();
        FxTestUtil.onFxRun(() -> persons.add(second));
        assertEquals("Active Contacts (2)", activeContactsLabel.getText());
    }

    @Test
    public void show_makesStageVisible() {
        ObservableList<Person> persons = FXCollections.observableArrayList();
        FakeLogic logic = new FakeLogic(persons);
        Stage stage = FxTestUtil.onFx(Stage::new);
        MainWindow window = FxTestUtil.onFx(() -> new MainWindow(stage, logic));

        FxTestUtil.onFxRun(window::show);

        assertTrue(FxTestUtil.onFx(stage::isShowing));
        FxTestUtil.onFxRun(stage::hide);
    }

    @Test
    public void getPersonListPanel_afterFillInnerParts_returnsPanel() {
        ObservableList<Person> persons = FXCollections.observableArrayList(
                new PersonBuilder().withName("Alice Pauline").build());
        FakeLogic logic = new FakeLogic(persons);
        MainWindow window = FxTestUtil.onFx(() -> new MainWindow(new Stage(), logic));
        FxTestUtil.onFxRun(window::fillInnerParts);

        PersonListPanel expected = getField(window, "personListPanel");
        assertSame(expected, window.getPersonListPanel());
    }

    @Test
    public void handleHelp_whenNotShowing_opensHelpWindow() {
        ObservableList<Person> persons = FXCollections.observableArrayList();
        FakeLogic logic = new FakeLogic(persons);
        MainWindow window = FxTestUtil.onFx(() -> new MainWindow(new Stage(), logic));
        HelpWindow helpWindow = getField(window, "helpWindow");

        assertFalse(FxTestUtil.onFx(helpWindow::isShowing));
        FxTestUtil.onFxRun(window::handleHelp);

        assertTrue(FxTestUtil.onFx(helpWindow::isShowing));
        FxTestUtil.onFxRun(helpWindow::hide);
    }

    @Test
    public void handleHelp_whenAlreadyShowing_remainsVisibleWithoutError() {
        ObservableList<Person> persons = FXCollections.observableArrayList();
        FakeLogic logic = new FakeLogic(persons);
        MainWindow window = FxTestUtil.onFx(() -> new MainWindow(new Stage(), logic));
        HelpWindow helpWindow = getField(window, "helpWindow");

        FxTestUtil.onFxRun(helpWindow::show);
        assertTrue(FxTestUtil.onFx(helpWindow::isShowing));

        FxTestUtil.onFxRun(window::handleHelp);
        assertTrue(FxTestUtil.onFx(helpWindow::isShowing));
        FxTestUtil.onFxRun(helpWindow::hide);
    }

    private static Object invoke(Object target, String methodName, Object... args) {
        try {
            Class<?>[] types = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                types[i] = args[i].getClass();
            }
            Method method = target.getClass().getDeclaredMethod(methodName, types);
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }

    private static Object invokeExpectingException(Object target, String methodName, Object... args)
            throws CommandException, ParseException {
        try {
            Class<?>[] types = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                types[i] = args[i].getClass();
            }
            Method method = target.getClass().getDeclaredMethod(methodName, types);
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof CommandException) {
                throw (CommandException) cause;
            }
            if (cause instanceof ParseException) {
                throw (ParseException) cause;
            }
            throw new AssertionError(cause);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }

    private static void runExecuteCommandExpectingException(MainWindow window, String commandText) {
        FxTestUtil.onFx(() -> invokeExpectingException(window, "executeCommand", commandText));
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

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }

    private static String getResultText(ResultDisplay resultDisplay) {
        javafx.scene.control.TextArea area = getField(resultDisplay, "resultDisplay");
        return area.getText();
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
        private final ObservableList<Person> persons;
        private CommandResult resultToReturn = new CommandResult("ok");
        private CommandException commandExceptionToThrow;
        private ParseException parseExceptionToThrow;
        private String lastCommandText;
        private GuiSettings guiSettings = new GuiSettings();

        private FakeLogic(ObservableList<Person> persons) {
            this.persons = persons;
        }

        @Override
        public CommandResult execute(String commandText) throws CommandException, ParseException {
            lastCommandText = commandText;
            if (commandExceptionToThrow != null) {
                throw commandExceptionToThrow;
            }
            if (parseExceptionToThrow != null) {
                throw parseExceptionToThrow;
            }
            return resultToReturn;
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return null;
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            return persons;
        }

        @Override
        public Path getAddressBookFilePath() {
            return Path.of("data", "addressbook.json");
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

    private static class StubPersonListPanel extends PersonListPanel {
        private Person selectedPerson;

        private StubPersonListPanel(ObservableList<Person> personList) {
            super(personList);
        }

        @Override
        public Person getSelectedPerson() {
            return selectedPerson;
        }
    }

    private static class TrackingTransactionListPanel extends TransactionListPanel {
        private Person lastDisplayedPerson;

        @Override
        public void displayPerson(Person person) {
            super.displayPerson(person);
            lastDisplayedPerson = person;
        }
    }

}
