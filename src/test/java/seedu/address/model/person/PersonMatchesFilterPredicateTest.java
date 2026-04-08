package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;
import seedu.address.model.transaction.Transaction;
import seedu.address.testutil.PersonBuilder;

/**
 * Unit tests for {@link PersonMatchesFilterPredicate}.
 *
 * <p>Each test exercises a single filter in isolation and in combination with others
 * to verify that the predicate correctly implements AND semantics across all active filters.
 */
public class PersonMatchesFilterPredicateTest {

    /** A creditor person used when building test transactions. */
    private Person creditor;

    /** The primary person under test. */
    private Person alice;

    /**
     * Sets up shared test fixtures before each test.
     *
     * <p>Alice is built with:
     * <ul>
     *   <li>Name: "Alice Pauline"</li>
     *   <li>Tag: "friends"</li>
     *   <li>One transaction with description "Lunch money", amount 50.00</li>
     * </ul>
     */
    @BeforeEach
    public void setUp() {
        creditor = new PersonBuilder().withName("Bob Tan").build();
        alice = new PersonBuilder()
                .withName("Alice Pauline")
                .withTags("friends")
                .build();
        Transaction lunchTransaction = new Transaction(alice, creditor, 50.00, 0, "Lunch money");
        alice.appendTransaction(lunchTransaction);
    }

    // ======================== Name filter tests ========================

    @Test
    public void test_nameFilterAbsent_alwaysPasses() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        assertTrue(predicate.test(alice));
    }

    @Test
    public void test_nameFilterPartialMatch_returnsTrue() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("alice"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        assertTrue(predicate.test(alice));
    }

    @Test
    public void test_nameFilterCaseInsensitive_returnsTrue() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("ALICE"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        assertTrue(predicate.test(alice));
    }

    @Test
    public void test_nameFilterSubstringOfLastName_returnsTrue() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("pau"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        assertTrue(predicate.test(alice));
    }

    @Test
    public void test_nameFilterNoMatch_returnsFalse() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("Charlie"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        assertFalse(predicate.test(alice));
    }

    // ======================== Description filter tests ========================

    @Test
    public void test_descriptionFilterMatchesTransaction_returnsTrue() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.of("lunch"), Optional.empty(), Optional.empty(), Optional.empty());
        assertTrue(predicate.test(alice));
    }

    @Test
    public void test_descriptionFilterCaseInsensitive_returnsTrue() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.of("LUNCH MONEY"), Optional.empty(), Optional.empty(), Optional.empty());
        assertTrue(predicate.test(alice));
    }

    @Test
    public void test_descriptionFilterNoMatchingTransaction_returnsFalse() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.of("rent"), Optional.empty(), Optional.empty(), Optional.empty());
        assertFalse(predicate.test(alice));
    }

    @Test
    public void test_descriptionFilterPersonWithNoTransactions_returnsFalse() {
        Person personWithoutTransactions = new PersonBuilder().withName("Eve Lim").build();
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.of("lunch"), Optional.empty(), Optional.empty(), Optional.empty());
        assertFalse(predicate.test(personWithoutTransactions));
    }

    // ======================== Min amount filter tests ========================

    @Test
    public void test_minAmountExactMatch_returnsTrue() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.of(50.00), Optional.empty(), Optional.empty());
        assertTrue(predicate.test(alice));
    }

    @Test
    public void test_minAmountBelowTransactionAmount_returnsTrue() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.of(10.00), Optional.empty(), Optional.empty());
        assertTrue(predicate.test(alice));
    }

    @Test
    public void test_minAmountAboveTransactionAmount_returnsFalse() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.of(100.00), Optional.empty(), Optional.empty());
        assertFalse(predicate.test(alice));
    }

    // ======================== Max amount filter tests ========================

    @Test
    public void test_maxAmountExactMatch_returnsTrue() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(50.00), Optional.empty());
        assertTrue(predicate.test(alice));
    }

    @Test
    public void test_maxAmountAboveTransactionAmount_returnsTrue() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(200.00), Optional.empty());
        assertTrue(predicate.test(alice));
    }

    @Test
    public void test_maxAmountBelowTransactionAmount_returnsFalse() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(10.00), Optional.empty());
        assertFalse(predicate.test(alice));
    }

    // ======================== Amount range tests ========================

    @Test
    public void test_amountWithinRange_returnsTrue() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.of(10.00), Optional.of(100.00), Optional.empty());
        assertTrue(predicate.test(alice));
    }

    @Test
    public void test_amountBelowRange_returnsFalse() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.of(60.00), Optional.of(100.00), Optional.empty());
        assertFalse(predicate.test(alice));
    }

    @Test
    public void test_amountAboveRange_returnsFalse() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.of(10.00), Optional.of(30.00), Optional.empty());
        assertFalse(predicate.test(alice));
    }

    @Test
    public void test_personWithMultipleTransactions_matchesIfAnyTransactionWithinRange() {
        Person bob = new PersonBuilder().withName("Bob Martin").build();
        Person carol = new PersonBuilder().withName("Carol Diaz").build();
        bob.appendTransaction(new Transaction(bob, carol, 20.00, 0, "Coffee"));
        bob.appendTransaction(new Transaction(bob, carol, 80.00, 0, "Dinner"));

        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.of(50.00), Optional.of(100.00), Optional.empty());
        assertTrue(predicate.test(bob));
    }

    // ======================== Tag filter tests ========================

    @Test
    public void test_tagFilterExactMatch_returnsTrue() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("friends"));
        assertTrue(predicate.test(alice));
    }

    @Test
    public void test_tagFilterCaseInsensitive_returnsTrue() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("FRIENDS"));
        assertTrue(predicate.test(alice));
    }

    @Test
    public void test_tagFilterPartialMatch_returnsTrue() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("frien"));
        assertTrue(predicate.test(alice));
    }

    @Test
    public void test_tagFilterNoMatchingTag_returnsFalse() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("colleagues"));
        assertFalse(predicate.test(alice));
    }

    @Test
    public void test_tagFilterPersonHasNoTags_returnsFalse() {
        Person personNoTags = new PersonBuilder().withName("Dave Kim").build();
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("friends"));
        assertFalse(predicate.test(personNoTags));
    }

    // ======================== Combined filter tests (AND semantics) ========================

    @Test
    public void test_allFiltersMatch_returnsTrue() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("alice"),
                Optional.of("lunch"),
                Optional.of(10.00),
                Optional.of(100.00),
                Optional.of("friends"));
        assertTrue(predicate.test(alice));
    }

    @Test
    public void test_nameMatchesButTagDoesNot_returnsFalse() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("alice"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of("colleagues"));
        assertFalse(predicate.test(alice));
    }

    @Test
    public void test_tagMatchesButDescriptionDoesNot_returnsFalse() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(),
                Optional.of("rent"),
                Optional.empty(),
                Optional.empty(),
                Optional.of("friends"));
        assertFalse(predicate.test(alice));
    }

    @Test
    public void test_amountAndDescriptionBothMatch_returnsTrue() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(),
                Optional.of("money"),
                Optional.of(40.00),
                Optional.of(60.00),
                Optional.empty());
        assertTrue(predicate.test(alice));
    }

    // ======================== Equality tests ========================

    @Test
    public void equals_sameObject_returnsTrue() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("alice"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        assertEquals(predicate, predicate);
    }

    @Test
    public void equals_sameValues_returnsTrue() {
        PersonMatchesFilterPredicate first = buildPredicate(
                Optional.of("alice"), Optional.of("lunch"), Optional.of(10.0), Optional.of(50.0), Optional.of("frd"));
        PersonMatchesFilterPredicate second = buildPredicate(
                Optional.of("alice"), Optional.of("lunch"), Optional.of(10.0), Optional.of(50.0), Optional.of("frd"));
        assertEquals(first, second);
    }

    @Test
    public void equals_differentNameKeyword_returnsFalse() {
        PersonMatchesFilterPredicate first = buildPredicate(
                Optional.of("alice"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        PersonMatchesFilterPredicate second = buildPredicate(
                Optional.of("bob"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        assertFalse(first.equals(second));
    }

    @Test
    public void equals_differentMinAmount_returnsFalse() {
        PersonMatchesFilterPredicate first = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.of(10.0), Optional.empty(), Optional.empty());
        PersonMatchesFilterPredicate second = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.of(20.0), Optional.empty(), Optional.empty());
        assertFalse(first.equals(second));
    }

    @Test
    public void equals_nullObject_returnsFalse() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("alice"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        assertFalse(predicate.equals(null));
    }

    @Test
    public void equals_differentType_returnsFalse() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("alice"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        assertFalse(predicate.equals(new Tag("friends")));
    }

    // ======================== toString test ========================

    @Test
    public void toString_containsAllFields() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("alice"),
                Optional.of("lunch"),
                Optional.of(5.0),
                Optional.of(100.0),
                Optional.of("friends"));
        String result = predicate.toString();
        assertTrue(result.contains("alice"));
        assertTrue(result.contains("lunch"));
        assertTrue(result.contains("5.0"));
        assertTrue(result.contains("100.0"));
        assertTrue(result.contains("friends"));
    }

    // ======================== Getter tests ========================

    @Test
    public void getters_returnCorrectValues() {
        Optional<String> name = Optional.of("alice");
        Optional<String> desc = Optional.of("lunch");
        Optional<Double> min = Optional.of(5.0);
        Optional<Double> max = Optional.of(100.0);
        Optional<String> tag = Optional.of("friends");

        PersonMatchesFilterPredicate predicate = buildPredicate(name, desc, min, max, tag);

        assertEquals(name, predicate.getNameKeyword());
        assertEquals(desc, predicate.getDescriptionKeyword());
        assertEquals(min, predicate.getMinAmount());
        assertEquals(max, predicate.getMaxAmount());
        assertEquals(tag, predicate.getTagKeyword());
    }

    // ======================== Helper methods ========================

    /**
     * Builds a {@link PersonMatchesFilterPredicate} from the given optional filter values.
     *
     * @param nameKeyword        optional name keyword
     * @param descriptionKeyword optional description keyword
     * @param minAmount          optional minimum amount
     * @param maxAmount          optional maximum amount
     * @param tagKeyword         optional tag keyword
     * @return a configured predicate
     */
    private PersonMatchesFilterPredicate buildPredicate(
            Optional<String> nameKeyword,
            Optional<String> descriptionKeyword,
            Optional<Double> minAmount,
            Optional<Double> maxAmount,
            Optional<String> tagKeyword) {
        return new PersonMatchesFilterPredicate(nameKeyword, descriptionKeyword, minAmount, maxAmount, tagKeyword);
    }
}
