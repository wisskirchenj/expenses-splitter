package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.controller.PersonsResolver;
import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.PairBalance;
import de.cofinpro.splitter.model.Repositories;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static java.util.function.Predicate.not;

/**
 * implementation of LineCommand for the "balance" command execution.
 */
public class BalanceCommand implements LineCommand {

    private final ConsolePrinter printer;
    private final LocalDate balanceDate;
    private boolean isOpenBalance = false;
    private final boolean invalid;

    private String[] personsTokens = new String[0];

    public BalanceCommand(ConsolePrinter printer, LocalDate date, String[] arguments) {
        this.printer = printer;
        invalid = !validClArgumentsProcessed(arguments);
        if (isOpenBalance) {
            balanceDate = date.minusDays(date.getDayOfMonth());
        } else {
            balanceDate = date;
        }
    }

    /**
     * validates and processes the arguments given: correct number of args (1 or 0), if argument given. must be "open"
     * or "close" = default.
     * @param arguments arguments to be processed and validated
     * @return validation result.
     */
    private boolean validClArgumentsProcessed(String[] arguments) {
        if (arguments.length == 0) {
            return true;
        }
        if (arguments[0].contains("(")) {
            return personsResolved(arguments);
        }
        if (arguments[0].equalsIgnoreCase("open")) {
            isOpenBalance = true;
        } else if (!arguments[0].equalsIgnoreCase("close")) {
            return false;
        }
        if (arguments.length > 1) {
            return personsResolved(Arrays.copyOfRange(arguments, 1, arguments.length));
        }
        return true;
    }

    private boolean personsResolved(String[] arguments) {
        personsTokens = PersonsResolver.tokenizePersonsArguments(arguments);
        return personsTokens.length != 0;
    }

    /** if valid arguments were given,
     * get all balance texts from the overall transactions and hand them over for printing.
     * @param repositories the repositories
     */
    @Override
    public void execute(Repositories repositories) {
        if (invalid) {
            printer.printError(ERROR_INVALID);
        } else {
            Collection<String> owerFilter
                    = PersonsResolver.resolvePersonsFromTokens(personsTokens, repositories.getGroupRepository());
            List<PairBalance> balances = repositories.getTransactionRepository().getBalances(balanceDate);
            printer.printOwes(balances.stream()
                    .map(this::getOwesText)
                    .filter(not(String::isEmpty))
                    .filter(owesText -> {
                        if (owerFilter.isEmpty()) {
                            return true;
                        } else {
                            return owerFilter.contains(owesText.substring(0, owesText.indexOf(" ")));
                        }
                    })
                    .sorted()
                    .toList());
        }
    }

    private String getOwesText(PairBalance pairBalance) {
        if (pairBalance.getBalance() == 0) {
            return "";
        }
        String ower = pairBalance.getBalance() < 0 ? pairBalance.getSecondPerson() : pairBalance.getFirstPerson();
        String owee = pairBalance.getBalance() < 0 ? pairBalance.getFirstPerson() : pairBalance.getSecondPerson();
        return String.format(Locale.US, "%s owes %s %.2f", ower, owee, Math.abs(pairBalance.getBalance()) / 100.0);
    }
}
