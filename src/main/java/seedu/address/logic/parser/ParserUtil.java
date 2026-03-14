package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.loan.Loan;
import seedu.address.model.loan.MonthlyLoan;
import seedu.address.model.loan.YearlyLoan;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index is not a non-zero unsigned integer.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses a {@code String loanArguments} into a {@code Loan}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code loanDetails} is invalid.
     */
    public static Loan parseLoan(String loanDetails) throws ParseException {
        requireNonNull(loanDetails);
        String trimmedLoanDetails = loanDetails.trim();
        if (!Loan.isValidLoanArguments(trimmedLoanDetails)) {
            throw new ParseException(Loan.MESSAGE_CONSTRAINTS);
        }

        String lowercasedLoanDetails = trimmedLoanDetails.toLowerCase();
        String loanDetailsWithoutType = trimmedLoanDetails;

        if (lowercasedLoanDetails.startsWith("m ") || lowercasedLoanDetails.startsWith("y ")) {
            loanDetailsWithoutType = trimmedLoanDetails.substring(2);
        }

        String[] parts = loanDetailsWithoutType.split("\\s*,\\s*", 3);

        double amount = Double.parseDouble(parts[0]);
        double rate = Double.parseDouble(parts[1]);
        String description = parts[2];

        if (lowercasedLoanDetails.startsWith("m ")) {
            return new MonthlyLoan(amount, rate, description);
        } else if (lowercasedLoanDetails.startsWith("y ")) {
            return new YearlyLoan(amount, rate, description);
        } else {
            return new Loan(amount, rate, description);
        }
    }

    /**
     * Parses {@code Collection<String> loans} into a {@code Set<Loan>}.
     */
    public static Set<Loan> parseLoans(Collection<String> loans) throws ParseException {
        requireNonNull(loans);
        final Set<Loan> loanSet = new HashSet<>();
        for (String loanDetails : loans) {
            loanSet.add(parseLoan(loanDetails));
        }
        return loanSet;
    }
}
