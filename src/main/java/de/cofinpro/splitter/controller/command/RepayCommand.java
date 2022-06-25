package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.MoneyTransfer;
import de.cofinpro.splitter.model.PersonPair;
import de.cofinpro.splitter.model.Transactions;

import java.time.LocalDate;

public class RepayCommand extends PayCommand {

    public RepayCommand(ConsolePrinter printer, LocalDate date, String[] arguments) {
        super(printer, date, arguments);
    }

    @Override
    public void executeMoneyTransfer(Transactions transactions) {
        PersonPair personPair = new PersonPair(from, to);
        // repay => the from person gets a negative balance attribution. So if from is 1st in pair, we need to switch amount to negative
        if (personPair.getFirst().equals(from)) {
            amount *= -1;
        }
        transactions.addTransaction(personPair, new MoneyTransfer(date, amount));
    }
}