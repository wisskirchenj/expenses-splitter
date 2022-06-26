package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.Transactions;

import java.util.Arrays;

/**
 * the help commands just sends all commmands in lower case and sorted to the console printer.
 */
public class HelpCommand implements LineCommand {

    private final ConsolePrinter printer;
    public HelpCommand(ConsolePrinter printer) {
        this.printer = printer;
    }

    @Override
    public void execute(Transactions transactions) {
        Arrays.stream(CommandType.values())
                .map(CommandType::name)
                .map(String::toLowerCase)
                .sorted()
                .forEach(printer::printInfo);
    }
}
