package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.controller.PersonsResolver;
import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.Repositories;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

import static de.cofinpro.splitter.controller.command.GroupCommand.EMPTY_GROUP;

/**
 * LineCommand implementation of the "purchase" command, used to create a purchase of one person for a group,
 * that is distributed in the command execution into adequate mutual borrow transactions.
 */
public class PurchaseCommand implements LineCommand {

    private final ConsolePrinter printer;
    private final boolean invalid;
    private final LocalDate date;
    private final boolean isCashback;
    private long amount;
    private String payerOrRefunder;
    private String[] personsTokens;

    public PurchaseCommand(ConsolePrinter printer, LocalDate date, String[] arguments) {
        this(printer, date, false, arguments);
    }


    public PurchaseCommand(ConsolePrinter printer, LocalDate date, boolean isCashback, String[] arguments) {
        this.printer = printer;
        this.date = date;
        this.isCashback = isCashback;
        invalid = !validClArgumentsProcessed(arguments);
    }

    /**
     * validates and processes the arguments given: less than 2 arguments, if argument given. must be "open"
     * or "close" = default.
     *
     * @param arguments arguments to be processed and validated
     * @return validation result.
     */
    private boolean validClArgumentsProcessed(String[] arguments) {
        if (arguments.length < 4) {
            return false;
        }
        payerOrRefunder = arguments[0];
        try {
            this.amount = getCentsFromDecimalInput(arguments[2]);
        } catch (NumberFormatException e) {
            return false;
        }
        personsTokens = PersonsResolver.tokenizePersonsArguments(Arrays.copyOfRange(arguments, 3, arguments.length));
        return personsTokens.length != 0;
    }

    /**
     * execute the purchase, if command arguments valid and group exists.
     *
     * @param repositories the application model data
     */
    @Override
    public void execute(Repositories repositories) {
        if (invalid || amount == 0) {
            printer.printError(ERROR_INVALID);
        } else {
            Collection<String> personsToSplit =
                    PersonsResolver.resolvePersonsFromTokens(personsTokens, repositories.getGroupRepository());
            if (personsToSplit.isEmpty()) {
                printer.printError(EMPTY_GROUP);
            } else {
                executeGroupSplit(personsToSplit, repositories);
            }
        }
    }

    /**
     * split the amount by cent division and distribute possible remainder cent-wise to first persons in group.
     * Method also handles the case, when the payer belongs to the group; then no transaction is created for her share.
     *
     * @param personsToSplit group of person names for splitting the amount.
     * @param repositories the application model data
     */
    private void executeGroupSplit(Collection<String> personsToSplit, Repositories repositories) {
        long splitAmount = amount / personsToSplit.size();
        long remainingCents = amount % personsToSplit.size();
        for (String person : personsToSplit) {
            if (!person.equals(payerOrRefunder)) {
                executeBorrowTransaction(repositories, person, remainingCents > 0 ? splitAmount + 1 : splitAmount);
            }
            remainingCents--;
        }
    }

    private void executeBorrowTransaction(Repositories repositories, String borrower, long centAmount) {
        String transactionAmount = String.valueOf((isCashback ? centAmount * -1 : centAmount) / 100.0);
        new BorrowCommand(printer, date, new String[]{borrower, payerOrRefunder, transactionAmount})
                .execute(repositories);
    }
}
