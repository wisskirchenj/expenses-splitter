package de.cofinpro.splitter;

import de.cofinpro.splitter.controller.CommandLineInterpreter;
import de.cofinpro.splitter.controller.SplitterCommandLineRunner;
import de.cofinpro.splitter.controller.command.CommandType;
import de.cofinpro.splitter.controller.command.LineCommand;
import de.cofinpro.splitter.io.CommandLineConfiguration;
import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.ExpensesModel;
import de.cofinpro.splitter.model.Groups;
import de.cofinpro.splitter.model.Transactions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SplitterCommandLineRunnerIT {

    @Mock
    Scanner scanner;

    @Spy
    ConsolePrinter printer;

    @Captor
    ArgumentCaptor<String> printCaptor;

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
        inOrder.verify(printer).printInfo("cashBack");
        inOrder.verify(printer).printInfo("exit");
        inOrder.verify(printer).printInfo("group");
        inOrder.verify(printer).printInfo("help");
        inOrder.verify(printer).printInfo("purchase");
        inOrder.verify(printer).printInfo("repay");
        inOrder.verify(printer).printInfo("secretSanta");
        inOrder.verify(printer).printInfo("writeOff");
        verify(printer, times(CommandType.values().length)).printInfo(anyString());
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
        inOrder.verify(printer).printOwes(List.of("A owes B 20.00"));
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
        inOrder.verify(printer).printOwes(List.of("Ann owes Bob 20.00"));
        inOrder.verify(printer).printOwes(List.of("Bob owes Ann 5.00"));
        inOrder.verify(printer).printOwes(List.of());
        verify(printer, times(2)).printInfo("No repayments");
    }

    @Test
    void exampleGroups_Stage2() {
        when(scanner.nextLine()).thenReturn("group create lowerCaseText",
                "group show NOTCREATEDGROUP",
                "group create BOYS (Elon, Bob, Chuck)",
                "group show BOYS",
                "exit");
        splitterCommandLineRunner.run();
        InOrder inOrder = Mockito.inOrder(printer);
        inOrder.verify(printer).printError(LineCommand.ERROR_INVALID);
        inOrder.verify(printer).printError("Unknown group");
        inOrder.verify(printer).printInfo("Bob");
        inOrder.verify(printer).printInfo("Chuck");
        inOrder.verify(printer).printInfo("Elon");
        verify(printer, times(3)).printInfo(anyString());
    }

    @Test
    void exampleSimpleSplit_Stage2() {
        when(scanner.nextLine()).thenReturn("group create COFFEETEAM (Ann, Bob)",
                "purchase Bob coffee 10 (COFFEETEAM)",
                "balance close",
                "repay Ann Bob 5.00",
                "balance close",
                "exit");
        splitterCommandLineRunner.run();
        InOrder inOrder = Mockito.inOrder(printer);
        inOrder.verify(printer).printOwes(List.of("Ann owes Bob 5.00"));
        inOrder.verify(printer).printInfo("No repayments");
        verify(printer, times(2)).printInfo(anyString());
    }

    @Test
    void exampleFriendsSplit_Stage2() {
        when(scanner.nextLine()).thenReturn("group create FRIENDS (Ann, Bob, Chuck)",
                "purchase Elon chocolate 12.50 (FRIENDS)",
                "balance close",
                "exit");
        splitterCommandLineRunner.run();
        verify(printer).printOwes(List.of("Ann owes Elon 4.17",
                "Bob owes Elon 4.17",
                "Chuck owes Elon 4.16"));
        verify(printer, never()).printError(anyString());
        verify(printer, times(3)).printInfo(anyString());
    }

    @Test
    void exampleFriendsSplitModified_Stage2() {
        when(scanner.nextLine()).thenReturn("group create FRIENDS (Ann, Bob, Chuck)",
                "purchase Bob chocolate 12.50 (FRIENDS)",
                "balance close",
                "exit");
        splitterCommandLineRunner.run();
        verify(printer).printOwes(List.of("Ann owes Bob 4.17",
                "Chuck owes Bob 4.16"));
        verify(printer, never()).printError(anyString());
        verify(printer, times(2)).printInfo(anyString());
    }

    @Test
    void exampleGroupPurchases_Stage2() {
        when(scanner.nextLine()).thenReturn("group create BOYS (Elon, Bob, Chuck)",
                "group create GIRLS (Ann, Diana)",
                "2020.10.20 purchase Diana flowers 15.65 (BOYS)",
                "2020.10.21 purchase Chuck pizza 6.30 (BOYS)",
                "2020.10.22 purchase Bob icecream 3.99 (GIRLS)",
                "balance close",
                "exit");
        splitterCommandLineRunner.run();
        verify(printer).printOwes(List.of("Ann owes Bob 2.00",
                "Bob owes Chuck 2.10",
                "Bob owes Diana 3.23",
                "Chuck owes Diana 5.22",
                "Elon owes Chuck 2.10",
                "Elon owes Diana 5.21"));
        verify(printer, never()).printError(anyString());
        verify(printer, times(6)).printInfo(anyString());
    }

    @Test
    void exampleTest_Stage3() {
        when(scanner.nextLine()).thenReturn("group create SOMEGROUP (Bob)",
                "group create GIRLS (Ann, Diana, Ann)",
                "group create BOYS (Bob, Chuck, Elon)",
                "group add SOMEGROUP (GIRLS, Frank)",
                "group remove SOMEGROUP (-BOYS, Bob, +Frank)",
                "group show SOMEGROUP",
                "exit");
        splitterCommandLineRunner.run();
        InOrder inOrder = Mockito.inOrder(printer);
        inOrder.verify(printer).printInfo("Ann");
        inOrder.verify(printer).printInfo("Bob");
        inOrder.verify(printer).printInfo("Diana");
        verify(printer, times(3)).printInfo(anyString());
    }

    @Test
    void exampleEmptyTest_Stage3() {
        when(scanner.nextLine()).thenReturn("group add SOMEGROUP (Bob)",
                "group remove SOMEGROUP (Bob)",
                "group show SOMEGROUP",
                "exit");
        splitterCommandLineRunner.run();
        verify(printer).printInfo("group is empty");
    }

    @Test
    void examplePurchaseTest_Stage3() {
        when(scanner.nextLine()).thenReturn("group create GIRLS (Ann, Diana)",
                "group create TEAM (+Bob, GIRLS, -Frank, Chuck)",
                "2020.10.20 purchase Diana flowers 15.65 (TEAM, Elon, -GIRLS)",
                "2020.10.21 purchase Elon ChuckBirthdayGift 20.99 (TEAM, -Chuck)",
                "balance close",
                "exit");
        splitterCommandLineRunner.run();
        verify(printer).printOwes(List.of("Ann owes Elon 7.00",
                "Bob owes Diana 5.22",
                "Bob owes Elon 7.00",
                "Chuck owes Diana 5.22",
                "Diana owes Elon 1.78"));
        verify(printer, never()).printError(anyString());
        verify(printer, times(5)).printInfo(anyString());
    }


    @Test
    void exampleEmptyPurchase_PrintsGroupEmpty() {
        when(scanner.nextLine()).thenReturn("group add SOMEGROUP (Bob, Franz)",
                "group remove SOMEGROUP (+Franz)",
                "2020.12.12 purchase Heini Lolly 20.34 (-Bob, +SOMEGROUP)",
                "exit");
        splitterCommandLineRunner.run();
        verify(printer).printError("group is empty");
    }

    @Test
    void exampleSecretSanta_Stage4() {
        when(scanner.nextLine()).thenReturn("group create SOMESANTAGROUP (Gordon, Bob, Ann, Chuck, Elon, Diana, Foxy)",
                "secretSanta SOMESANTAGROUP",
                "exit");
        splitterCommandLineRunner.run();
        verify(printer, times(7)).printInfo(printCaptor.capture());
        String receivers = printCaptor.getAllValues().stream().map(line -> line.substring(line.lastIndexOf(' ')))
                .collect(Collectors.joining());
        String gifters = printCaptor.getAllValues().stream()
                .map(line -> line.substring(0, line.indexOf(' ')))
                .collect(Collectors.joining());
        for (String person: new String[] {"Gordon", "Bob", "Ann", "Chuck", "Elon", "Diana", "Foxy"}) {
            assertTrue(gifters.contains(person));
            assertTrue(receivers.contains(person));
        }
    }

    @Test
    void exampleWriteOff_Stage4() {
        when(scanner.nextLine()).thenReturn("borrow Ann Bob 1.00",
                "3030.03.30 purchase Bob coffee 3.50 (Bob, Ann)",
                "writeOff",
                "balance close",
                "3030.03.30 balance close",
                "3030.03.30 writeOff",
                "3030.03.30 balance close",
                "exit");
        splitterCommandLineRunner.run();
        verify(printer, times(3)).printInfo(printCaptor.capture());
        InOrder inOrder = Mockito.inOrder(printer);
        inOrder.verify(printer).printInfo("No repayments");
        inOrder.verify(printer).printInfo("Ann owes Bob 1.75");
        inOrder.verify(printer).printInfo("No repayments");
    }
}