package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.ExpensesModel;
import de.cofinpro.splitter.model.Transactions;

import java.time.LocalDate;

/**
 * abstract base class for the pay command "borrow" and "repay" that bundles the arguments validation / processing
 * and error case. The actual command is called by an abstract hook method.
 */
public abstract class PayCommand implements LineCommand {

    protected final ConsolePrinter printer;
    protected final LocalDate date;
    protected String from;
    protected String to;
    protected long amount;
    private final boolean invalid;

    protected PayCommand(ConsolePrinter printer, LocalDate date, String[] arguments) {
        this.printer = printer;
        this.date = date;
        invalid = !validClArgumentsProcessed(arguments);
    }

    /**
     * does 3 validations on the arguments given: correct number of args, persons given are different and amount is
     * a valid money amount (max. 2 digits). Also, validated arguments are stored in field variables.
     * @param arguments arguments to be processed and validated
     * @return validation result.
     */
    private boolean validClArgumentsProcessed(String[] arguments) {
        if (arguments.length != 3) {
            return false;
        }
        from = arguments[0];
        to = arguments[1];
        if (from.equals(to)) {
            return false;
        }
        try {
            this.amount = getCentsFromDecimalInput(arguments[2]);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public void execute(ExpensesModel expensesModel) {
        if (invalid || amount == 0) {
            printer.printError(ERROR_INVALID);
        } else {
            executeMoneyTransfer(expensesModel.getTransactions());
        }
    }

    /**
     * do the happy path execution of money transfer.
     * @param transactions map of all transactions.
     */
    protected abstract void executeMoneyTransfer(Transactions transactions);
}
