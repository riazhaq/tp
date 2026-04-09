package seedu.address.logic.parser;

import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AMOUNT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_COMPOUNDING_TYPE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

/**
 * Contains unit tests for {@link AddTransactionCommandParser}.
 */
public class AddTransactionCommandParserTest {

    private final AddTransactionCommandParser parser = new AddTransactionCommandParser();

    @Test
    public void parse_duplicateAmountSuffix_failure() {
        String userInput = "1 2 a/10 a/20 i/5";

        assertParseFailure(parser, userInput, getErrorMessageForDuplicatePrefixes(PREFIX_AMOUNT));
    }

    @Test
    public void parse_duplicateOptionalPrefixes_failure() {
        String userInput = "1 2 a/10 i/5 d/lunch d/dinner t/m t/y";

        assertParseFailure(parser, userInput,
                getErrorMessageForDuplicatePrefixes(PREFIX_DESCRIPTION, PREFIX_COMPOUNDING_TYPE));
    }
}
