package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonListPanelTest {

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

}
