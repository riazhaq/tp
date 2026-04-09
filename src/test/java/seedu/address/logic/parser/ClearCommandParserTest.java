package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ClearCommand;

/**
 * Contains unit tests for {@link ClearCommandParser}.
 */
public class ClearCommandParserTest {

    private final ClearCommandParser parser = new ClearCommandParser();

    @Test
    public void parse_noArguments_success() {
        assertParseSuccess(parser, "", new ClearCommand());
    }

    @Test
    public void parse_extraArguments_throwsParseException() {
        assertParseFailure(parser, "garbage", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                ClearCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_onlySpaces_success() {
        assertParseSuccess(parser, "   ", new ClearCommand());
    }
}
