package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.loan.Loan;

public class LoanListPanelTest {

    @Test
    public void noSelectionTitle_matchesExpected() {
        assertEquals("Transactions - Select a person", LoanListPanel.noSelectionTitle());
    }

    @Test
    public void statusText_isPending() {
        assertEquals("Pending", LoanListPanel.statusText());
    }

    @Test
    public void typeText_amountPositive_isOwe() {
        assertEquals("Owe", LoanListPanel.typeText(12.34));
        assertEquals("Owe", LoanListPanel.typeText(0));
    }

    @Test
    public void typeText_amountNegative_isLent() {
        assertEquals("Lent", LoanListPanel.typeText(-0.01));
    }

    @Test
    public void amountText_formatsAbsoluteCurrency() {
        assertEquals("$12.50", LoanListPanel.amountText(12.5));
        assertEquals("$12.50", LoanListPanel.amountText(-12.5));
        assertEquals("$0.00", LoanListPanel.amountText(0));
    }

    @Test
    public void dateText_null_throws() {
        assertThrows(NullPointerException.class, () -> LoanListPanel.dateText(null));
    }

    @Test
    public void styleClassForType_null_returnsNull() {
        assertNull(LoanListPanel.styleClassForType(null));
    }

    @Test
    public void styleClassForType_recognisesOweAndLent() {
        assertEquals("tx-type-owe", LoanListPanel.styleClassForType("Owe"));
        assertEquals("tx-type-owe", LoanListPanel.styleClassForType("owe"));
        assertEquals("tx-type-lent", LoanListPanel.styleClassForType("Lent"));
        assertEquals("tx-type-lent", LoanListPanel.styleClassForType("lent"));
        assertNull(LoanListPanel.styleClassForType("Other"));
    }

    @Test
    public void sortedLoans_sortsDescendingByAmount() {
        Loan loanHigh = new Loan(10, 0, "high");
        Loan loanMid = new Loan(7, 0, "mid");
        Loan loanLow = new Loan(-5, 0, "low");

        List<Loan> sorted = LoanListPanel.sortedLoans(Set.of(loanLow, loanMid, loanHigh));
        assertEquals(List.of(loanHigh, loanMid, loanLow), sorted);
    }
}

