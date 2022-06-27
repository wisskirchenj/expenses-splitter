package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.ExpensesModel;
import de.cofinpro.splitter.model.Group;
import de.cofinpro.splitter.model.Transactions;

import java.time.LocalDate;

/**
 * LineCommand implementation of the "purchase" command, used to create a purchase of one person for a group,
 * that is distributed in the command execution into adequate mutual borrow transactions.
 */
public class PurchaseCommand implements LineCommand {

    private final ConsolePrinter printer;
    private final boolean invalid;
    private final LocalDate date;
    private long amount;
    private String payer;
    private String groupName;

    public PurchaseCommand(ConsolePrinter printer, LocalDate date, String[] arguments) {
        this.printer = printer;
        this.date = date;
        invalid = !validClArgumentsProcessed(arguments);
    }

    /**
     * validates and processes the arguments given: less than 2 arguments, if argument given. must be "open"
     * or "close" = default.
     * @param arguments arguments to be processed and validated
     * @return validation result.
     */
    private boolean validClArgumentsProcessed(String[] arguments) {
        if (arguments.length < 4) {
            return false;
        }
        payer = arguments[0];
        try {
            this.amount = getCentsFromDecimalInput(arguments[2]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (!arguments[3].matches("\\([A-Z]+\\)")) {
            return false;
        }
        groupName = arguments[3].replaceAll("[()]", "");
        return true;
    }

    /**
     * execute the purchase, if command argumetns valid and group exists.
     * @param expensesModel the application model data
     */
    @Override
    public void execute(ExpensesModel expensesModel) {
        if (invalid || amount == 0 || expensesModel.getGroups().get(groupName) == null) {
            printer.printError(ERROR_INVALID);
        } else {
            executeGroupSplit(expensesModel);
        }
    }

    /**
     * split the amount by cent division and distribute possible remainder cent-wise to first persons in group.
     * Method also handles the case, when the payer belongs to the group; then no transaction is created for her share.
     * @param expensesModel the model data
     */
    private void executeGroupSplit(ExpensesModel expensesModel) {
        Group group = expensesModel.getGroups().get(groupName);
        long splitAmount = amount / group.size();
        long remainingCents = amount % group.size();
        for (String person : group) {
            if (!person.equals(payer)) {
                executeBorrowTransaction(expensesModel.getTransactions(), person,
                        remainingCents > 0 ? splitAmount + 1: splitAmount);
            }
            remainingCents--;
        }
    }

    private void executeBorrowTransaction(Transactions transactions, String borrower, long centAmount) {
            new BorrowCommand(printer, date, new String[] {borrower, payer, String.valueOf(centAmount / 100.0)})
                    .executeMoneyTransfer(transactions);
    }
}
