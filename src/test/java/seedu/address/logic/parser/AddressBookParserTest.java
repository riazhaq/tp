package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.SettleCommand;
import seedu.address.logic.commands.SettleUpCommand;
import seedu.address.logic.commands.SimplifyCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonMatchesFilterPredicate;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

/**
 * Unit tests for {@link AddressBookParser}.
 *
 * <p>Each test verifies that the parser correctly dispatches a command word to the
 * appropriate {@link seedu.address.logic.commands.Command} subclass and that the
 * resulting command is equal to one constructed with the expected parameters.
 */
public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertThrows(ParseException.class, () -> parser.parseCommand(ClearCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        descriptor.setTags(null);
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    /**
     * Verifies that {@code find n/Alice} is parsed into a {@link FindCommand} wrapping a
     * {@link PersonMatchesFilterPredicate} with only the name keyword set.
     *
     * <p>The old plain-keyword syntax (e.g. {@code find foo bar baz}) is no longer
     * supported. The find command now requires at least one labelled prefix such as
     * {@code n/}, {@code d/}, {@code min/}, {@code max/}, or {@code t/}.
     */
    @Test
    public void parseCommand_find() throws Exception {
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " n/Alice");
        PersonMatchesFilterPredicate expectedPredicate = new PersonMatchesFilterPredicate(
                Optional.of("Alice"),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());
        assertEquals(new FindCommand(expectedPredicate), command);
    }

    /**
     * Verifies that {@code find} with multiple filters produces a command with all
     * the corresponding predicate fields populated correctly.
     */
    @Test
    public void parseCommand_find_multipleFilters() throws Exception {
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " n/Bob d/lunch min/10 max/200 t/friends");
        PersonMatchesFilterPredicate expectedPredicate = new PersonMatchesFilterPredicate(
                Optional.of("Bob"),
                Optional.of("lunch"),
                Optional.of(10.0),
                Optional.of(200.0),
                Optional.of("friends"));
        assertEquals(new FindCommand(expectedPredicate), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_settle() throws Exception {
        SettleCommand command = (SettleCommand) parser.parseCommand(
                SettleCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased() + " t/1");
        assertEquals(new SettleCommand(INDEX_FIRST_PERSON, Index.fromOneBased(1)), command);
    }

    @Test
    public void parseCommand_simplify() throws Exception {
        SimplifyCommand command = (SimplifyCommand) parser.parseCommand(
                SimplifyCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased() + " 2 3");
        assertEquals(new SimplifyCommand(List.of(
                INDEX_FIRST_PERSON,
                Index.fromOneBased(2),
                Index.fromOneBased(3))), command);
    }

    @Test
    public void parseCommand_settleup() throws Exception {
        SettleUpCommand command = (SettleUpCommand) parser.parseCommand(
                SettleUpCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased() + " 2 3");
        assertEquals(new SettleUpCommand(List.of(
                INDEX_FIRST_PERSON,
                Index.fromOneBased(2),
                Index.fromOneBased(3))), command);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
                -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
