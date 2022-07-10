package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.model.Repositories;

public class ExitCommand implements LineCommand {

    @Override
    public void execute(Repositories repositories) {
        // nothing to do, because isExit is questioned and runner stopped before execution
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
