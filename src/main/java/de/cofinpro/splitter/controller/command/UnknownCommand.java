package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.Transactions;

/**
 * class for an unknown command, that prints an error message as execution
 */
public class UnknownCommand implements LineCommand {

    private final ConsolePrinter printer;

    public UnknownCommand(ConsolePrinter printer) {
        this.printer = printer;
    }

    @Override
    public void execute(Transactions transactions) {
        printer.printError(ERROR_UNKNOWN);
    }
}
