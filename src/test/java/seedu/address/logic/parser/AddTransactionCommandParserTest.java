package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.AddTransactionCommand.MESSAGE_INVALID_CREDITOR_INDEX;
import static seedu.address.logic.commands.AddTransactionCommand.MESSAGE_INVALID_DEBTOR_INDEX;
import static seedu.address.logic.commands.AddTransactionCommand.MESSAGE_USAGE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.CliSyntax.PREFIX_AMOUNT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DESCRIPTION;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddTransactionCommand;
import seedu.address.model.transaction.TransactionDescriptor;

public class AddTransactionCommandParserTest {

    private AddTransactionCommandParser parser = new AddTransactionCommandParser();

    // ========== Valid Cases ==========

    @Test
    public void parse_allFieldsPresent_success() {
        String userInput = "1 2 " + PREFIX_AMOUNT + "10.50 "
                + PREFIX_DESCRIPTION + "lunch";

        TransactionDescriptor descriptor = new TransactionDescriptor(10.50, "lunch");

        AddTransactionCommand expectedCommand =
                new AddTransactionCommand(Index.fromOneBased(1), Index.fromOneBased(2), descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingDescription_failure() {
        String userInput = "1 2 " + PREFIX_AMOUNT + "20";

        assertParseFailure(parser, userInput,
                "Description cannot be empty.");
    }

    @Test
    public void parse_extraWhitespace_success() {
        String userInput = "   1   2   "
                + PREFIX_AMOUNT + "15   "
                + PREFIX_DESCRIPTION + "dinner   ";

        TransactionDescriptor descriptor = new TransactionDescriptor(15, "dinner");

        AddTransactionCommand expectedCommand =
                new AddTransactionCommand(Index.fromOneBased(1), Index.fromOneBased(2), descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    // ========== Invalid Index Cases ==========

    @Test
    public void parse_invalidDebtorIndex_failure() {
        String userInput = "a 2 " + PREFIX_AMOUNT + "10";

        assertParseFailure(parser, userInput,
                MESSAGE_INVALID_DEBTOR_INDEX + "\n" + MESSAGE_USAGE);
    }

    @Test
    public void parse_invalidCreditorIndex_failure() {
        String userInput = "1 b " + PREFIX_AMOUNT + "10";

        assertParseFailure(parser, userInput,
                MESSAGE_INVALID_CREDITOR_INDEX + "\n" + MESSAGE_USAGE);
    }

    @Test
    public void parse_missingIndices_failure() {
        String userInput = PREFIX_AMOUNT + "10";

        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
    }

    @Test
    public void parse_onlyOneIndex_failure() {
        String userInput = "1 " + PREFIX_AMOUNT + "10";

        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
    }

    // ========== Invalid Amount Cases ==========

    @Test
    public void parse_missingAmount_failure() {
        String userInput = "1 2 " + PREFIX_DESCRIPTION + "lunch";

        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidAmount_failure() {
        String userInput = "1 2 " + PREFIX_AMOUNT + "abc";

        assertParseFailure(parser, userInput,
                "Amount must be a positive number.");
    }

    // ========== General Invalid Format ==========

    @Test
    public void parse_emptyArgs_failure() {
        assertParseFailure(parser, "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
    }
}
