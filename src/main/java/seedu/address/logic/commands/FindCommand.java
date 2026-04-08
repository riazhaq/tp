package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.PersonMatchesFilterPredicate;

/**
 * Finds and lists all persons in the address book whose details match all provided filters.
 *
 * <p>The following optional filters can be combined freely:
 * <ul>
 *   <li>{@code n/} – person's name contains the keyword (partial, case-insensitive)</li>
 *   <li>{@code d/} – any transaction description contains the keyword (partial, case-insensitive)</li>
 *   <li>{@code min/} – at least one transaction amount is ≥ the specified minimum</li>
 *   <li>{@code max/} – at least one transaction amount is ≤ the specified maximum</li>
 *   <li>{@code t/} – person has a tag matching the keyword (case-insensitive)</li>
 * </ul>
 *
 * <p>Only persons satisfying <em>all</em> supplied filters are shown.
 */
public class FindCommand extends Command {

    /** The command keyword used to invoke this command from the CLI. */
    public static final String COMMAND_WORD = "find";

    /** Usage message displayed when the command is used incorrectly. */
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds all persons who match ALL of the provided filters.\n"
            + "All filters are optional, but at least one must be provided.\n"
            + "Parameters: [n/NAME] [d/DESCRIPTION] [min/MIN_AMOUNT] [max/MAX_AMOUNT] [t/TAG]\n"
            + "  n/   - Person's name (partial match, case-insensitive)\n"
            + "  d/   - Transaction description (partial match, case-insensitive)\n"
            + "  min/ - Minimum transaction amount (inclusive)\n"
            + "  max/ - Maximum transaction amount (inclusive)\n"
            + "  t/   - Tag name (partial match, case-insensitive)\n"
            + "Example: " + COMMAND_WORD + " n/alice min/10 t/friends";

    /** The composite predicate that encapsulates all active search filters. */
    private final PersonMatchesFilterPredicate predicate;

    /**
     * Creates a {@code FindCommand} that will filter the person list using the given predicate.
     *
     * @param predicate the composite filter predicate; must not be {@code null}
     */
    public FindCommand(PersonMatchesFilterPredicate predicate) {
        requireNonNull(predicate);
        this.predicate = predicate;
    }

    /**
     * Executes the find command by updating the filtered person list in the model.
     *
     * @param model the application model; must not be {@code null}
     * @return a {@link CommandResult} reporting how many persons matched the filters
     */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof FindCommand)) {
            return false;
        }

        FindCommand otherFindCommand = (FindCommand) other;
        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public int hashCode() {
        return predicate.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
