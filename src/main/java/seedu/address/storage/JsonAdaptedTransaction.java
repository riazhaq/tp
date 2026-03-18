package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.transaction.MonthlyTransaction;
import seedu.address.model.transaction.Transaction;
import seedu.address.model.transaction.YearlyTransaction;

/**
 * Jackson-friendly version of {@link Transaction}.
 */
class JsonAdaptedTransaction {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Transaction details are missing!";

    private final String transactionType; // "m" for monthly, "y" for yearly, "" for generic
    private final double amount;
    private final double rate;
    private final String description;

    /**
     * Constructs a {@code JsonAdaptedTransaction} with the given transaction details.
     */
    @JsonCreator
    public JsonAdaptedTransaction(@JsonProperty("transactionType") String transactionType,
                                  @JsonProperty("amount") double amount,
                                  @JsonProperty("rate") double rate,
                                  @JsonProperty("description") String description) {
        this.transactionType = transactionType == null ? "" : transactionType;
        this.amount = amount;
        this.rate = rate;
        this.description = description;
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
        rate = source.getInterest();
        description = source.getDescription();
    }

    public String getTransactionType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public double getRate() {
        return rate;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Converts this Jackson-friendly adapted transaction object into the model's {@code Transaction} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public Transaction toModelType() throws IllegalValueException {
        try {
            switch (transactionType) {
            case "m":
                return new MonthlyTransaction(amount, rate, description);
            case "y":
                return new YearlyTransaction(amount, rate, description);
            default:
                return new Transaction(amount, rate, description); // generic transaction
            }
        } catch (Exception e) {
            throw new IllegalValueException("Invalid transaction data: " + e.getMessage());
        }
    }
}
