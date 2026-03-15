package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    private static final Prefix PREFIX_TRANSACTION_INDEX = new Prefix("t/");

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_TRANSACTION_INDEX);

        try {
            Index targetIndex = ParserUtil.parseIndex(argMultimap.getPreamble());
            Index transactionIndex = null;

            if (argMultimap.getValue(PREFIX_TRANSACTION_INDEX).isPresent()) {
                transactionIndex = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_TRANSACTION_INDEX).get());
            }

            return (transactionIndex == null) ? new DeleteCommand(targetIndex)
                    : new DeleteCommand(targetIndex, transactionIndex);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE), pe);
        }
    }

}
