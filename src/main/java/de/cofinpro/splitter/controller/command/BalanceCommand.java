package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.controller.PersonsResolver;
import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.BalanceOptimizer;
import de.cofinpro.splitter.model.PairBalanceRecord;
import de.cofinpro.splitter.model.Repositories;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * implementation of LineCommand for the "balance" and "balancePerfect" command (extra constructor) execution.
 */
public class BalanceCommand implements LineCommand {

    private final ConsolePrinter printer;
    private final LocalDate balanceDate;
    private final boolean isPerfect;
    private boolean isOpenBalance = false;
    private final boolean invalid;

    private String[] personsTokens = new String[0];

    public BalanceCommand(ConsolePrinter printer, LocalDate date, String[] arguments) {
        this(printer, date, false, arguments);
    }

    public BalanceCommand(ConsolePrinter printer, LocalDate date, boolean isPerfect, String[] arguments) {
        this.printer = printer;
        this.isPerfect = isPerfect;
        invalid = !validClArgumentsProcessed(arguments);
        if (isOpenBalance) {
            balanceDate = date.minusDays(date.getDayOfMonth());
        } else {
            balanceDate = date;
        }
    }

    /**
     * validates and processes the arguments given: the first argument can be presen as "open"
     * or "close" = default. Optional further arguments are persons, group lists.
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

    /**
     * resolve and return the person names given by the list arguments
     * @param arguments list arguments
     * @return true, if resolving succeeds, false if syntax error in group parameters
     */
    private boolean personsResolved(String[] arguments) {
        personsTokens = PersonsResolver.tokenizePersonsArguments(arguments);
        return personsTokens.length != 0;
    }

    /** if valid arguments were given,
     * get all balance texts from the overall transactions and hand them over for printing.
     * Consider the optional persons filter on the owing side (owerFilter) and do a balance
     * optimization using the BalanceOptimizer class, if the command was "balancePerfect".
     * @param repositories the repositories
     */
    @Override
    public void execute(Repositories repositories) {
        if (invalid) {
            printer.printError(ERROR_INVALID);
        } else {
            Collection<String> owerFilter
                    = PersonsResolver.resolvePersonsFromTokens(personsTokens, repositories.getGroupRepository());
            List<PairBalanceRecord> balances = repositories.getTransactionRepository().getBalances(balanceDate)
                    .stream()
                    .map(PairBalanceRecord::fromJpa)
                    .filter(pb -> pb.balance() != 0)
                    .filter(pb -> {
                        if (owerFilter.isEmpty()) {
                            return true;
                        } else {
                            return owerFilter.contains(pb.balance() < 0 ? pb.second() : pb.first());
                        }
                    })
                    .toList();

            if (isPerfect) {
                balances = new BalanceOptimizer().optimize(balances);
            }
            printBalances(balances);
        }
    }

    private void printBalances(List<PairBalanceRecord> balances) {
        printer.printOwes(balances.stream().map(this::getOwesText).sorted().toList());
    }


    private String getOwesText(PairBalanceRecord pairBalance) {
        String ower = pairBalance.balance() < 0 ? pairBalance.second() : pairBalance.first();
        String owee = pairBalance.balance() < 0 ? pairBalance.first() : pairBalance.second();
        return String.format(Locale.US, "%s owes %s %.2f", ower, owee, Math.abs(pairBalance.balance()) / 100.0);
    }
}