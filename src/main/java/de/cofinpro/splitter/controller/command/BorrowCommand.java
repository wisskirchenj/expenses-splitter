package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.PersonPair;
import de.cofinpro.splitter.model.Repositories;

import java.time.LocalDate;

/**
 * implementation of the LineCommand for "borrow". The argument processing is done in the abstract base class.
 */
public class BorrowCommand extends PayCommand {

    public BorrowCommand(ConsolePrinter printer, LocalDate date, String[] arguments) {
        super(printer, date, arguments);
    }

    /**
     * add the transaction and possibly new persons in it to the database.
     * The amount may have to be negated, depending on the position of the borrower in the PersonPair key.
     * @param repositories  the repositories.
     */
    @Override
    public void executeMoneyTransfer(Repositories repositories) {
        PersonPair personPair = new PersonPair(from, to);
        // borrow => the from person gets a positive balance attribution. So if from is 2nd in pair, we need to switch amount to negative
        if (personPair.getSecond().equals(from)) {
            amount *= -1;
        }
        addTransaction(repositories, personPair);
    }
}
