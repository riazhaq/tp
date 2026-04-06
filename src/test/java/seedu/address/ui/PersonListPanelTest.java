package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonListPanelTest {

    @BeforeAll
    public static void setUpFxToolkit() throws InterruptedException {
        FxTestUtil.setUpFxToolkit();
    }

    @Test
    public void getSelectedPerson_noSelection_returnsNull() {
        Person first = new PersonBuilder().withName("Alice Pauline").build();
        Person second = new PersonBuilder().withName("Bob Choo").build();
        var persons = FXCollections.observableArrayList(first, second);
        PersonListPanel panel = FxTestUtil.onFx(() -> new PersonListPanel(persons));

        assertNull(FxTestUtil.onFx(panel::getSelectedPerson));
    }

    @Test
    public void getSelectedPerson_selectedItem_returnsSelectedPerson() {
        Person first = new PersonBuilder().withName("Alice Pauline").build();
        Person second = new PersonBuilder().withName("Bob Choo").build();
        var persons = FXCollections.observableArrayList(first, second);
        PersonListPanel panel = FxTestUtil.onFx(() -> new PersonListPanel(persons));
        ListView<Person> personListView = getField(panel, "personListView");

        FxTestUtil.onFxRun(() -> personListView.getSelectionModel().select(second));

        assertSame(second, FxTestUtil.onFx(panel::getSelectedPerson));
    }

    @Test
    public void selectionChangeListener_nullListener_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> PersonListPanel.selectionChangeListener(null));
    }

    @Test
    public void selectionChangeListener_forwardsNewSelection() {
        Person selected = new PersonBuilder().withName("Carla Gomez").build();
        final Person[] holder = new Person[1];

        PersonListPanel.selectionChangeListener(person -> holder[0] = person)
                .changed(null, null, selected);

        assertEquals(selected, holder[0]);
    }

    @Test
    public void personListViewCell_updateItem_setsAndClearsGraphic() {
        Person alice = new PersonBuilder().withName("Alice Pauline").build();
        var persons = FXCollections.observableArrayList(alice);
        PersonListPanel panel = FxTestUtil.onFx(() -> new PersonListPanel(persons));
        ListView<Person> personListView = getField(panel, "personListView");
        javafx.scene.control.ListCell<Person> cell =
                FxTestUtil.onFx(() -> personListView.getCellFactory().call(personListView));

        FxTestUtil.onFxRun(() -> invokePersonCellUpdate(cell, alice, false));
        assertNotNull(FxTestUtil.onFx(cell::getGraphic));

        FxTestUtil.onFxRun(() -> invokePersonCellUpdate(cell, null, true));
        assertNull(FxTestUtil.onFx(cell::getGraphic));
        assertNull(FxTestUtil.onFx(cell::getText));
    }

    private static void invokePersonCellUpdate(
            javafx.scene.control.ListCell<Person> cell, Person item, boolean empty) {
        try {
            java.lang.reflect.Method method =
                    cell.getClass().getDeclaredMethod("updateItem", Person.class, boolean.class);
            method.setAccessible(true);
            method.invoke(cell, item, empty);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(Object target, String fieldName) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(target);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }
}
