---
layout: page
title: User Guide
---

# About IOU

## What is IOU?
IOU is a desktop application designed to help you keep track of small debts and loans between friends, colleagues, or roommates. Prefer typing over clicking? You’ll feel right at home here. Commands let you work fast, while the interface keeps everything visible and organized.

By using simple text commands instead of spreadsheets or mobile apps, IOU allows users to log financial transactions directly from their laptops in seconds. It’s ideal for staying on top of peer-to-peer debts during workdays, social events, or shared living situations.

## Why is this app needed?
Managing multiple small debts can be tedious and error-prone, especially when relying on spreadsheets or phone apps. These tools can be slow, disruptive, or hard to reference quickly.

IOU provides a keyboard-friendly, streamlined solution that lets users record, check, and update financial records instantly—saving time, reducing mistakes, and keeping your personal finances organized.

## Who are the target users?
IOU is especially suited for people who:
* Spend most of their day on a laptop or desktop
* Handle multiple informal loans or shared expenses
* Prefer typing commands over navigating menus or clicking buttons
* Want fast, clear, and organized access to personal finance records

## What value does IOU provide?
With IOU, users can:
* Quickly record new debts and loans
* Track outstanding balances at a glance
* Keep financial records accurate and up-to-date
* Replace cluttered spreadsheets with a smooth, efficient workflow


* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure you have Java `17` or above installed in your Computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/se-edu/addressbook-level3/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your AddressBook.

1. Open a command terminal, `cd` into the folder you put the jar file in, and use the `java -jar addressbook.jar` command to run the application.<br>
   A GUI similar to the below should appear in a few seconds. Note how the app contains some sample data.<br>
  If no contact named `Me` exists yet, IOU inserts a default `Me` contact at the top of the list on startup.<br>
   ![Ui](images/Ui.png)

1. Type the command in the command box and press Enter to execute it. e.g. typing **`help`** and pressing Enter will open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01` : Adds a contact named `John Doe` to the Address Book.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  e.g. in `add n/NAME`, `NAME` is a parameter which can be used as `add n/John Doe`.

* Items in square brackets are optional.<br>
  e.g `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items with `…`​ after them can be used multiple times including zero times.<br>
  e.g. `[t/TAG]…​` can be used as ` ` (i.e. 0 times), `t/friend`, `t/friend t/family` etc.

* Parameters can be in any order.<br>
  e.g. if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that do not take in parameters (such as `help`, `list`, `exit` and `clear`) will be ignored.<br>
  e.g. if the command specifies `help 123`, it will be interpreted as `help`.

* Select a person in the person list to view that person's transactions in the transaction panel.

* For commands that target a transaction using `t/TRANSACTION_INDEX`, the transaction index refers to the order shown in the transaction panel, sorted from the largest current amount to the smallest.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help : `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a person: `add`

Adds a person to the address book.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​`

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A person can have any number of tags (including 0)
</div>

Examples:
* `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
* `add n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 t/criminal`

### Listing all persons : `list`

Shows a list of all persons in the address book.

Format: `list`

### Editing a person : `edit`

Edits an existing person in the address book.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, the existing tags of the person will be removed i.e adding of tags is not cumulative.
* You can remove all the person’s tags by typing `t/` without
    specifying any tags after it.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.

### Locating persons by name: `find`

Finds persons whose names contain any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive. e.g `hans` will match `Hans`
* The order of the keywords does not matter. e.g. `Hans Bo` will match `Bo Hans`
* Only the name is searched.
* Only full words will be matched e.g. `Han` will not match `Hans`
* Persons matching at least one keyword will be returned (i.e. `OR` search).
  e.g. `Hans Bo` will return `Hans Gruber`, `Bo Yang`

Examples:
* `find John` returns `john` and `John Doe`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Deleting a person : `delete`

Deletes the specified person from the address book.

Format: `delete INDEX`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete 2` deletes the 2nd person in the address book.
* `find Betsy` followed by `delete 1` deletes the 1st person in the results of the `find` command.

### Adding a transaction : `addtxn`

Adds a transaction between two persons in the address book.

Format: `addtxn DEBTOR_INDEX CREDITOR_INDEX a/AMOUNT i/INTEREST_RATE [d/DESCRIPTION] [t/COMPOUNDING_TYPE]`

* Adds a transaction from the debtor to the creditor at the specified indexes.
* The indexes refer to the index numbers shown in the displayed person list.
* Both indexes **must be positive integers** 1, 2, 3, …
* Both indexes **must be different** (a person cannot transact with themselves).
* `AMOUNT` must be a positive number.
* `INTEREST_RATE` must be a non-negative number.
* If `t/COMPOUNDING_TYPE` is omitted, the transaction is created with no compounding.
* `COMPOUNDING_TYPE` must be `m` (monthly), `y` (yearly), or `n` (none) if specified.
* The transaction appears in the transaction panel for both people involved.
* Items in square brackets are optional.

Examples:
* `addtxn 1 2 a/50 i/5 d/lunch t/m` adds a transaction where person 1 owes person 2 $50 at 5% monthly compounding interest for lunch.
* `addtxn 2 3 a/10 i/5 d/lunch t/m` adds a transaction where person 2 owes person 3 $10 at 5% monthly compounding interest for lunch.
* `addtxn 1 2 a/100 i/0` adds a transaction where person 1 owes person 2 $100 with no interest.

### Clearing all entries : `clear`

Clears all entries from the address book.

Format: `clear`

### Settling a transaction : `settle`

Marks a specific transaction as paid while keeping it in the history so the outstanding balance for the person is recalculated without losing the record.

Format: `settle PERSON_INDEX t/TRANSACTION_INDEX`

* The person index refers to the displayed person list.
* The transaction index refers to the displayed transaction panel for that selected person.
* Settling sets the transaction amount to `$0.00` and marks it as `Settled`, but keeps the record visible in the transaction history.

Example: `settle 1 t/2`

### Deleting a transaction : `delete`

Removes a specific transaction from a person; specifying both the person index and transaction index lets you target the exact entry.

Format: `delete INDEX t/TRANSACTION_INDEX`

* The person index refers to the displayed person list.
* The transaction index refers to the displayed transaction panel for that selected person.
* Deleting a transaction removes the same shared record from both the debtor and the creditor.

Example: `delete 1 t/2`

### Exiting the program : `exit`

Exits the program.

Format: `exit`

### Saving the data

IOU saves data to disk automatically after any command that changes the data. There is no need to save manually.

Person data is stored in `[JAR file location]/data/addressbook.json`.
Transaction data is stored in `[JAR file location]/data/addressbook_transactions.json`.

### Editing the data file

Advanced users can edit the JSON files directly.

* `[JAR file location]/data/addressbook.json` stores persons.
* `[JAR file location]/data/addressbook_transactions.json` stores transactions.
* If you edit transactions manually, debtor and creditor entries must still match valid persons in the address book.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make either file invalid, IOU may fail to load your saved data correctly at the next run. Hence, it is recommended to take a backup of both files before editing them.<br>
Furthermore, certain edits can cause IOU to behave in unexpected ways if person and transaction records no longer match. Therefore, edit the data files only if you are confident that you can update them correctly.
</div>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another Computer?<br>
**A**: Install the app in the other computer and overwrite the empty data file it creates with the file that contains the data of your previous AddressBook home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 t/friend t/colleague`
**Clear** | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit** | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`<br> e.g.,`edit 2 n/James Lee e/jameslee@example.com`
**Find** | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**List** | `list`
**Help** | `help`
