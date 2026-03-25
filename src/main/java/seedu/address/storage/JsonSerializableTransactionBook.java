package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

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
     * @throws IllegalValueException if any transaction data is invalid or a person cannot be matched
     */
    public void loadInto(AddressBook addressBook) throws IllegalValueException {
        List<Person> persons = addressBook.getPersonList();

        for (JsonAdaptedTransaction jsonTransaction : transactions) {
            Transaction transaction = jsonTransaction.toModelType();

            // Match the reconstructed debtor/creditor to the live Person objects in the model
            // so that appendTransaction is called on the actual instances, not detached copies
            Person liveDebtor = persons.stream()
                    .filter(p -> p.equals(transaction.getDebtor()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalValueException(
                            "Debtor in transaction not found in address book: "
                                    + transaction.getDebtor().getName()));

            Person liveCreditor = persons.stream()
                    .filter(p -> p.equals(transaction.getCreditor()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalValueException(
                            "Creditor in transaction not found in address book: "
                                    + transaction.getCreditor().getName()));

            liveDebtor.appendTransaction(transaction);
            liveCreditor.appendTransaction(transaction);
        }
    }
}
