package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.ExpensesModel;
import de.cofinpro.splitter.model.Group;
import de.cofinpro.splitter.model.Groups;
import de.cofinpro.splitter.model.Transactions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupCommandTest {

    @Mock
    ConsolePrinter printer;

    ExpensesModel expensesModel;


    @BeforeEach
    void setup() {
        expensesModel = new ExpensesModel(new Transactions(), new Groups());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "create group (Anne, Peter)",
            "",
            "create",
            "create GROUP",
            "create GROUP Peter, Anne)",
            "creat GROUP (Peter, Anne)",
            "show GROUP (Peter, Anne)",
            "show group",
            "show"
    })
    void whenInvalidArguments_thenErrorInvalid(String arg) {
        String[] args = arg.split(" ");
        GroupCommand groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(expensesModel);
        verify(printer).printError(LineCommand.ERROR_INVALID);
    }

    @Test
    void whenValidNonExistentGroup_showErrorUnknown() {
        String[] args = "show GROUP".split(" ");
        GroupCommand groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(expensesModel);
        verify(printer).printError("Unknown group");
    }

    @Test
    void whenExistentGroup_showWorks() {
        expensesModel.getGroups().put("GROUP", new Group("Peter", "Mary"));
        String[] args = "show GROUP".split(" ");
        GroupCommand groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(expensesModel);
        verify(printer, never()).printError(anyString());
        verify(printer, times(2)).printInfo(anyString());
        InOrder inOrder = inOrder(printer);
        inOrder.verify(printer).printInfo("Mary");
        inOrder.verify(printer).printInfo("Peter");
    }

    @Test
    void whenCreateValid_GroupAdded() {
        String[] args = "create GROUP (Hans, Franz, Sabine)".split(" ");
        GroupCommand groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(expensesModel);
        verify(printer, never()).printError(anyString());
        Group newGroup = expensesModel.getGroups().get("GROUP");
        assertNotNull(newGroup);
        assertEquals(1, expensesModel.getGroups().size());
        assertEquals(new Group("Franz", "Sabine", "Hans"), newGroup);
    }
}