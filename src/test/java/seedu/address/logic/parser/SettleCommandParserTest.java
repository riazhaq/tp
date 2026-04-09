package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.SettleCommand;

public class SettleCommandParserTest {

    private static final Prefix PREFIX_TRANSACTION_INDEX = new Prefix("t/");

    private final SettleCommandParser parser = new SettleCommandParser();

    @Test
    public void parse_validArgs_returnsSettleCommand() {
        assertParseSuccess(parser, "1 t/2",
                new SettleCommand(INDEX_FIRST_PERSON, Index.fromOneBased(2)));
    }

    @Test
    public void parse_missingTransactionIndex_throwsParseException() {
        assertParseFailure(parser, "1", String.format(MESSAGE_INVALID_COMMAND_FORMAT, SettleCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingPersonIndex_throwsParseException() {
        assertParseFailure(parser, "t/1", String.format(MESSAGE_INVALID_COMMAND_FORMAT, SettleCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a t/1", String.format(MESSAGE_INVALID_COMMAND_FORMAT, SettleCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateTransactionIndex_throwsParseException() {
        assertParseFailure(parser, "1 t/1 t/2", getErrorMessageForDuplicatePrefixes(PREFIX_TRANSACTION_INDEX));
    }
}
