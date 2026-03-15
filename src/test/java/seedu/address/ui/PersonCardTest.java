package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;
import seedu.address.model.loan.Loan;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class PersonCardTest {

    @BeforeAll
    public static void initJavaFx() throws Exception {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException alreadyStarted) {
            // JavaFX runtime already initialized by other tests.
        }
    }

    @Test
    public void constructor_noLoans_showsZeroBalance() throws Exception {
        Person person = new PersonBuilder().withLoans().build();

        PersonCard card = createOnFxThread(() -> new PersonCard(person, 1));
        assertEquals("Balance: $0.00", getBalanceLabel(card).getText());
    }

    @Test
    public void constructor_positiveLoan_showsYouOwe() throws Exception {
        Person person = new PersonBuilder()
                .withLoans(new Loan(12.5, 0, "Dinner"))
                .build();

        PersonCard card = createOnFxThread(() -> new PersonCard(person, 1));
        assertEquals("You owe: $12.50", getBalanceLabel(card).getText());
    }

    @Test
    public void constructor_negativeLoan_showsTheyOweYou() throws Exception {
        Person person = new PersonBuilder()
                .withLoans(new Loan(-45.0, 0, "Project Lunch"))
                .build();

        PersonCard card = createOnFxThread(() -> new PersonCard(person, 1));
        assertEquals("They owe you: $45.00", getBalanceLabel(card).getText());
    }

    @Test
    public void constructor_multipleLoans_showsSummedBalance() throws Exception {
        Person person = new PersonBuilder()
                .withLoans(
                        new Loan(20.0, 0, "Lunch"),
                        new Loan(-5.0, 0, "Coffee"))
                .build();

        PersonCard card = createOnFxThread(() -> new PersonCard(person, 1));
        assertEquals("You owe: $15.00", getBalanceLabel(card).getText());
    }

    private static Label getBalanceLabel(PersonCard card) throws Exception {
        Field field = PersonCard.class.getDeclaredField("balance");
        field.setAccessible(true);
        return (Label) field.get(card);
    }

    private static <T> T createOnFxThread(java.util.concurrent.Callable<T> factory) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final Object[] resultHolder = new Object[1];
        final Exception[] exceptionHolder = new Exception[1];

        Platform.runLater(() -> {
            try {
                resultHolder[0] = factory.call();
            } catch (Exception e) {
                exceptionHolder[0] = e;
            } finally {
                latch.countDown();
            }
        });

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        if (!completed) {
            throw new AssertionError("Timed out waiting for JavaFX thread.");
        }
        if (exceptionHolder[0] != null) {
            throw exceptionHolder[0];
        }
        @SuppressWarnings("unchecked")
        T result = (T) resultHolder[0];
        return result;
    }
}

