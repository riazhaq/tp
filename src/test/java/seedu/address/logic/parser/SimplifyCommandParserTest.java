package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.SimplifyCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class SimplifyCommandParserTest {

    private final SimplifyCommandParser parser = new SimplifyCommandParser();

    @Test
    public void parse_validArgs_returnsSimplifyCommand() throws Exception {
        SimplifyCommand expected = new SimplifyCommand(List.of(
                Index.fromOneBased(1),
                Index.fromOneBased(2),
                Index.fromOneBased(3),
                Index.fromOneBased(4)));

        assertEquals(expected, parser.parse(" 1 2 3 4 "));
    }

    @Test
    public void parse_lessThanThreeIndices_throwsParseException() {
        String expectedMessage = SimplifyCommand.MESSAGE_MINIMUM_PARTICIPANTS;
        assertThrows(ParseException.class, expectedMessage, () -> parser.parse("1 2"));
    }

    @Test
    public void parse_duplicateIndices_throwsParseException() {
        String expectedMessage = String.format(SimplifyCommand.MESSAGE_DUPLICATE_PERSON_INDEX, 2);
        assertThrows(ParseException.class, expectedMessage, () -> parser.parse("1 2 2"));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SimplifyCommand.MESSAGE_USAGE);
        assertThrows(ParseException.class, expectedMessage, () -> parser.parse("1 two 3"));
    }
}
