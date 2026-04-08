package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.MonthlyTransaction;
import seedu.address.model.transaction.Transaction;
import seedu.address.model.transaction.YearlyTransaction;

/**
 * Jackson-friendly version of {@link Transaction}.
 * Embeds full {@link JsonAdaptedPerson} objects for debtor and creditor.
 * This is safe because {@link JsonAdaptedPerson} no longer stores transactions,
 * so there is no circular reference.
 */
class JsonAdaptedTransaction {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Transaction's %s field is missing!";

    private final String transactionType;
    private final double amount;
    private final double originalAmount;
    private final double rate;
    private final String description;
    private final JsonAdaptedPerson debtor;
    private final JsonAdaptedPerson creditor;
    private final boolean settled;

    /**
     * Constructs a {@code JsonAdaptedTransaction} with the given transaction details.
     */
    @JsonCreator
    public JsonAdaptedTransaction(@JsonProperty("transactionType") String transactionType,
                                  @JsonProperty("amount") double amount,
                                  @JsonProperty("originalAmount") Double originalAmount,
                                  @JsonProperty("rate") double rate,
                                  @JsonProperty("description") String description,
                                  @JsonProperty("debtor") JsonAdaptedPerson debtor,
                                  @JsonProperty("creditor") JsonAdaptedPerson creditor,
                                  @JsonProperty("settled") Boolean settled) {
        this.transactionType = transactionType == null ? "" : transactionType;
        this.amount = amount;
        this.originalAmount = (originalAmount != null) ? originalAmount : amount;
        this.rate = rate;
        this.description = description;
        this.debtor = debtor;
        this.creditor = creditor;
        this.settled = settled != null && settled;
    }

    /**
     * Converts a given {@code Transaction} into this class for Jackson use.
     */
    public JsonAdaptedTransaction(Transaction source) {
        if (source instanceof MonthlyTransaction) {
            transactionType = "m";
        } else if (source instanceof YearlyTransaction) {
            transactionType = "y";
        } else {
            transactionType = "";
        }
        amount = source.getCurrAmount();
        originalAmount = source.getOriginalAmount();
        rate = source.getInterest();
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

        Transaction transaction;
        switch (transactionType) {
        case "m":
            transaction = new MonthlyTransaction(modelDebtor, modelCreditor, amount, rate, description);
            break;
        case "y":
            transaction = new YearlyTransaction(modelDebtor, modelCreditor, amount, rate, description);
            break;
        default:
            transaction = new Transaction(modelDebtor, modelCreditor, amount, rate, description);
            break;
        }

        transaction.setSettled(settled);
        transaction.setOriginalAmount(originalAmount);
        return transaction;
    }
}
