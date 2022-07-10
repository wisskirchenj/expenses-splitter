package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.ExpensesModel;
import de.cofinpro.splitter.model.Group;
import de.cofinpro.splitter.model.Groups;
import de.cofinpro.splitter.model.Transactions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SecretSantaCommandTest {

    @Spy
    ConsolePrinter printer;

    ExpensesModel expensesModel;

    @Captor
    ArgumentCaptor<String> printCaptor;

    @BeforeEach
    void setUp() {
        Groups groups = new Groups();
        groups.put("SIX", new Group("Peter", "Anton", "Berta", "Willi", "Ina", "Ulrich"));
        groups.put("PAIR", new Group("Ulrich", "Manni"));
        groups.put("SINGLE", new Group("Ulrich"));
        groups.put("EMPTY", new Group());
        expensesModel = new ExpensesModel(new Transactions(), groups);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "lowercase",
            "two params",
            "MIxED"
    })
    void whenInvalidArguments_thenErrorInvalid(String arg) {
        String[] args = arg.split(" ");
        var command = new SecretSantaCommand(printer, args);
        command.execute(expensesModel);
        verify(printer).printError(LineCommand.ERROR_INVALID);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SINGLE",
            "EMPTY"
    })
    void whenTooSmallGroup_thenErrorGroupTooSmall(String arg) {
        String[] args = arg.split(" ");
        var command = new SecretSantaCommand(printer, args);
        command.execute(expensesModel);
        verify(printer).printError("group too small for santa gifts");
    }

    @Test
    void whenGroupOfTwo_thenMutualGiftsAllowed() {
        var command = new SecretSantaCommand(printer, new String[] {"PAIR"});
        command.execute(expensesModel);
        verify(printer).printInfo("Manni gift to Ulrich");
        verify(printer).printInfo("Ulrich gift to Manni");
    }

    @RepeatedTest(5)
    void whenGroupOfSix_thenAllAssignmentsValid() {
        var command = new SecretSantaCommand(printer, new String[] {"SIX"});
        command.execute(expensesModel);
        verify(printer, times(6)).printInfo(printCaptor.capture());
        String receivers = printCaptor.getAllValues().stream().map(line -> line.substring(line.lastIndexOf(' ')))
                .collect(Collectors.joining());
        String gifters = printCaptor.getAllValues().stream()
                .map(line -> line.substring(0, line.indexOf(' ')))
                .collect(Collectors.joining());
        for (String person: expensesModel.getGroups().get("SIX")) {
            assertTrue(gifters.contains(person));
            assertTrue(receivers.contains(person));
        }
    }
}