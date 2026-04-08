package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteCommand;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeleteCommandParserTest {
    private static final Prefix PREFIX_TRANSACTION_INDEX = new Prefix("t/");

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new DeleteCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_validArgsWithTransaction_returnsDeleteCommand() {
        assertParseSuccess(parser, "1 t/2",
                new DeleteCommand(INDEX_FIRST_PERSON, Index.fromOneBased(2)));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingPersonIndex_throwsParseException() {
        assertParseFailure(parser, "t/1", String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateTransactionPrefix_throwsParseException() {
        assertParseFailure(parser, "1 t/1 t/2",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_TRANSACTION_INDEX));
    }


    @Test
    public void parse_transactionIndexWithTrailingGarbage_throwsParseException() {
        // e.g. "delete 1 t/1delete" — single t/ but value is not purely numeric
        assertParseFailure(parser, "1 t/1delete",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }


    @Test
    public void parse_fusedCommands_throwsParseException() {
        assertParseFailure(parser, "1 t/1delete 1 t/1",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_TRANSACTION_INDEX));
    }

    @Test
    public void parse_invalidTransactionIndex_throwsParseException() {
        // t/ present but value is not a valid index
        assertParseFailure(parser, "1 t/0",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "1 t/a",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidPersonIndex_throwsParseException() {
        // Person index is zero or negative
        assertParseFailure(parser, "0 t/1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "-1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
    }
}
