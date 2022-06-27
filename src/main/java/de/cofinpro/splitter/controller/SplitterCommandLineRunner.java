package de.cofinpro.splitter.controller;

import de.cofinpro.splitter.controller.command.LineCommand;
import de.cofinpro.splitter.model.ExpensesModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Spring CommandLineRunner implementation,that runs after contextLoad and acts as the main
 * routine, hosting the command line loop. It also acts as the client of the Command-Pattern and
 * offers the specific LineCommands it executes the autowired transactions hash-map.
 */
@Component
public class SplitterCommandLineRunner implements CommandLineRunner {

    private final CommandLineInterpreter commandLineInterpreter;
    private final ExpensesModel expensesModel;

    @Autowired
    public SplitterCommandLineRunner(CommandLineInterpreter commandLineInterpreter,
                                     ExpensesModel expensesModel) {
        this.commandLineInterpreter = commandLineInterpreter;
        this.expensesModel = expensesModel;
    }

    @Override
    public void run(String... args) {
        LineCommand command = commandLineInterpreter.parseNext();
        while (!command.isExit()) {
            command.execute(expensesModel);
            command = commandLineInterpreter.parseNext();
        }
    }
}
