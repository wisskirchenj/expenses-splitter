package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.Repositories;
import de.cofinpro.splitter.model.Transactions;
import de.cofinpro.splitter.persistence.GroupRepository;
import de.cofinpro.splitter.persistence.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GroupMembersCommandTest {

    @Mock
    ConsolePrinter printer;

    @Spy
    GroupRepository groupRepository;

    @Spy
    PersonRepository personRepository;

    Repositories repositories;


    @BeforeEach
    void setup() {
        repositories = new Repositories(new Transactions(), groupRepository, personRepository);
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
        groupCommand.execute(repositories);
        verify(printer).printError(LineCommand.ERROR_INVALID);
    }

    @Test
    void whenValidNonExistentGroup_showErrorUnknown() {
        String[] args = "show GROUP".split(" ");
        GroupCommand groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(repositories);
        verify(printer).printError("Unknown group");
    }

   /* @Test
    void whenExistentGroup_showWorks() {
        repositories.getGroups().put("GROUP", new GroupMembers("Peter", "Mary"));
        String[] args = "show GROUP".split(" ");
        GroupCommand groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(repositories);
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
        groupCommand.execute(repositories);
        verify(printer, never()).printError(anyString());
        GroupMembers newGroup = repositories.getGroups().get("GROUP");
        assertNotNull(newGroup);
        assertEquals(1, repositories.getGroups().size());
        assertEquals(new GroupMembers("Franz", "Sabine", "Hans"), newGroup);
    }

    @Test
    void whenAddMembers_MembersAdded() {
        String[] args = "create GROUP (Hans, Franz, Sabine)".split(" ");
        GroupCommand groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(repositories);
        args = "add GROUP (-Anton, Eugen, +Ina)".split(" ");
        groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(repositories);
        GroupMembers addedGroup = repositories.getGroups().get("GROUP");
        assertEquals(5, addedGroup.size());
        assertEquals(new GroupMembers("Franz", "Sabine", "Hans", "Eugen", "Ina"), addedGroup);
    }

    @Test
    void whenGroupAdd_MembersAdded() {
        String[] args = "create GROUP (Hans, Franz, Sabine)".split(" ");
        GroupCommand groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(repositories);
        args = "create GIRLS (Anna, Laura, Tina)".split(" ");
        groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(repositories);
        args = "add GROUP (-Anna, GIRLS, +Ina)".split(" ");
        groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(repositories);
        GroupMembers addedGroup = repositories.getGroups().get("GROUP");
        assertEquals(6, addedGroup.size());
        assertEquals(new GroupMembers("Franz", "Sabine", "Hans", "Laura", "Ina", "Tina"), addedGroup);
    }

    @Test
    void whenGroupRemove_MembersRemoved() {
        String[] args = "create GROUP (Hans, Franz, Sabine)".split(" ");
        GroupCommand groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(repositories);
        args = "create GIRLS (Anna, Laura, Tina)".split(" ");
        groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(repositories);
        args = "add GROUP (-Anna, GIRLS)".split(" ");
        groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(repositories);
        assertEquals(5, repositories.getGroups().get("GROUP").size());
        args = "remove GROUP (-Tina, GIRLS)".split(" ");
        groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(repositories);
        assertEquals(4, repositories.getGroups().get("GROUP").size());
        assertEquals(new GroupMembers("Franz", "Sabine", "Hans", "Tina"), repositories.getGroups().get("GROUP"));
    }*/
}