package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.Repositories;

import java.time.LocalDate;

/**
 * implementation of LineCommand for the "balance" command execution.
 */
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

    /**
     * validates and processes the arguments given: correct number of args (1 or 0), if argument given. must be "open"
     * or "close" = default.
     * @param arguments arguments to be processed and validated
     * @return validation result.
     */
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

    /** if valid arguments were given,
     * get all balance texts from the overall transactions and hand them over for printing.
     * @param repositories the model data with the transactions map
     */
    @Override
    public void execute(Repositories repositories) {
        if (invalid) {
            printer.printError(ERROR_INVALID);
        } else {
            printer.printOwes(repositories.getTransactions().getBalances(balanceDate));
        }
    }
}
