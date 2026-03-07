package seedu.address.model.loan;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public abstract class Loan {

    protected double currAmount;
    protected final InterestRate interestRate;
    protected LocalDate lastRecalculatedDate;

    public Loan(double currAmount, InterestRate interestRate) {
        this.currAmount = currAmount;
        this.interestRate = interestRate;
        this.lastRecalculatedDate = LocalDate.now();
    }

    public abstract void updateLoanAmount();

    public void payLoan(double amount) {
        if (!this.lastRecalculatedDate.equals(LocalDate.now())) {
            this.updateLoanAmount();
        }

        currAmount -= amount;

        updateLastRecalculatedDate();
    }

    public long getNumberOfMonthsSinceLastPaid() {
        LocalDate currDate = LocalDate.now();
        return ChronoUnit.MONTHS.between(lastRecalculatedDate, currDate);
    }

    public long getNumberOfYearsSinceLastPaid() {
        LocalDate currDate = LocalDate.now();
        return ChronoUnit.YEARS.between(lastRecalculatedDate, currDate);
    }

    protected void updateLastRecalculatedDate() {
        this.lastRecalculatedDate = LocalDate.now();
    }
}
