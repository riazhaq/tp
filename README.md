[![Java CI](https://github.com/AY2526S2-CS2103T-T16-2/tp/actions/workflows/gradle.yml/badge.svg?branch=master)](https://github.com/AY2526S2-CS2103T-T16-2/tp/actions/workflows/gradle.yml)
![Ui](docs/images/Ui.png)

# IOU – Simple Debt Tracking for Friends and Colleagues

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
* Quickly record new transactions
* Track outstanding balances at a glance
* Keep financial records accurate and up-to-date
* Replace cluttered spreadsheets with a smooth, efficient workflow

---

## Features

### Add Person

The **Add Person** feature allows users to create a profile for a friend or colleague so that transactions can be associated with them.

Command format:
add n/NAME [p/PHONE] [e/EMAIL]

Example:
add n/Alex Lim p/91234567

The name must contain only alphanumeric characters and spaces and cannot be blank. Leading and trailing spaces are automatically removed, and multiple internal spaces are treated as a single space. The system checks for duplicate names in a case-insensitive manner to prevent confusion when recording debts. If a duplicate name already exists, the system will reject the command.

If the command is successful, the application will display the message:
“New person added: Alex Lim.”

Errors may occur if the name field is missing, blank, contains special characters, or if multiple name values are provided. Optional phone and email fields follow the standard validation rules from the base application.

---

### Add Transaction

The **Add Transaction** feature records that one person owes another person.

Command format:
`addtxn DEBTOR_INDEX CREDITOR_INDEX a/AMOUNT d/DESCRIPTION`

Example:
`addtxn 1 2 a/12.50 d/Dinner at Fish Market`

The debtor index refers to the person who owes the money, and the creditor index refers to the person who is owed the money. Both indices must be positive integers and must correspond to valid people in the currently displayed list. The amount must be a positive decimal value greater than zero and may contain up to two decimal places. Currency symbols are not allowed.

Multiple transactions between the same two people are allowed because users may need to track separate IOUs.

The description is required and cannot be empty.

If successful, the application displays a message like:
```text
New transaction added (#1): Alex Lim owes Sarah Tan - Dinner at Fish Market
```

Possible errors include invalid indices, the debtor and creditor being the same person, missing prefixes such as a/, an empty description, negative or zero amounts, currency symbols in the amount field, or values exceeding two decimal places.

---

### List People

The **List** command restores the full person list in the interface. It clears any filtered view and shows every person again.

Command format:
`list`

If the command is successful, the system displays:
```
Listed all persons.
```

An error will occur if additional parameters are provided, since the command does not accept arguments.

---

### Settle Transaction

The **Settle Transaction** feature allows users to mark a specific transaction as settled without deleting the record from the system. This preserves transaction history while ensuring the balance is updated.

Command format:
`settle PERSON_INDEX t/TRANSACTION_INDEX`

Example:
`settle 1 t/2`

The person index identifies the individual in the main list, while the transaction index refers to the specific transaction within that person's transaction history.

Once a transaction is settled, its status is updated in the interface and the person’s overall balance is recalculated.

Errors may occur if either index is invalid, if the transaction does not exist, if the command format is incorrect, or if the user attempts to settle a transaction that has already been settled.

---

### Settle Up

The **Settle Up** command settles all unsettled transactions among three or more selected people in one action.

Command format:
`settleup PERSON_INDEX [MORE_PERSON_INDEXES...]`

Example:
`settleup 1 2 3 4`

The command requires at least three distinct person indices, and every index must refer to a valid person in the current list. If successful, it marks every unsettled transaction that involves only the selected group as settled and reports how many transactions were updated.

Errors may occur if fewer than three distinct indices are provided, if any index is invalid, or if a duplicate person index is used.

---

### Simplify

The **Simplify** command computes a settlement plan for three or more selected people without changing any data.

Command format:
`simplify PERSON_INDEX [MORE_PERSON_INDEXES...]`

Example:
`simplify 1 2 3 4`

The command requires at least three distinct person indices, and every index must refer to a valid person in the current list. It analyzes unsettled transactions among the selected group and outputs a simplified payment plan.

Errors may occur if fewer than three distinct indices are provided, if any index is invalid, or if a duplicate person index is used.

---

### Delete Entry

The **Delete Entry** command allows users to remove either a person or a specific transaction from the records.

To delete a person:
`delete INDEX`

Example:
`delete 3`

To delete a transaction belonging to a person:
`delete INDEX t/TRANS_INDEX`

Example:
`delete 1 t/2`

Indices must always be positive integers that correspond to the positions shown in the current list view. When the list is filtered, the index refers to the visible position rather than the internal database ID.

If successful, the system displays messages such as:
```
“Deleted Alex Lim.”
```
or
```
“Deleted Transaction #2.”
```

Errors may occur if indices are out of range, non-numeric values are provided, or if the specified transaction does not exist.

---

### Auto-Save

IOU automatically saves all changes to ensure that no data is lost if the application closes unexpectedly. The auto-save function is triggered after any command that modifies the data, such as adding a person, adding a transaction, settling transactions, simplifying a settlement plan, or deleting an entry.

Data is stored in the following files:
* `data/addressbook.json`
* `data/addressbook_transactions.json`

If the system detects a corrupted data file when loading, it will display the message:
`Data file corrupted. Starting with an empty list.`

If the application encounters an input/output error while saving, it will display:
`ERROR: Unable to save data.`

---

## Command Summary

add – Add a new person to the system
addtxn – Record a transaction between two people (description required)
list – Display all people
settle – Mark a transaction as settled
settleup – Settle all transactions within a group
simplify – Show a simplified settlement plan for a group
delete – Remove a person or transaction from the records

---

## Example Workflow

A typical workflow may look like this:
```
add n/Alex Lim
add n/Sarah Tan

addtxn 1 2 a/12.50 d/Lunch
addtxn 2 1 a/30 d/Movie tickets

list

settle 1 t/1
simplify 1 2
settleup 1 2
```

This sequence adds two people, records two transactions, restores the full list, settles one transaction, previews a simplified plan, and then settles all transactions within the selected group.

---

## Project Information

IOU is a command-driven financial tracking application developed as part of a software engineering project. The system focuses on simplicity and speed, allowing users to record transactions quickly while maintaining a clear overview of balances and settlement history.
