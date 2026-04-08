package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.FindCommandParser.MESSAGE_INVALID_AMOUNT_RANGE;
import static seedu.address.logic.parser.FindCommandParser.MESSAGE_INVALID_AMOUNT_VALUE;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonMatchesFilterPredicate;

/**
 * Unit tests for {@link FindCommandParser}.
 *
 * <p>Tests cover valid single-filter inputs, valid multi-filter combinations,
 * invalid inputs (blank args, invalid amounts, min > max), and duplicate prefix detection.
 */
public class FindCommandParserTest {

    /** The parser under test. */
    private final FindCommandParser parser = new FindCommandParser();

    // ======================== Empty / no-filter input ========================

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertParseFailure("     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_noPrefixSupplied_throwsParseException() {
        // Plain text with no recognised prefix is treated as a preamble — no filter present.
        assertParseFailure("alice", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    // ======================== Name filter ========================

    @Test
    public void parse_nameOnly_success() throws ParseException {
        FindCommand result = parser.parse(" n/Alice");
        FindCommand expected = buildCommand(
                Optional.of("Alice"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        assertEquals(expected, result);
    }

    @Test
    public void parse_nameWithLeadingTrailingSpaces_trimmed() throws ParseException {
        FindCommand result = parser.parse(" n/  Alice  ");
        FindCommand expected = buildCommand(
                Optional.of("Alice"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        assertEquals(expected, result);
    }

    @Test
    public void parse_nameBlankValue_throwsParseException() {
        assertParseFailure(" n/   ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    // ======================== Description filter ========================

    @Test
    public void parse_descriptionOnly_success() throws ParseException {
        FindCommand result = parser.parse(" d/lunch");
        FindCommand expected = buildCommand(
                Optional.empty(), Optional.of("lunch"), Optional.empty(), Optional.empty(), Optional.empty());
        assertEquals(expected, result);
    }

    @Test
    public void parse_descriptionBlankValue_throwsParseException() {
        assertParseFailure(" d/   ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    // ======================== Min amount filter ========================

    @Test
    public void parse_minAmountOnly_success() throws ParseException {
        FindCommand result = parser.parse(" min/10");
        FindCommand expected = buildCommand(
                Optional.empty(), Optional.empty(), Optional.of(10.0), Optional.empty(), Optional.empty());
        assertEquals(expected, result);
    }

    @Test
    public void parse_minAmountDecimal_success() throws ParseException {
        FindCommand result = parser.parse(" min/5.50");
        FindCommand expected = buildCommand(
                Optional.empty(), Optional.empty(), Optional.of(5.50), Optional.empty(), Optional.empty());
        assertEquals(expected, result);
    }

    @Test
    public void parse_minAmountNonNumeric_throwsParseException() {
        assertParseFailure(" min/abc", MESSAGE_INVALID_AMOUNT_VALUE);
    }

    @Test
    public void parse_minAmountNegative_throwsParseException() {
        assertParseFailure(" min/-5", MESSAGE_INVALID_AMOUNT_VALUE);
    }

    @Test
    public void parse_minAmountZero_throwsParseException() {
        assertParseFailure(" min/0", MESSAGE_INVALID_AMOUNT_VALUE);
    }

    // ======================== Max amount filter ========================

    @Test
    public void parse_maxAmountOnly_success() throws ParseException {
        FindCommand result = parser.parse(" max/100");
        FindCommand expected = buildCommand(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(100.0), Optional.empty());
        assertEquals(expected, result);
    }

    @Test
    public void parse_maxAmountNonNumeric_throwsParseException() {
        assertParseFailure(" max/abc", MESSAGE_INVALID_AMOUNT_VALUE);
    }

    @Test
    public void parse_maxAmountNegative_throwsParseException() {
        assertParseFailure(" max/-10", MESSAGE_INVALID_AMOUNT_VALUE);
    }

    // ======================== Amount range validation ========================

    @Test
    public void parse_minLessThanMax_success() throws ParseException {
        FindCommand result = parser.parse(" min/10 max/100");
        FindCommand expected = buildCommand(
                Optional.empty(), Optional.empty(), Optional.of(10.0), Optional.of(100.0), Optional.empty());
        assertEquals(expected, result);
    }

    @Test
    public void parse_minEqualsMax_success() throws ParseException {
        FindCommand result = parser.parse(" min/50 max/50");
        FindCommand expected = buildCommand(
                Optional.empty(), Optional.empty(), Optional.of(50.0), Optional.of(50.0), Optional.empty());
        assertEquals(expected, result);
    }

    @Test
    public void parse_minGreaterThanMax_throwsParseException() {
        assertParseFailure(" min/100 max/10", MESSAGE_INVALID_AMOUNT_RANGE);
    }

    // ======================== Tag filter ========================

    @Test
    public void parse_tagOnly_success() throws ParseException {
        FindCommand result = parser.parse(" t/friends");
        FindCommand expected = buildCommand(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("friends"));
        assertEquals(expected, result);
    }

    @Test
    public void parse_tagBlankValue_throwsParseException() {
        assertParseFailure(" t/  ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    // ======================== Multi-filter combinations ========================

    @Test
    public void parse_nameAndTag_success() throws ParseException {
        FindCommand result = parser.parse(" n/Alice t/friends");
        FindCommand expected = buildCommand(
                Optional.of("Alice"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("friends"));
        assertEquals(expected, result);
    }

    @Test
    public void parse_nameAndDescriptionAndAmountRange_success() throws ParseException {
        FindCommand result = parser.parse(" n/Alice d/lunch min/5 max/100");
        FindCommand expected = buildCommand(
                Optional.of("Alice"), Optional.of("lunch"), Optional.of(5.0), Optional.of(100.0), Optional.empty());
        assertEquals(expected, result);
    }

    @Test
    public void parse_allFilters_success() throws ParseException {
        FindCommand result = parser.parse(" n/Alice d/lunch min/5 max/100 t/friends");
        FindCommand expected = buildCommand(
                Optional.of("Alice"), Optional.of("lunch"), Optional.of(5.0), Optional.of(100.0),
                Optional.of("friends"));
        assertEquals(expected, result);
    }

    @Test
    public void parse_filtersInDifferentOrder_success() throws ParseException {
        FindCommand result = parser.parse(" t/friends min/5 n/Alice max/100 d/lunch");
        FindCommand expected = buildCommand(
                Optional.of("Alice"), Optional.of("lunch"), Optional.of(5.0), Optional.of(100.0),
                Optional.of("friends"));
        assertEquals(expected, result);
    }

    // ======================== Duplicate prefix tests ========================

    @Test
    public void parse_duplicateNamePrefix_throwsParseException() {
        assertParseFailure(" n/Alice n/Bob", "Multiple values specified");
    }

    @Test
    public void parse_duplicateDescriptionPrefix_throwsParseException() {
        assertParseFailure(" d/lunch d/dinner", "Multiple values specified");
    }

    @Test
    public void parse_duplicateMinPrefix_throwsParseException() {
        assertParseFailure(" min/5 min/10", "Multiple values specified");
    }

    @Test
    public void parse_duplicateMaxPrefix_throwsParseException() {
        assertParseFailure(" max/50 max/100", "Multiple values specified");
    }

    // ======================== Helper methods ========================

    /**
     * Asserts that parsing {@code args} throws a {@link ParseException} whose message
     * contains {@code expectedMessageSubstring}.
     *
     * @param args                    the argument string to parse
     * @param expectedMessageSubstring a substring that must appear in the exception message
     */
    private void assertParseFailure(String args, String expectedMessageSubstring) {
        ParseException thrown = assertThrows(ParseException.class, () -> parser.parse(args));
        assertTrue(thrown.getMessage().contains(expectedMessageSubstring),
                "Expected exception message to contain: " + expectedMessageSubstring
                        + "\nActual: " + thrown.getMessage());
    }

    /**
     * Asserts a condition is true; re-throws as {@link AssertionError} with a message if not.
     *
     * @param condition the condition to assert
     * @param message   message to display on failure
     */
    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    /**
     * Builds a {@link FindCommand} wrapping a {@link PersonMatchesFilterPredicate}
     * constructed from the given optional values.
     *
     * @param nameKeyword        optional name keyword
     * @param descriptionKeyword optional description keyword
     * @param minAmount          optional minimum amount
     * @param maxAmount          optional maximum amount
     * @param tagKeyword         optional tag keyword
     * @return a {@link FindCommand} with the specified predicate
     */
    private FindCommand buildCommand(
            Optional<String> nameKeyword,
            Optional<String> descriptionKeyword,
            Optional<Double> minAmount,
            Optional<Double> maxAmount,
            Optional<String> tagKeyword) {
        return new FindCommand(
                new PersonMatchesFilterPredicate(nameKeyword, descriptionKeyword, minAmount, maxAmount, tagKeyword));
    }
}
