package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.loan.Loan;
import seedu.address.model.loan.MonthlyLoan;
import seedu.address.model.loan.YearlyLoan;

/**
 * Jackson-friendly version of {@link Loan}.
 */
class JsonAdaptedLoan {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Loan details are missing!";

    private final String loanType; // "m" for monthly, "y" for yearly, "" for generic
    private final double amount;
    private final double rate;
    private final String description;

    /**
     * Constructs a {@code JsonAdaptedLoan} with the given loan details.
     */
    @JsonCreator
    public JsonAdaptedLoan(@JsonProperty("loanType") String loanType,
                           @JsonProperty("amount") double amount,
                           @JsonProperty("rate") double rate,
                           @JsonProperty("description") String description) {
        this.loanType = loanType == null ? "" : loanType;
        this.amount = amount;
        this.rate = rate;
        this.description = description;
    }

    /**
     * Converts a given {@code Loan} into this class for Jackson use.
     */
    public JsonAdaptedLoan(Loan source) {
        if (source instanceof MonthlyLoan) {
            loanType = "m";
        } else if (source instanceof YearlyLoan) {
            loanType = "y";
        } else {
            loanType = "";
        }
        amount = source.getCurrAmount();
        rate = source.getInterest();
        description = source.getDescription();
    }

    public String getLoanType() {
        return loanType;
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
     * Converts this Jackson-friendly adapted loan object into the model's {@code Loan} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public Loan toModelType() throws IllegalValueException {
        try {
            switch (loanType) {
            case "m":
                return new MonthlyLoan(amount, rate, description);
            case "y":
                return new YearlyLoan(amount, rate, description);
            default:
                return new Loan(amount, rate, description); // generic loan
            }
        } catch (Exception e) {
            throw new IllegalValueException("Invalid loan data: " + e.getMessage());
        }
    }
}
