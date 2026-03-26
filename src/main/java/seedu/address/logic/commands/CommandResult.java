package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.OptionalInt;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /**
     * The 1-based index of the person whose transaction panel should be refreshed
     * after this command, or empty if no refresh is needed.
     *
     * <p>Set by commands such as {@code DeleteCommand} (transaction variant) so that
     * {@code MainWindow} can re-render the {@code TransactionListPanel} with the
     * updated person data without relying on a ListView re-selection event.
     */
    private final OptionalInt personIndexToRefresh;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.personIndexToRefresh = OptionalInt.empty();
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false);
    }

    /**
     * Constructs a {@code CommandResult} that also signals {@code MainWindow} to
     * refresh the transaction panel for the person at the given 1-based
     * {@code personOneBased} index.
     *
     * @param feedbackToUser  message to display in the result box
     * @param personOneBased  1-based index of the person whose panel needs refresh
     */
    public CommandResult(String feedbackToUser, int personOneBased) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = false;
        this.exit = false;
        this.personIndexToRefresh = OptionalInt.of(personOneBased);
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isExit() {
        return exit;
    }

    /**
     * Returns the 1-based index of the person whose transaction panel should be
     * refreshed, or {@link OptionalInt#empty()} if no refresh is needed.
     */
    public OptionalInt getPersonIndexToRefresh() {
        return personIndexToRefresh;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult)) {
            return false;
        }

        CommandResult otherCommandResult = (CommandResult) other;
        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit
                && personIndexToRefresh.equals(otherCommandResult.personIndexToRefresh);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit, personIndexToRefresh);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .add("personIndexToRefresh", personIndexToRefresh)
                .toString();
    }

}
