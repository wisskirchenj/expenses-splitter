package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.PersonPair;
import de.cofinpro.splitter.model.Repositories;

import java.time.LocalDate;

/**
 * implementation of the LineCommand for "repay". The argument processing is done in the abstract base class.
 */
public class RepayCommand extends PayCommand {

    public RepayCommand(ConsolePrinter printer, LocalDate date, String[] arguments) {
        super(printer, date, arguments);
    }

    /**
     * add the transaction to the list of the PairTransactions to the persons given (if exists - or create).
     * The amount may have to be negated, depending on the position of the borrower in the PersonPair key.
     * @param repositories the repositories.
     */
    @Override
    public void executeMoneyTransfer(Repositories repositories) {
        PersonPair personPair = new PersonPair(from, to);
        // repay => the from person gets a negative balance attribution. So if from is 1st in pair, we need to switch amount to negative
        if (personPair.getFirst().equals(from)) {
            amount *= -1;
        }
        addTransaction(repositories, personPair);
    }
}