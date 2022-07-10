package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.model.Repositories;

/**
 * Command-pattern interface: implementing classes are executable (Concrete) LineCommands
 */
public interface LineCommand {

    String ERROR_INVALID = "Illegal command arguments";
    String ERROR_UNKNOWN = "Unknown command. Print help to show commands list";

    /**
     * execute the command. Gets the complete application expemnses model as parameters, so the commands have access to
     * the created transactions as well as groups.
     */
    void execute(Repositories repositories);

    default boolean isExit() {
        return false;
    }

    /**
     * parses a decimal money value text input into its cents amount.
     * @param amountArgument decimal amount text
     * @return the cents amount as long
     * @throws NumberFormatException if text not parseable in to double or has more than 2 digits.
     */
    default long getCentsFromDecimalInput(String amountArgument) throws NumberFormatException {
        double centsAsDouble = Double.parseDouble(amountArgument) * 100;
        // take care of rounding errors (maybe change to BigDecimal after all ? :-)
        if (Math.abs(centsAsDouble - Math.round(centsAsDouble)) > 1e-6) {
            throw new NumberFormatException();
        }
        return Math.round(centsAsDouble);
    }
}
