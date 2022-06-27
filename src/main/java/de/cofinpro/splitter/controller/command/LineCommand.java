package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.model.ExpensesModel;

/**
 * Command-pattern interface: implementing classes are executable (Concrete) LineCommands
 */
public interface LineCommand {

    String ERROR_INVALID = "Illegal command arguments";
    String ERROR_UNKNOWN = "Unknown command. Print help to show commands list";

    /**
     * execute the command.
     */
    void execute(ExpensesModel expensesModel);

    default boolean isExit() {
        return false;
    }
}
