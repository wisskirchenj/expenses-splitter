package de.cofinpro.splitter.controller;

import de.cofinpro.splitter.controller.command.LineCommand;
import de.cofinpro.splitter.model.Transactions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SplitterCommandLineRunner implements CommandLineRunner {

    private final CommandLineInterpreter commandLineInterpreter;
    private final Transactions transactions;

    @Autowired
    public SplitterCommandLineRunner(CommandLineInterpreter commandLineInterpreter,
                                     Transactions transactions) {
        this.commandLineInterpreter = commandLineInterpreter;
        this.transactions = transactions;
    }

    @Override
    public void run(String... args) {
        LineCommand command = commandLineInterpreter.parseNext();
        while (!command.isExit()) {
            command.execute(transactions);
            command = commandLineInterpreter.parseNext();
        }
    }
}
