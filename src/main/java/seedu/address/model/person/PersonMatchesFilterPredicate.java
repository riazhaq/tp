package seedu.address.model.person;

import java.util.Optional;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;
import seedu.address.model.transaction.Transaction;

/**
 * Tests whether a {@code Person} satisfies all provided search filters simultaneously.
 *
 * <p>Each filter is optional (represented as {@link Optional}). A filter that is not
 * present is treated as a wildcard and always passes. A person must satisfy <em>every</em>
 * filter that is present in order for {@link #test(Person)} to return {@code true}.
 *
 * <p>Supported filters:
 * <ul>
 *   <li><b>nameKeyword</b> – case-insensitive substring match on the person's full name.</li>
 *   <li><b>descriptionKeyword</b> – case-insensitive substring match on any of the
 *       person's transaction descriptions.</li>
 *   <li><b>minAmount</b> – the person must have at least one transaction whose current
 *       outstanding amount is ≥ this value.</li>
 *   <li><b>maxAmount</b> – the person must have at least one transaction whose current
 *       outstanding amount is ≤ this value.</li>
 *   <li><b>tag</b> – the person must have a tag whose name matches case-insensitively.</li>
 * </ul>
 *
 * <p>When both {@code minAmount} and {@code maxAmount} are present, the same transaction
 * must satisfy both bounds simultaneously (i.e. the bounds form a range over a single
 * transaction, not across different transactions).
 */
public class PersonMatchesFilterPredicate implements Predicate<Person> {

    /** Optional keyword to match against the person's name (partial, case-insensitive). */
    private final Optional<String> nameKeyword;

    /** Optional keyword to match against any transaction description (partial, case-insensitive). */
    private final Optional<String> descriptionKeyword;

    /** Optional lower bound (inclusive) on the transaction amount. */
    private final Optional<Double> minAmount;

    /** Optional upper bound (inclusive) on the transaction amount. */
    private final Optional<Double> maxAmount;

    /** Optional tag name to match (case-insensitive). */
    private final Optional<String> tagKeyword;

    /**
     * Constructs a {@code PersonMatchesFilterPredicate} with all filters specified.
     *
     * <p>Pass {@link Optional#empty()} for any filter that should not be applied.
     *
     * @param nameKeyword        optional partial name keyword
     * @param descriptionKeyword optional partial description keyword
     * @param minAmount          optional minimum transaction amount (inclusive)
     * @param maxAmount          optional maximum transaction amount (inclusive)
     * @param tagKeyword         optional tag name keyword
     */
    public PersonMatchesFilterPredicate(
            Optional<String> nameKeyword,
            Optional<String> descriptionKeyword,
            Optional<Double> minAmount,
            Optional<Double> maxAmount,
            Optional<String> tagKeyword) {
        this.nameKeyword = nameKeyword;
        this.descriptionKeyword = descriptionKeyword;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.tagKeyword = tagKeyword;
    }

    /**
     * Returns {@code true} if {@code person} satisfies every active filter.
     *
     * @param person the person to test; must not be {@code null}
     * @return {@code true} if all present filters match, {@code false} otherwise
     */
    @Override
    public boolean test(Person person) {
        return matchesName(person)
                && matchesDescription(person)
                && matchesAmountRange(person)
                && matchesTag(person);
    }

    /**
     * Returns {@code true} if the name filter is absent, or if the person's full name
     * contains the name keyword as a substring (case-insensitive).
     *
     * @param person the person whose name is tested
     * @return {@code true} if the name filter passes
     */
    private boolean matchesName(Person person) {
        return nameKeyword
                .map(keyword -> containsIgnoreCase(person.getName().fullName, keyword))
                .orElse(true);
    }

    /**
     * Returns {@code true} if the description filter is absent, or if at least one of
     * the person's transactions has a description containing the keyword (case-insensitive).
     *
     * @param person the person whose transactions are tested
     * @return {@code true} if the description filter passes
     */
    private boolean matchesDescription(Person person) {
        return descriptionKeyword
                .map(keyword -> person.getTransactions().stream()
                        .anyMatch(tx -> containsIgnoreCase(tx.getDescription(), keyword)))
                .orElse(true);
    }

    /**
     * Returns {@code true} if both amount filters are absent, or if at least one of
     * the person's transactions has an amount that falls within the specified range.
     *
     * <p>Both {@code minAmount} and {@code maxAmount} are inclusive bounds. When only
     * one bound is present the other is treated as unbounded.
     *
     * @param person the person whose transactions are tested
     * @return {@code true} if the amount-range filter passes
     */
    private boolean matchesAmountRange(Person person) {
        if (minAmount.isEmpty() && maxAmount.isEmpty()) {
            return true;
        }
        return person.getTransactions().stream()
                .anyMatch(tx -> isWithinAmountRange(tx));
    }

    /**
     * Returns {@code true} if the given transaction's current outstanding amount satisfies
     * all active amount bounds.
     *
     * @param transaction the transaction to check
     * @return {@code true} if the transaction amount is within the active range
     */
    private boolean isWithinAmountRange(Transaction transaction) {
        double amount = transaction.getCurrAmount();
        boolean aboveMin = minAmount.map(min -> amount >= min).orElse(true);
        boolean belowMax = maxAmount.map(max -> amount <= max).orElse(true);
        return aboveMin && belowMax;
    }

    /**
     * Returns {@code true} if the tag filter is absent, or if the person has at least
     * one tag whose name matches the keyword (case-insensitive).
     *
     * @param person the person whose tags are tested
     * @return {@code true} if the tag filter passes
     */
    private boolean matchesTag(Person person) {
        return tagKeyword
                .map(keyword -> person.getTags().stream()
                        .anyMatch(tag -> matchesTagKeyword(tag, keyword)))
                .orElse(true);
    }

    /**
     * Returns {@code true} if the given tag's name contains the keyword as a
     * case-insensitive substring.
     *
     * @param tag     the tag to test
     * @param keyword the keyword to search for
     * @return {@code true} if the tag name contains the keyword
     */
    private boolean matchesTagKeyword(Tag tag, String keyword) {
        return containsIgnoreCase(tag.tagName, keyword);
    }

    /**
     * Returns {@code true} if {@code text} contains {@code keyword} as a substring,
     * ignoring case.
     *
     * @param text    the string to search within
     * @param keyword the substring to search for
     * @return {@code true} if {@code text} contains {@code keyword} (case-insensitive)
     */
    private boolean containsIgnoreCase(String text, String keyword) {
        return text.toLowerCase().contains(keyword.toLowerCase());
    }

    /**
     * Returns the optional name keyword used by this predicate.
     *
     * @return an {@link Optional} containing the name keyword, or empty if not set
     */
    public Optional<String> getNameKeyword() {
        return nameKeyword;
    }

    /**
     * Returns the optional description keyword used by this predicate.
     *
     * @return an {@link Optional} containing the description keyword, or empty if not set
     */
    public Optional<String> getDescriptionKeyword() {
        return descriptionKeyword;
    }

    /**
     * Returns the optional minimum amount bound used by this predicate.
     *
     * @return an {@link Optional} containing the minimum amount, or empty if not set
     */
    public Optional<Double> getMinAmount() {
        return minAmount;
    }

    /**
     * Returns the optional maximum amount bound used by this predicate.
     *
     * @return an {@link Optional} containing the maximum amount, or empty if not set
     */
    public Optional<Double> getMaxAmount() {
        return maxAmount;
    }

    /**
     * Returns the optional tag keyword used by this predicate.
     *
     * @return an {@link Optional} containing the tag keyword, or empty if not set
     */
    public Optional<String> getTagKeyword() {
        return tagKeyword;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof PersonMatchesFilterPredicate)) {
            return false;
        }

        PersonMatchesFilterPredicate otherPredicate = (PersonMatchesFilterPredicate) other;
        return nameKeyword.equals(otherPredicate.nameKeyword)
                && descriptionKeyword.equals(otherPredicate.descriptionKeyword)
                && minAmount.equals(otherPredicate.minAmount)
                && maxAmount.equals(otherPredicate.maxAmount)
                && tagKeyword.equals(otherPredicate.tagKeyword);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(nameKeyword, descriptionKeyword, minAmount, maxAmount, tagKeyword);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("nameKeyword", nameKeyword)
                .add("descriptionKeyword", descriptionKeyword)
                .add("minAmount", minAmount)
                .add("maxAmount", maxAmount)
                .add("tagKeyword", tagKeyword)
                .toString();
    }
}
