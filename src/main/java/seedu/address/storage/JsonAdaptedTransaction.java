package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.Transaction;

/**
 * Jackson-friendly version of {@link Transaction}.
 * Embeds full {@link JsonAdaptedPerson} objects for debtor and creditor.
 * This is safe because {@link JsonAdaptedPerson} no longer stores transactions,
 * so there is no circular reference.
 */
class JsonAdaptedTransaction {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Transaction's %s field is missing!";

    private final double amount;
    private final String description;
    private final JsonAdaptedPerson debtor;
    private final JsonAdaptedPerson creditor;
    private final boolean settled;

    /**
     * Constructs a {@code JsonAdaptedTransaction} with the given transaction details.
     */
    @JsonCreator
    public JsonAdaptedTransaction(@JsonProperty("amount") double amount,
                                  @JsonProperty("description") String description,
                                  @JsonProperty("debtor") JsonAdaptedPerson debtor,
                                  @JsonProperty("creditor") JsonAdaptedPerson creditor,
                                  @JsonProperty("settled") Boolean settled) {
        this.amount = amount;
        this.description = description;
        this.debtor = debtor;
        this.creditor = creditor;
        this.settled = settled != null && settled;
    }

    /**
     * Converts a given {@code Transaction} into this class for Jackson use.
     */
    public JsonAdaptedTransaction(Transaction source) {
        amount = source.getCurrAmount();
        description = source.getDescription();
        debtor = new JsonAdaptedPerson(source.getDebtor());
        creditor = new JsonAdaptedPerson(source.getCreditor());
        settled = source.isSettled();
    }

    /**
     * Converts this Jackson-friendly adapted transaction object into the model's {@code Transaction} object.
     * The debtor and creditor are reconstructed directly from their embedded person data.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public Transaction toModelType() throws IllegalValueException {
        if (debtor == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "debtor"));
        }
        if (creditor == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "creditor"));
        }
        if (description == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "description"));
        }

        Person modelDebtor = debtor.toModelType();
        Person modelCreditor = creditor.toModelType();

        Transaction transaction = new Transaction(modelDebtor, modelCreditor, amount, description);
        transaction.setSettled(settled);
        return transaction;
    }
}
