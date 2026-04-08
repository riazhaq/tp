package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;

/**
 * Panel containing list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);

    @FXML
    private ListView<Person> personListView;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        super(FXML);
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
    }

    /**
     * Sets a listener that is invoked whenever the selected person changes.
     *
     * @param listener listener that accepts the new selection (can be null when selection is cleared)
     */
    public void setSelectionListener(Consumer<Person> listener) {
        requireNonNull(listener);
        personListView.getSelectionModel().selectedItemProperty().addListener(
                selectionChangeListener(listener));
    }

    /**
     * Returns the currently selected person in the list, or {@code null} if no selection exists.
     */
    public Person getSelectedPerson() {
        return personListView.getSelectionModel().getSelectedItem();
    }

    /**
     * Selects the given person in the list and scrolls to it.
     * Does nothing if {@code person} is not in the list.
     */
    public void selectPerson(Person person) {
        requireNonNull(person);
        if (!personListView.getItems().contains(person)) {
            logger.warning("Tried to select a person not in the list: " + person);
            return;
        }
        personListView.getSelectionModel().select(person);
        personListView.scrollTo(person);
    }

    /**
     * Creates the ChangeListener used to notify a consumer about selection changes.
     *
     * @param listener consumer to invoke with the new selection
     * @return a ChangeListener that forwards the new selection to {@code listener}
     */
    static javafx.beans.value.ChangeListener<Person> selectionChangeListener(Consumer<Person> listener) {
        requireNonNull(listener);
        return (observable, oldValue, newValue) -> listener.accept(newValue);
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new PersonCard(person, getIndex() + 1).getRoot());
            }
        }
    }

}
