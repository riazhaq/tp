package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.Transaction;

/**
 * An immutable list of transactions that is serializable to JSON format.
 * Stored separately from persons to avoid transactions being embedded inside persons.
 * Each transaction embeds its own full debtor and creditor data, which is safe
 * because {@link JsonAdaptedPerson} no longer stores transactions.
 */
@JsonRootName(value = "transactionbook")
class JsonSerializableTransactionBook {

    private static final Logger logger = LogsCenter.getLogger(JsonSerializableTransactionBook.class);

    private final List<JsonAdaptedTransaction> transactions = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableTransactionBook} with the given transactions.
     */
    @JsonCreator
    public JsonSerializableTransactionBook(
            @JsonProperty("transactions") List<JsonAdaptedTransaction> transactions) {
        if (transactions != null) {
            this.transactions.addAll(transactions);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use,
     * collecting all unique transactions across all persons.
     */
    public JsonSerializableTransactionBook(ReadOnlyAddressBook source) {
        source.getPersonList().stream()
                .flatMap(person -> person.getTransactions().stream())
                .distinct()
                .map(JsonAdaptedTransaction::new)
                .forEach(transactions::add);
    }

    /**
     * Loads all transactions and appends them to the relevant persons in the address book.
     * Each transaction's debtor and creditor are reconstructed from their embedded data,
     * then matched against the address book's person list by equality to obtain the live
     * {@code Person} references already stored in the model.
     *
     * @param addressBook the already-loaded address book whose persons will receive transactions
     */
    public void loadInto(AddressBook addressBook) {
        List<Person> persons = addressBook.getPersonList();
        int skippedTransactions = 0;

        for (JsonAdaptedTransaction jsonTransaction : transactions) {
            final Transaction transaction;
            try {
                transaction = jsonTransaction.toModelType();
            } catch (IllegalValueException ive) {
                skippedTransactions++;
                logger.warning("Skipping invalid transaction entry: " + ive.getMessage());
                continue;
            }

            // Match the reconstructed debtor/creditor to the live Person objects in the model
            // so that appendTransaction is called on the actual instances, not detached copies
            Person liveDebtor = persons.stream()
                    .filter(p -> p.equals(transaction.getDebtor()) || p.isSamePerson(transaction.getDebtor()))
                    .findFirst()
                    .orElse(null);

            Person liveCreditor = persons.stream()
                    .filter(p -> p.equals(transaction.getCreditor()) || p.isSamePerson(transaction.getCreditor()))
                    .findFirst()
                    .orElse(null);

            if (liveDebtor == null || liveCreditor == null) {
                skippedTransactions++;
                logger.warning("Skipping transaction with missing person reference(s): debtor="
                        + transaction.getDebtor().getName() + ", creditor=" + transaction.getCreditor().getName());
                continue;
            }

            // Rebind to live person instances to keep transaction perspective and
            // downstream command updates consistent across the app lifecycle.
            transaction.setDebtor(liveDebtor);
            transaction.setCreditor(liveCreditor);

            liveDebtor.appendTransaction(transaction);
            liveCreditor.appendTransaction(transaction);
        }

        if (skippedTransactions > 0) {
            logger.warning("Skipped " + skippedTransactions + " invalid transaction(s) while loading data.");
        }
    }
}
