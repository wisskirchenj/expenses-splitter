package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.Transactions;

import java.time.LocalDate;

public class BalanceCommand implements LineCommand {

    private final ConsolePrinter printer;
    private final LocalDate balanceDate;
    private boolean isOpenBalance = false;
    private final boolean invalid;

    public BalanceCommand(ConsolePrinter printer, LocalDate date, String[] arguments) {
        this.printer = printer;
        invalid = !validClArgumentsProcessed(arguments);
        if (isOpenBalance) {
            balanceDate = date.minusDays(date.getDayOfMonth());
        } else {
            balanceDate = date;
        }
    }

    private boolean validClArgumentsProcessed(String[] arguments) {
        if (arguments.length > 1) {
            return false;
        }
        if (arguments.length == 0 || arguments[0].equalsIgnoreCase("close")) {
            return true;
        }
        if (arguments[0].equalsIgnoreCase("open")) {
            isOpenBalance = true;
            return true;
        }
        return false;
    }

    @Override
    public void execute(Transactions transactions) {
        if (invalid) {
            printer.printError(ERROR_INVALID);
        } else {
            printer.printOwes(transactions.getBalances(balanceDate));
        }
    }
}
