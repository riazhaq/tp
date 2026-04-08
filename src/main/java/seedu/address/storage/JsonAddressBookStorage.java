package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;

/**
 * A class to access AddressBook data stored as JSON files on the hard disk.
 * Persons are stored in the primary file (e.g. addressbook.json).
 * Transactions are stored in a sibling file with "_transactions" appended before
 * the extension (e.g. addressbook_transactions.json), derived automatically from
 * the address book file path.
 */
public class JsonAddressBookStorage implements AddressBookStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonAddressBookStorage.class);

    private final Path filePath;

    public JsonAddressBookStorage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Derives the transaction file path from the address book file path by inserting
     * "_transactions" before the file extension.
     * e.g. "data/addressbook.json" → "data/addressbook_transactions.json"
     */
    static Path toTransactionFilePath(Path addressBookFilePath) {
        String fileName = addressBookFilePath.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        String transactionFileName = dotIndex == -1
                ? fileName + "_transactions"
                : fileName.substring(0, dotIndex) + "_transactions" + fileName.substring(dotIndex);
        return addressBookFilePath.resolveSibling(transactionFileName);
    }

    @Override
    public Path getAddressBookFilePath() {
        return filePath;
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
        return readAddressBook(filePath);
    }

    /**
     * Reads persons from {@code filePath}, then loads transactions from the derived
     * transaction file path and attaches them to the relevant persons.
     *
     * @param filePath location of the address book data file. Cannot be null.
     * @throws DataLoadingException if loading either file fails.
     */
    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);
        Path transactionFilePath = toTransactionFilePath(filePath);

        // Step 1: load persons
        Optional<JsonSerializableAddressBook> jsonAddressBook =
                JsonUtil.readJsonFile(filePath, JsonSerializableAddressBook.class);

        if (!jsonAddressBook.isPresent()) {
            return Optional.empty();
        }

        AddressBook addressBook;
        try {
            addressBook = jsonAddressBook.get().toModelType();
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }

        // Step 2: load transactions and attach to persons (persons must be loaded first)
        Optional<JsonSerializableTransactionBook> jsonTransactionBook =
                JsonUtil.readJsonFile(transactionFilePath, JsonSerializableTransactionBook.class);

        if (jsonTransactionBook.isPresent()) {
            jsonTransactionBook.get().loadInto(addressBook);
        }

        return Optional.of(addressBook);
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, filePath);
    }

    /**
     * Saves persons to {@code filePath} and transactions to the derived transaction file path.
     *
     * @param filePath location of the address book data file. Cannot be null.
     */
    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
        requireNonNull(addressBook);
        requireNonNull(filePath);
        Path transactionFilePath = toTransactionFilePath(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableAddressBook(addressBook), filePath);

        FileUtil.createIfMissing(transactionFilePath);
        JsonUtil.saveJsonFile(new JsonSerializableTransactionBook(addressBook), transactionFilePath);
    }
}
