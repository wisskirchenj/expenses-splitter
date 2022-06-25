package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.MoneyTransfer;
import de.cofinpro.splitter.model.PersonPair;
import de.cofinpro.splitter.model.Transactions;

import java.time.LocalDate;

public class BorrowCommand extends PayCommand {

    public BorrowCommand(ConsolePrinter printer, LocalDate date, String[] arguments) {
        super(printer, date, arguments);
    }

    @Override
    public void executeMoneyTransfer(Transactions transactions) {
        PersonPair personPair = new PersonPair(from, to);
        // borrow => the from person gets a positive balance attribution. So if from is 2nd in pair, we need to switch amount to negative
        if (personPair.getSecond().equals(from)) {
            amount *= -1;
        }
        transactions.addTransaction(personPair, new MoneyTransfer(date, amount));
    }
}
