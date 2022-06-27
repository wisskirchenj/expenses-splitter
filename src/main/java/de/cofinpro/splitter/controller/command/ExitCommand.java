package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.model.ExpensesModel;

public class ExitCommand implements LineCommand {

    @Override
    public void execute(ExpensesModel expensesModel) {
        // nothing to do, because isExit is questioned and runner stopped before execution
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
