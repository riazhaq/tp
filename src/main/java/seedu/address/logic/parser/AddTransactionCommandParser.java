package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.AddTransactionCommand.MESSAGE_INVALID_CREDITOR_INDEX;
import static seedu.address.logic.commands.AddTransactionCommand.MESSAGE_INVALID_DEBTOR_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AMOUNT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DESCRIPTION;

import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddTransactionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.transaction.TransactionDescriptor;

/**
 * Parses input arguments and creates a new AddTransactionCommand object.
 *
 * <p>Expected format:
 * {@code addtxn DEBTOR_INDEX CREDITOR_INDEX a/AMOUNT d/DESCRIPTION}
 * e.g. {@code addtxn 2 3 a/10 d/lunch}
 *
 * <p>The debtor and creditor indices refer to the one-based positions of persons
 * currently displayed in the person list.
 */
public class AddTransactionCommandParser implements Parser<AddTransactionCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddTransactionCommand
     * and returns an AddTransactionCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddTransactionCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                args, PREFIX_AMOUNT, PREFIX_DESCRIPTION);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_AMOUNT,
                PREFIX_DESCRIPTION);

        String preamble = argMultimap.getPreamble().trim();
        String[] indices = preamble.split("\\s+", 2);

        if (indices.length != 2) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddTransactionCommand.MESSAGE_USAGE));
        }

        Index debtorIndex;
        Index creditorIndex;

        try {
            debtorIndex = ParserUtil.parseIndex(indices[0]);
        } catch (ParseException pe) {
            throw new ParseException(
                    MESSAGE_INVALID_DEBTOR_INDEX + "\n" + AddTransactionCommand.MESSAGE_USAGE, pe);
        }

        try {
            creditorIndex = ParserUtil.parseIndex(indices[1]);
        } catch (ParseException pe) {
            throw new ParseException(
                    MESSAGE_INVALID_CREDITOR_INDEX + "\n" + AddTransactionCommand.MESSAGE_USAGE, pe);
        }

        if (!arePrefixesPresent(argMultimap, PREFIX_AMOUNT)) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddTransactionCommand.MESSAGE_USAGE));
        }

        TransactionDescriptor descriptor = ParserUtil.parseTransactionDescriptor(
                argMultimap.getValue(PREFIX_AMOUNT).get(),
                argMultimap.getValue(PREFIX_DESCRIPTION).orElse(""));

        return new AddTransactionCommand(debtorIndex, creditorIndex, descriptor);
    }

    /** Returns true only if all of the prefixes contain non-empty {@code Optional} values. */
    private static boolean arePrefixesPresent(ArgumentMultimap argMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argMultimap.getValue(prefix).isPresent());
    }
}
