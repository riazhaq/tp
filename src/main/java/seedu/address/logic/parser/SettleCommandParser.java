package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.SettleCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new SettleCommand object.
 */
public class SettleCommandParser implements Parser<SettleCommand> {

    private static final Prefix PREFIX_TRANSACTION_INDEX = new Prefix("t/");

    @Override
    public SettleCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TRANSACTION_INDEX);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_TRANSACTION_INDEX);

        try {
            Index targetIndex = ParserUtil.parseIndex(argMultimap.getPreamble());

            if (argMultimap.getValue(PREFIX_TRANSACTION_INDEX).isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SettleCommand.MESSAGE_USAGE));
            }

            Index transactionIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_TRANSACTION_INDEX).get());
            return new SettleCommand(targetIndex, transactionIndex);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SettleCommand.MESSAGE_USAGE), pe);
        }
    }
}
