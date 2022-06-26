package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.Transactions;

/**
 * class for a recognized, but invalid command (syntax), that prints an error message as execution
 */
public class InvalidCommand implements LineCommand {

    private final ConsolePrinter printer;

    public InvalidCommand(ConsolePrinter printer) {
        this.printer = printer;
    }

    @Override
    public void execute(Transactions transactions) {
        printer.printError(ERROR_INVALID);
    }
}
