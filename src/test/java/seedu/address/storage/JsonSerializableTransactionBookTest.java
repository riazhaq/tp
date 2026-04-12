package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.Transaction;
import seedu.address.testutil.PersonBuilder;

public class JsonSerializableTransactionBookTest {

    @Test
    public void loadInto_transactionsAreReboundToLivePersons() throws Exception {
        Person sourceDebtor = new PersonBuilder().withName("Alice Pauline")
                .withPhone("91111111")
                .withEmail("alice@example.com")
                .withAddress("Alice Street")
                .build();
        Person sourceCreditor = new PersonBuilder().withName("Bob Choo")
                .withPhone("92222222")
                .withEmail("bob@example.com")
                .withAddress("Bob Street")
                .build();
        Transaction transaction = new Transaction(sourceDebtor, sourceCreditor, 42.0, "Lunch");
        transaction.setSettled(true);

        sourceDebtor.appendTransaction(transaction);
        sourceCreditor.appendTransaction(transaction);

        AddressBook sourceBook = new AddressBook();
        sourceBook.addPerson(sourceDebtor);
        sourceBook.addPerson(sourceCreditor);

        JsonSerializableTransactionBook serializableBook =
                new JsonSerializableTransactionBook(sourceBook);

        Person liveDebtor = new PersonBuilder(sourceDebtor).withTransactions().build();
        Person liveCreditor = new PersonBuilder(sourceCreditor).withTransactions().build();

        AddressBook targetBook = new AddressBook();
        targetBook.addPerson(liveDebtor);
        targetBook.addPerson(liveCreditor);

        serializableBook.loadInto(targetBook);

        assertEquals(1, liveDebtor.getTransactions().size());

        Transaction loaded = liveDebtor.getTransactions().iterator().next();

        assertSame(loaded, liveCreditor.getTransactions().iterator().next());
        assertSame(liveDebtor, loaded.getDebtor());
        assertSame(liveCreditor, loaded.getCreditor());
        assertEquals(42.0, loaded.getCurrAmount(), 0.001);
        assertEquals("Lunch", loaded.getDescription());
        assertTrue(loaded.isSettled());
    }

    @Test
    public void loadInto_missingMatchedPerson_skipsInvalidTransaction() {
        Person debtor = new PersonBuilder().withName("Alice Pauline")
                .withPhone("91111111")
                .withEmail("alice@example.com")
                .withAddress("Alice Street")
                .build();
        Person creditor = new PersonBuilder().withName("Bob Choo")
                .withPhone("92222222")
                .withEmail("bob@example.com")
                .withAddress("Bob Street")
                .build();

        JsonAdaptedTransaction adaptedTransaction = new JsonAdaptedTransaction(
                10.0, 0.0, "Lunch", new JsonAdaptedPerson(debtor), new JsonAdaptedPerson(creditor), false);
        JsonSerializableTransactionBook serializableBook =
                new JsonSerializableTransactionBook(List.of(adaptedTransaction));

        AddressBook targetBook = new AddressBook();
        Person liveDebtor = new PersonBuilder(debtor).build();
        targetBook.addPerson(liveDebtor);

        serializableBook.loadInto(targetBook);

        assertTrue(liveDebtor.getTransactions().isEmpty());
    }
}
