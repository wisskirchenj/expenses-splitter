package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.Transactions;

import java.time.LocalDate;

public abstract class PayCommand implements LineCommand {

    protected final ConsolePrinter printer;
    protected final LocalDate date;
    protected String from;
    protected String to;
    protected int amount = 0;
    private final boolean invalid;

    protected PayCommand(ConsolePrinter printer, LocalDate date, String[] arguments) {
        this.printer = printer;
        this.date = date;
        invalid = !validClArgumentsProcessed(arguments);
    }

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
            this.amount = Integer.parseInt(arguments[2]);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @Override
    public void execute(Transactions transactions) {
        if (invalid) {
            printer.printError(ERROR_INVALID);
        } else {
            executeMoneyTransfer(transactions);
        }
    }

    protected abstract void executeMoneyTransfer(Transactions transactions);
}
