package de.cofinpro.splitter.controller;

import de.cofinpro.splitter.io.CommandLineConfiguration;
import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.ExpensesModel;
import de.cofinpro.splitter.model.Groups;
import de.cofinpro.splitter.model.Transactions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Scanner;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SplitterCommandLineRunnerIT {

    @Mock
    Scanner scanner;

    @Spy
    ConsolePrinter printer;

    SplitterCommandLineRunner splitterCommandLineRunner;

    @BeforeEach
    void setUp() {
        splitterCommandLineRunner = new SplitterCommandLineRunner(
                new CommandLineInterpreter(scanner, printer,
                        new CommandLineConfiguration().getDateFormatter()),
                new ExpensesModel(new Transactions(), new Groups())
        );
    }

    @Test
    void whenHelp_AllCommandsAreGivenOrdered() {
        when(scanner.nextLine()).thenReturn("help", "exit");
        splitterCommandLineRunner.run();
        InOrder inOrder = Mockito.inOrder(printer);
        inOrder.verify(printer).printInfo("balance");
        inOrder.verify(printer).printInfo("borrow");
        inOrder.verify(printer).printInfo("exit");
        inOrder.verify(printer).printInfo("help");
        inOrder.verify(printer).printInfo("repay");
    }

    @Test
    void whenBorrowWithDate_DateAppliedToTransaction() {
        when(scanner.nextLine()).thenReturn("2022.05.05 borrow A B 20",
                "2022.05.04 balance",
                "2022.05.05 balance",
                "exit");
        splitterCommandLineRunner.run();
        InOrder inOrder = Mockito.inOrder(printer);
        inOrder.verify(printer).printOwes(List.of());
        inOrder.verify(printer).printOwes(List.of("A owes B 20"));
        verify(printer).printInfo("No repayments");
    }


    @Test
    void example_Stage1() {
        when(scanner.nextLine()).thenReturn("2020.09.30 borrow Ann Bob 20",
                "2020.10.01 repay Ann Bob 10",
                "2020.10.10 borrow Bob Ann 7",
                "2020.10.15 repay Ann Bob 8",
                "repay Bob Ann 5",
                "2020.09.25 balance",
                "2020.10.30 balance open",
                "2020.10.20 balance close",
                "balance close",
                "exit");
        splitterCommandLineRunner.run();
        InOrder inOrder = Mockito.inOrder(printer);
        inOrder.verify(printer).printOwes(List.of());
        inOrder.verify(printer).printOwes(List.of("Ann owes Bob 20"));
        inOrder.verify(printer).printOwes(List.of("Bob owes Ann 5"));
        inOrder.verify(printer).printOwes(List.of());
        verify(printer, times(2)).printInfo("No repayments");
    }
}