package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AMOUNT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COMPOUNDING_TYPE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INTEREST_RATE;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.transaction.MonthlyTransaction;
import seedu.address.model.transaction.Transaction;
import seedu.address.model.transaction.TransactionDescriptor;
import seedu.address.model.transaction.TransactionDescriptor.CompoundingType;
import seedu.address.model.transaction.YearlyTransaction;

/**
 * Adds a transaction between two existing persons in the address book.
 *
 * <p>Both persons are identified by their one-based index in the currently
 * displayed person list. The debtor owes money to the creditor.
 */
public class AddTransactionCommand extends Command {

    public static final String COMMAND_WORD = "addtxn";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a transaction between two persons identified by their index numbers "
            + "in the displayed person list.\n"
            + "Parameters: DEBTOR_INDEX CREDITOR_INDEX (both must be positive integers) "
            + PREFIX_AMOUNT + "AMOUNT "
            + PREFIX_INTEREST_RATE + "INTEREST_RATE "
            + "[" + PREFIX_DESCRIPTION + "DESCRIPTION] "
            + "[" + PREFIX_COMPOUNDING_TYPE + "COMPOUNDING_TYPE(m/y/n)]\n"
            + "Example: " + COMMAND_WORD + " 2 3 "
            + PREFIX_AMOUNT + "10 "
            + PREFIX_INTEREST_RATE + "5 "
            + PREFIX_DESCRIPTION + "lunch "
            + PREFIX_COMPOUNDING_TYPE + "m";

    public static final String MESSAGE_SUCCESS = "New transaction added: %1$s owes %2$s — %3$s";
    public static final String MESSAGE_SAME_PERSON =
            "Debtor and creditor cannot be the same person.";
    public static final String MESSAGE_INVALID_DEBTOR_INDEX =
            "The debtor index provided is invalid or out of range.";
    public static final String MESSAGE_INVALID_CREDITOR_INDEX =
            "The creditor index provided is invalid or out of range.";

    private final Index debtorIndex;
    private final Index creditorIndex;
    private final TransactionDescriptor descriptor;

    /**
     * Creates an AddTransactionCommand.
     *
     * @param debtorIndex   one-based index of the debtor in the filtered person list
     * @param creditorIndex one-based index of the creditor in the filtered person list
     * @param descriptor    the parsed transaction fields
     */
    public AddTransactionCommand(Index debtorIndex, Index creditorIndex, TransactionDescriptor descriptor) {
        requireNonNull(debtorIndex);
        requireNonNull(creditorIndex);
        requireNonNull(descriptor);
        this.debtorIndex = debtorIndex;
        this.creditorIndex = creditorIndex;
        this.descriptor = descriptor;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> lastShownList = model.getFilteredPersonList();

        if (debtorIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_INVALID_DEBTOR_INDEX);
        }
        if (creditorIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_INVALID_CREDITOR_INDEX);
        }
        if (debtorIndex.equals(creditorIndex)) {
            throw new CommandException(MESSAGE_SAME_PERSON);
        }

        Person debtor = lastShownList.get(debtorIndex.getZeroBased());
        Person creditor = lastShownList.get(creditorIndex.getZeroBased());

        Transaction transaction = buildTransaction(debtor, creditor, descriptor);

        debtor.appendTransaction(transaction);
        creditor.appendTransaction(transaction);

        return new CommandResult(String.format(MESSAGE_SUCCESS,
                Messages.format(debtor), Messages.format(creditor), descriptor.getDescription()));
    }

    /**
     * Builds the correct {@code Transaction} subtype from a {@code TransactionDescriptor}.
     */
    private static Transaction buildTransaction(Person debtor, Person creditor, TransactionDescriptor descriptor) {
        CompoundingType type = descriptor.getCompoundingType();
        double amount = descriptor.getAmount();
        double rate = descriptor.getRate();
        String description = descriptor.getDescription();

        if (type == CompoundingType.MONTHLY) {
            return new MonthlyTransaction(debtor, creditor, amount, rate, description);
        } else if (type == CompoundingType.YEARLY) {
            return new YearlyTransaction(debtor, creditor, amount, rate, description);
        } else {
            return new Transaction(debtor, creditor, amount, rate, description);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddTransactionCommand)) {
            return false;
        }

        AddTransactionCommand otherCommand = (AddTransactionCommand) other;
        return debtorIndex.equals(otherCommand.debtorIndex)
                && creditorIndex.equals(otherCommand.creditorIndex)
                && descriptor.equals(otherCommand.descriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("debtorIndex", debtorIndex)
                .add("creditorIndex", creditorIndex)
                .add("descriptor", descriptor)
                .toString();
    }
}
