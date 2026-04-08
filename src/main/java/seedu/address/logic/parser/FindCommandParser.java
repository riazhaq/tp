package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Optional;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonMatchesFilterPredicate;

/**
 * Parses user input for the {@code find} command and creates a corresponding {@link FindCommand}.
 *
 * <p>The parser accepts the following optional prefixed arguments:
 * <ul>
 *   <li>{@code n/} – name keyword (partial, case-insensitive)</li>
 *   <li>{@code d/} – transaction description keyword (partial, case-insensitive)</li>
 *   <li>{@code min/} – minimum transaction amount (must be a positive number)</li>
 *   <li>{@code max/} – maximum transaction amount (must be a positive number)</li>
 *   <li>{@code t/} – tag keyword (case-insensitive)</li>
 * </ul>
 *
 * <p>At least one prefix must be supplied. If {@code min/} and {@code max/} are both supplied,
 * the minimum must not exceed the maximum.
 *
 * <p>Duplicate prefixes are not allowed for any single-valued field.
 */
public class FindCommandParser implements Parser<FindCommand> {

    /** Prefix for the minimum transaction amount filter. */
    public static final Prefix PREFIX_MIN_AMOUNT = new Prefix("min/");

    /** Prefix for the maximum transaction amount filter. */
    public static final Prefix PREFIX_MAX_AMOUNT = new Prefix("max/");

    /** Error message when the minimum amount is greater than the maximum. */
    public static final String MESSAGE_INVALID_AMOUNT_RANGE =
            "Minimum amount cannot be greater than maximum amount.";

    /** Error message when an amount value cannot be parsed as a positive number. */
    public static final String MESSAGE_INVALID_AMOUNT_VALUE =
            "Amount values for min/ and max/ must be positive numbers (e.g. 10 or 5.50).";

    /**
     * Parses the given {@code args} string in the context of the {@link FindCommand}
     * and returns a {@code FindCommand} ready for execution.
     *
     * @param args the raw argument string entered by the user (excluding the command word)
     * @return a fully constructed {@link FindCommand}
     * @throws ParseException if the input is empty, contains duplicate prefixes, supplies an
     *                        invalid amount, or specifies a min that exceeds max
     */
    @Override
    public FindCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = tokenizeArguments(args);
        validateNoDuplicatePrefixes(argMultimap);
        ensureAtLeastOneFilterPresent(argMultimap, args);

        Optional<String> nameKeyword = extractNameKeyword(argMultimap);
        Optional<String> descriptionKeyword = extractDescriptionKeyword(argMultimap);
        Optional<Double> minAmount = extractMinAmount(argMultimap);
        Optional<Double> maxAmount = extractMaxAmount(argMultimap);
        Optional<String> tagKeyword = extractTagKeyword(argMultimap);

        validateAmountRange(minAmount, maxAmount);

        PersonMatchesFilterPredicate predicate =
                new PersonMatchesFilterPredicate(nameKeyword, descriptionKeyword, minAmount, maxAmount, tagKeyword);
        return new FindCommand(predicate);
    }

    /**
     * Tokenizes the raw argument string into an {@link ArgumentMultimap} keyed by prefix.
     *
     * @param args the raw argument string
     * @return the populated {@link ArgumentMultimap}
     */
    private ArgumentMultimap tokenizeArguments(String args) {
        return ArgumentTokenizer.tokenize(
                args,
                CliSyntax.PREFIX_NAME,
                CliSyntax.PREFIX_DESCRIPTION,
                PREFIX_MIN_AMOUNT,
                PREFIX_MAX_AMOUNT,
                CliSyntax.PREFIX_TAG);
    }

    /**
     * Validates that no prefix is used more than once in the argument map.
     *
     * @param argMultimap the tokenised argument map
     * @throws ParseException if any single-valued prefix appears more than once
     */
    private void validateNoDuplicatePrefixes(ArgumentMultimap argMultimap) throws ParseException {
        argMultimap.verifyNoDuplicatePrefixesFor(
                CliSyntax.PREFIX_NAME,
                CliSyntax.PREFIX_DESCRIPTION,
                PREFIX_MIN_AMOUNT,
                PREFIX_MAX_AMOUNT,
                CliSyntax.PREFIX_TAG);
    }

    /**
     * Throws a {@link ParseException} if no recognised prefix is present in the argument map.
     *
     * @param argMultimap the tokenised argument map
     * @param args        the original raw argument string (used in the error message)
     * @throws ParseException if no filter prefix was supplied
     */
    private void ensureAtLeastOneFilterPresent(ArgumentMultimap argMultimap, String args) throws ParseException {
        boolean hasAnyFilter = argMultimap.getValue(CliSyntax.PREFIX_NAME).isPresent()
                || argMultimap.getValue(CliSyntax.PREFIX_DESCRIPTION).isPresent()
                || argMultimap.getValue(PREFIX_MIN_AMOUNT).isPresent()
                || argMultimap.getValue(PREFIX_MAX_AMOUNT).isPresent()
                || argMultimap.getValue(CliSyntax.PREFIX_TAG).isPresent();

        if (!hasAnyFilter) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Extracts and trims the name keyword from the argument map, if present.
     *
     * @param argMultimap the tokenised argument map
     * @return an {@link Optional} containing the trimmed name keyword, or empty if absent
     * @throws ParseException if the name keyword is present but blank
     */
    private Optional<String> extractNameKeyword(ArgumentMultimap argMultimap) throws ParseException {
        Optional<String> raw = argMultimap.getValue(CliSyntax.PREFIX_NAME);
        if (raw.isEmpty()) {
            return Optional.empty();
        }
        String trimmed = raw.get().trim();
        if (trimmed.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        return Optional.of(trimmed);
    }

    /**
     * Extracts and trims the description keyword from the argument map, if present.
     *
     * @param argMultimap the tokenised argument map
     * @return an {@link Optional} containing the trimmed description keyword, or empty if absent
     * @throws ParseException if the description keyword is present but blank
     */
    private Optional<String> extractDescriptionKeyword(ArgumentMultimap argMultimap) throws ParseException {
        Optional<String> raw = argMultimap.getValue(CliSyntax.PREFIX_DESCRIPTION);
        if (raw.isEmpty()) {
            return Optional.empty();
        }
        String trimmed = raw.get().trim();
        if (trimmed.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        return Optional.of(trimmed);
    }

    /**
     * Extracts and parses the minimum amount from the argument map, if present.
     *
     * @param argMultimap the tokenised argument map
     * @return an {@link Optional} containing the parsed minimum amount, or empty if absent
     * @throws ParseException if the value is present but not a valid positive number
     */
    private Optional<Double> extractMinAmount(ArgumentMultimap argMultimap) throws ParseException {
        Optional<String> raw = argMultimap.getValue(PREFIX_MIN_AMOUNT);
        if (raw.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(parsePositiveAmount(raw.get()));
    }

    /**
     * Extracts and parses the maximum amount from the argument map, if present.
     *
     * @param argMultimap the tokenised argument map
     * @return an {@link Optional} containing the parsed maximum amount, or empty if absent
     * @throws ParseException if the value is present but not a valid positive number
     */
    private Optional<Double> extractMaxAmount(ArgumentMultimap argMultimap) throws ParseException {
        Optional<String> raw = argMultimap.getValue(PREFIX_MAX_AMOUNT);
        if (raw.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(parsePositiveAmount(raw.get()));
    }

    /**
     * Extracts and trims the tag keyword from the argument map, if present.
     *
     * @param argMultimap the tokenised argument map
     * @return an {@link Optional} containing the trimmed tag keyword, or empty if absent
     * @throws ParseException if the tag keyword is present but blank
     */
    private Optional<String> extractTagKeyword(ArgumentMultimap argMultimap) throws ParseException {
        Optional<String> raw = argMultimap.getValue(CliSyntax.PREFIX_TAG);
        if (raw.isEmpty()) {
            return Optional.empty();
        }
        String trimmed = raw.get().trim();
        if (trimmed.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        return Optional.of(trimmed);
    }

    /**
     * Parses a raw string as a positive double amount.
     *
     * @param raw the raw string to parse
     * @return the parsed positive double value
     * @throws ParseException if the string cannot be parsed or is not positive
     */
    private double parsePositiveAmount(String raw) throws ParseException {
        try {
            double value = Double.parseDouble(raw.trim());
            if (value <= 0) {
                throw new ParseException(MESSAGE_INVALID_AMOUNT_VALUE);
            }
            return value;
        } catch (NumberFormatException e) {
            throw new ParseException(MESSAGE_INVALID_AMOUNT_VALUE);
        }
    }

    /**
     * Validates that, when both amount bounds are present, the minimum does not exceed the maximum.
     *
     * @param minAmount the optional lower bound
     * @param maxAmount the optional upper bound
     * @throws ParseException if min is greater than max
     */
    private void validateAmountRange(Optional<Double> minAmount, Optional<Double> maxAmount) throws ParseException {
        if (minAmount.isPresent() && maxAmount.isPresent() && minAmount.get() > maxAmount.get()) {
            throw new ParseException(MESSAGE_INVALID_AMOUNT_RANGE);
        }
    }
}
