package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesFilterPredicate;
import seedu.address.model.transaction.Transaction;
import seedu.address.testutil.PersonBuilder;

/**
 * Integration tests for {@link FindCommand} interacting with {@link Model}.
 *
 * <p>Tests verify that filtering by name, description, amount range, tag, and all
 * combinations thereof correctly updates the filtered person list in the model.
 */
public class FindCommandTest {

    /** Alice owes 50 for "Lunch money", tagged "friends". */
    private Person alice;

    /** Benson owes 200 for "Rent split", tagged "owesMoney" and "friends". */
    private Person benson;

    /** Carl has no transactions, tagged "colleagues". */
    private Person carl;

    /** The creditor shared across test transactions. */
    private Person creditor;

    /** The model under test, populated with alice, benson, and carl. */
    private Model model;

    /** A parallel expected model for assertCommandSuccess comparisons. */
    private Model expectedModel;

    /**
     * Sets up the test fixture before every test method.
     *
     * <p>Three persons are built with distinct names, tags, and transactions so that
     * tests can target individual persons and verify AND-filter semantics.
     */
    @BeforeEach
    public void setUp() {
        creditor = new PersonBuilder().withName("Dave Lim").build();

        alice = new PersonBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123 Jurong West")
                .withTags("friends")
                .build();
        alice.appendTransaction(new Transaction(alice, creditor, 50.00, 0, "Lunch money"));

        benson = new PersonBuilder()
                .withName("Benson Meier")
                .withPhone("98765432")
                .withEmail("benson@example.com")
                .withAddress("311 Clementi Ave")
                .withTags("owesMoney", "friends")
                .build();
        benson.appendTransaction(new Transaction(benson, creditor, 200.00, 0, "Rent split"));

        carl = new PersonBuilder()
                .withName("Carl Kurz")
                .withPhone("95352563")
                .withEmail("carl@example.com")
                .withAddress("Wall Street")
                .withTags("colleagues")
                .build();

        AddressBook ab = new AddressBook();
        ab.addPerson(alice);
        ab.addPerson(benson);
        ab.addPerson(carl);

        model = new ModelManager(ab, new UserPrefs());
        expectedModel = new ModelManager(ab, new UserPrefs());
    }

    // ======================== Name filter ========================

    @Test
    public void execute_nameFilter_matchesOneResult() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("Alice"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.singletonList(alice), model.getFilteredPersonList());
    }

    @Test
    public void execute_nameFilter_matchesMultipleResults() {
        // "Meier" is in "Benson Meier"; partial match "er" hits both Alice and Benson and Carl
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("Meier"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.singletonList(benson), model.getFilteredPersonList());
    }

    @Test
    public void execute_nameFilter_caseInsensitiveMatch() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("alice"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(Collections.singletonList(alice), model.getFilteredPersonList());
    }

    @Test
    public void execute_nameFilterNoMatch_emptyResult() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("Zephyr"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0), expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    // ======================== Description filter ========================

    @Test
    public void execute_descriptionFilter_matchesPersonWithTransaction() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.of("Lunch"), Optional.empty(), Optional.empty(), Optional.empty());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(Collections.singletonList(alice), model.getFilteredPersonList());
    }

    @Test
    public void execute_descriptionFilter_personWithNoTransactionsExcluded() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.of("rent"), Optional.empty(), Optional.empty(), Optional.empty());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        // carl has no transactions, so only benson matches
        assertCommandSuccess(command, model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(Collections.singletonList(benson), model.getFilteredPersonList());
    }

    @Test
    public void execute_descriptionFilter_caseInsensitive() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.of("LUNCH MONEY"), Optional.empty(), Optional.empty(), Optional.empty());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(Collections.singletonList(alice), model.getFilteredPersonList());
    }

    // ======================== Min amount filter ========================

    @Test
    public void execute_minAmountFilter_excludesBelowThreshold() {
        // alice has 50, benson has 200; min 100 should return only benson
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.of(100.0), Optional.empty(), Optional.empty());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(Collections.singletonList(benson), model.getFilteredPersonList());
    }

    @Test
    public void execute_minAmountFilter_includesExactMatch() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.of(50.0), Optional.empty(), Optional.empty());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2), expectedModel);
        assertEquals(Arrays.asList(alice, benson), model.getFilteredPersonList());
    }

    // ======================== Max amount filter ========================

    @Test
    public void execute_maxAmountFilter_excludesAboveThreshold() {
        // alice has 50, benson has 200; max 100 should return only alice
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(100.0), Optional.empty());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(Collections.singletonList(alice), model.getFilteredPersonList());
    }

    @Test
    public void execute_maxAmountFilter_personWithNoTransactionsExcluded() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(10.0), Optional.empty());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        // carl has no transactions, alice has 50 (above 10), benson has 200 (above 10) → 0 results
        assertCommandSuccess(command, model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0), expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    // ======================== Amount range filter ========================

    @Test
    public void execute_amountRange_returnsOnlyPersonsWithinRange() {
        // min 10, max 100: alice (50) matches, benson (200) doesn't, carl (none) doesn't
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.of(10.0), Optional.of(100.0), Optional.empty());
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(Collections.singletonList(alice), model.getFilteredPersonList());
    }

    // ======================== Tag filter ========================

    @Test
    public void execute_tagFilter_matchesMultiplePersons() {
        // Both alice and benson are tagged "friends"
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("friends"));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2), expectedModel);
        assertEquals(Arrays.asList(alice, benson), model.getFilteredPersonList());
    }

    @Test
    public void execute_tagFilterUnique_matchesOne() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("colleagues"));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(Collections.singletonList(carl), model.getFilteredPersonList());
    }

    @Test
    public void execute_tagFilterCaseInsensitive_returnsMatch() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of("OWESMONEY"));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(Collections.singletonList(benson), model.getFilteredPersonList());
    }

    // ======================== Combined AND filter tests ========================

    @Test
    public void execute_nameAndTag_andSemanticsApplied() {
        // "Pauline" matches alice; tag "owesMoney" matches only benson → 0 results
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("Pauline"), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of("owesMoney"));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0), expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    @Test
    public void execute_nameAndDescriptionAndTag_allMatch() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("Benson"), Optional.of("Rent"), Optional.empty(), Optional.empty(),
                Optional.of("friends"));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(Collections.singletonList(benson), model.getFilteredPersonList());
    }

    @Test
    public void execute_allFiltersNarrows_toOneResult() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("Alice"),
                Optional.of("lunch"),
                Optional.of(10.0),
                Optional.of(100.0),
                Optional.of("friends"));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(Collections.singletonList(alice), model.getFilteredPersonList());
    }

    @Test
    public void execute_allFiltersNoMatching_emptyResult() {
        // alice doesn't have a tag "owesMoney", so this returns 0
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("Alice"),
                Optional.of("lunch"),
                Optional.of(10.0),
                Optional.of(100.0),
                Optional.of("owesMoney"));
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);

        assertCommandSuccess(command, model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0), expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredPersonList());
    }

    // ======================== Equality tests ========================

    @Test
    public void equals_sameObject_returnsTrue() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("Alice"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        FindCommand command = new FindCommand(predicate);
        assertTrue(command.equals(command));
    }

    @Test
    public void equals_samePredicate_returnsTrue() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("Alice"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        FindCommand first = new FindCommand(predicate);
        FindCommand second = new FindCommand(predicate);
        assertTrue(first.equals(second));
    }

    @Test
    public void equals_differentPredicate_returnsFalse() {
        FindCommand first = new FindCommand(buildPredicate(
                Optional.of("Alice"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
        FindCommand second = new FindCommand(buildPredicate(
                Optional.of("Benson"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
        assertFalse(first.equals(second));
    }

    @Test
    public void equals_differentType_returnsFalse() {
        FindCommand command = new FindCommand(buildPredicate(
                Optional.of("Alice"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
        assertFalse(command.equals("not a command"));
    }

    @Test
    public void equals_null_returnsFalse() {
        FindCommand command = new FindCommand(buildPredicate(
                Optional.of("Alice"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
        assertFalse(command.equals(null));
    }

    // ======================== toString test ========================

    @Test
    public void toStringMethod_containsPredicate() {
        PersonMatchesFilterPredicate predicate = buildPredicate(
                Optional.of("alice"), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        FindCommand command = new FindCommand(predicate);
        assertTrue(command.toString().contains(predicate.toString()));
    }

    // ======================== Helper methods ========================

    /**
     * Builds a {@link PersonMatchesFilterPredicate} from the given optional filter values.
     *
     * @param nameKeyword        optional name keyword
     * @param descriptionKeyword optional description keyword
     * @param minAmount          optional minimum amount (inclusive)
     * @param maxAmount          optional maximum amount (inclusive)
     * @param tagKeyword         optional tag keyword
     * @return a configured predicate
     */
    private PersonMatchesFilterPredicate buildPredicate(
            Optional<String> nameKeyword,
            Optional<String> descriptionKeyword,
            Optional<Double> minAmount,
            Optional<Double> maxAmount,
            Optional<String> tagKeyword) {
        return new PersonMatchesFilterPredicate(
                nameKeyword, descriptionKeyword, minAmount, maxAmount, tagKeyword);
    }
}
