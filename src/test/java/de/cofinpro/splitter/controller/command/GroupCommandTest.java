package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.Repositories;
import de.cofinpro.splitter.persistence.Group;
import de.cofinpro.splitter.persistence.GroupRepository;
import de.cofinpro.splitter.persistence.Person;
import de.cofinpro.splitter.persistence.PersonRepository;
import de.cofinpro.splitter.persistence.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
class GroupCommandTest {

    @Mock
    ConsolePrinter printer;

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    GroupRepository groupRepository;

    Repositories repositories;


    @BeforeEach
    void setup() {
        repositories = new Repositories(transactionRepository, groupRepository, personRepository);
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

    @Test
    void whenExistentGroup_showWorks() {
        Group group = new Group().setName("GROUP")
                .setMembers(Set.of(new Person("Peter"), new Person("Mary")));
        repositories.getGroupRepository().save(group);
        String[] args = "show GROUP".split(" ");
        GroupCommand groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(repositories);
        verify(printer, never()).printError(anyString());
        verify(printer, times(2)).printInfo(anyString());
        verify(printer).printInfo("Mary");
        verify(printer).printInfo("Peter");
        repositories.getGroupRepository().delete(group);
    }

    @Test
    void whenCreateValid_GroupAdded() {
        String[] args = "create GROUP (Hans, Franz, Sabine)".split(" ");
        GroupCommand groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(repositories);
        verify(printer, never()).printError(anyString());
        Group newGroup = repositories.getGroupRepository()
                .findByName("GROUP").orElseThrow();
        assertNotNull(newGroup);
        assertEquals(1L, repositories.getGroupRepository().count());
        assertEquals(Set.of("Franz", "Sabine", "Hans"),
                newGroup.getMembers().stream().map(Person::getName).collect(Collectors.toSet()));
    }

    @Test
    void whenAddMembers_MembersAdded() {
        String[] args = "create GROUP (Hans, Franz, Sabine)".split(" ");
        GroupCommand groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(repositories);
        args = "add GROUP (-Anton, Eugen, +Ina)".split(" ");
        groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(repositories);
        Group addedGroup = repositories.getGroupRepository()
                .findByName("GROUP").orElseThrow();
        assertEquals(5, addedGroup.getMembers().size());
        assertEquals(Set.of("Franz", "Sabine", "Hans", "Eugen", "Ina"),
                addedGroup.getMembers().stream().map(Person::getName).collect(Collectors.toSet()));
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
        Group addedGroup = repositories.getGroupRepository()
                .findByName("GROUP").orElseThrow();
        assertEquals(6, addedGroup.getMembers().size());
        assertEquals(Set.of("Franz", "Sabine", "Hans", "Laura", "Ina", "Tina"),
                addedGroup.getMembers().stream().map(Person::getName).collect(Collectors.toSet()));
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
        assertEquals(5, repositories.getGroupRepository()
                .findByName("GROUP").orElseThrow().getMembers().size());
        args = "remove GROUP (-Tina, GIRLS)".split(" ");
        groupCommand = new GroupCommand(printer, args);
        groupCommand.execute(repositories);
        assertEquals(4, repositories.getGroupRepository()
                .findByName("GROUP").orElseThrow().getMembers().size());
        assertEquals(Set.of("Franz", "Sabine", "Hans", "Tina"),
                repositories.getGroupRepository().findByName("GROUP").orElseThrow()
                        .getMembers().stream().map(Person::getName).collect(Collectors.toSet()));
    }
}