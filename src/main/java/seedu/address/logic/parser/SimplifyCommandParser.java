package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.SimplifyCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code SimplifyCommand} object.
 */
public class SimplifyCommandParser implements Parser<SimplifyCommand> {

    @Override
    public SimplifyCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SimplifyCommand.MESSAGE_USAGE));
        }

        String[] tokens = trimmedArgs.split("\\s+");
        List<Index> indices = new ArrayList<>();
        Set<Integer> uniqueOneBasedIndices = new HashSet<>();

        for (String token : tokens) {
            Index parsedIndex;
            try {
                parsedIndex = ParserUtil.parseIndex(token);
            } catch (ParseException pe) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, SimplifyCommand.MESSAGE_USAGE), pe);
            }

            if (!uniqueOneBasedIndices.add(parsedIndex.getOneBased())) {
                throw new ParseException(
                        String.format(SimplifyCommand.MESSAGE_DUPLICATE_PERSON_INDEX, parsedIndex.getOneBased()));
            }
            indices.add(parsedIndex);
        }

        if (indices.size() < 3) {
            throw new ParseException(SimplifyCommand.MESSAGE_MINIMUM_PARTICIPANTS);
        }

        return new SimplifyCommand(indices);
    }
}
