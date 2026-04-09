package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.Transaction;

/**
 * Settles a specific transaction while keeping it in history.
 */
public class SettleCommand extends Command {

    public static final String COMMAND_WORD = "settle";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks a specific transaction as settled while keeping it in history.\n"
            + "Parameters: INDEX (must be a positive integer) t/TRANS_INDEX\n"
            + "Example: " + COMMAND_WORD + " 1 t/2";

    public static final String MESSAGE_SETTLE_TRANSACTION_SUCCESS =
            "Settled Transaction #%1$d: %2$s";
    public static final String MESSAGE_NO_TRANSACTIONS = "No transactions found for %1$s.";
    public static final String MESSAGE_INVALID_TRANSACTION_DISPLAYED_INDEX =
            "The transaction index provided is invalid";
    public static final String MESSAGE_ALREADY_SETTLED =
            "This transaction has already been settled.";

    private final Index targetIndex;
    private final Index targetTransactionIndex;

    /**
     * Creates a SettleCommand to settle a specific transaction of a person.
     */
    public SettleCommand(Index targetIndex, Index targetTransactionIndex) {
        requireNonNull(targetIndex);
        requireNonNull(targetTransactionIndex);
        this.targetIndex = targetIndex;
        this.targetTransactionIndex = targetTransactionIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToModify = lastShownList.get(targetIndex.getZeroBased());
        return settleTransaction(model, personToModify);
    }

    private CommandResult settleTransaction(Model model, Person person) throws CommandException {
        List<Transaction> transactions = person.getTransactions().stream()
                .sorted(model.getTransactionComparator())
                .collect(Collectors.toList());

        if (transactions.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_NO_TRANSACTIONS, person.getName()));
        }

        if (targetTransactionIndex.getZeroBased() >= transactions.size()) {
            throw new CommandException(MESSAGE_INVALID_TRANSACTION_DISPLAYED_INDEX);
        }

        Transaction transactionToSettle = transactions.get(targetTransactionIndex.getZeroBased());
        if (transactionToSettle.isSettled()) {
            throw new CommandException(MESSAGE_ALREADY_SETTLED);
        }

        double amountBeforeSettling = transactionToSettle.getCurrAmount();
        transactionToSettle.settleTransaction();

        String transactionDetails = formatTransaction(transactionToSettle, amountBeforeSettling);
        return new CommandResult(
                String.format(
                        MESSAGE_SETTLE_TRANSACTION_SUCCESS,
                        targetTransactionIndex.getOneBased(),
                        transactionDetails
                ),
                targetIndex.getOneBased()
        );
    }

    private String formatTransaction(Transaction t, double settledAmount) {
        return String.format("$%.2f | %s | %s -> %s",
                settledAmount,
                t.getDescription(),
                t.getDebtor().getName(),
                t.getCreditor().getName());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SettleCommand)) {
            return false;
        }
        SettleCommand otherSettleCommand = (SettleCommand) other;
        return targetIndex.equals(otherSettleCommand.targetIndex)
                && targetTransactionIndex.equals(otherSettleCommand.targetTransactionIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("transactionIndex", targetTransactionIndex)
                .toString();
    }
}
