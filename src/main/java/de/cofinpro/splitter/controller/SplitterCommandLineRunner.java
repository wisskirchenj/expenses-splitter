package de.cofinpro.splitter.controller;

import de.cofinpro.splitter.controller.command.LineCommand;
import de.cofinpro.splitter.model.Repositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Spring CommandLineRunner implementation,that runs after contextLoad and acts as the main
 * routine, hosting the command line loop. It also acts as the client of the Command-Pattern and
 * offers the specific LineCommands it executes the autowired expenses model.
 */
@ConditionalOnProperty(
        prefix = "command.line.runner",
        value = "enabled",
        havingValue = "true",
        matchIfMissing = true)
@Component
public class SplitterCommandLineRunner implements CommandLineRunner {

    private final CommandLineInterpreter commandLineInterpreter;
    private final Repositories repositories;

    @Autowired
    public SplitterCommandLineRunner(CommandLineInterpreter commandLineInterpreter,
                                     Repositories repositories) {
        this.commandLineInterpreter = commandLineInterpreter;
        this.repositories = repositories;
    }

    @Override
    public void run(String... args) {
        LineCommand command = commandLineInterpreter.parseNext();
        while (!command.isExit()) {
            command.execute(repositories);
            command = commandLineInterpreter.parseNext();
        }
    }
}
