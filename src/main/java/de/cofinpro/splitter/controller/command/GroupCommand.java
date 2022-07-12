package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.controller.PersonsResolver;
import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.Repositories;
import de.cofinpro.splitter.persistence.Group;
import de.cofinpro.splitter.persistence.GroupRepository;
import de.cofinpro.splitter.persistence.Person;

import java.util.Arrays;
import java.util.Set;

import static de.cofinpro.splitter.controller.command.GroupCommand.Type.CREATE;
import static de.cofinpro.splitter.controller.command.GroupCommand.Type.SHOW;

/**
 * LineCommand implementation of "group" command to create and display (show) groups of persons.
 */
public class GroupCommand implements LineCommand {

    static final String UNKNOWN_GROUP = "Unknown group";
    static final String EMPTY_GROUP = "group is empty";

    private final ConsolePrinter printer;
    private Type type;
    private final boolean invalid;
    private String groupName;
    private String[] personsTokens;

    public GroupCommand(ConsolePrinter printer, String[] arguments) {
        this.printer = printer;
        invalid = !validClArgumentsProcessed(arguments);
    }

    /**
     * validates and processes the arguments given:
     * - not less than 2 arguments,
     * - group name (arg 1) is UPPERCASE
     * - group command type is valid
     * - if "show" (arg 0) selected, then only group name given
     * - if "create, add, remove" (arg 0) selected, then more than 2 arguments needed, where arguments from 3 on define the group members.
     * @param arguments arguments to be processed and validated
     * @return validation result.
     */
    private boolean validClArgumentsProcessed(String[] arguments) {
        if (arguments.length < 2) {
            return false;
        }
        groupName = arguments[1];
        if (!groupName.equals(groupName.toUpperCase())) {
            return false;
        }
        try {
            type = Type.valueOf(arguments[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            return false;
        }
        if (type == SHOW) {
            // show must NOT have further arguments beside the group
            return arguments.length == 2;
        }
        if (arguments.length == 2) {
            // all other commands need further argument(s)
            return false;
        }
        personsTokens = PersonsResolver.tokenizePersonsArguments(Arrays.copyOfRange(arguments, 2, arguments.length));
        return personsTokens.length != 0;
    }

    @Override
    public void execute(Repositories repositories) {
        if (invalid) {
            printer.printError(ERROR_INVALID);
            return;
        }
        switch (type) {
            case SHOW -> showGroup(repositories.getGroupRepository());
            case REMOVE -> removeFromGroup(repositories.getGroupRepository());
            default -> addToGroup(repositories);
        }
    }

    private void removeFromGroup(GroupRepository repository) {
        var groupOpt = repository.findByName(groupName);
        if (groupOpt.isEmpty()) {
            return;
        }
        Group group = groupOpt.get();
        group.removeAll(PersonsResolver.resolvePersonsFromTokens(personsTokens, repository));
        repository.save(group);
    }

    public void addToGroup(Repositories repositories) {
        GroupRepository groupRepository = repositories.getGroupRepository();
        Group group = groupRepository.findByName(groupName).orElse(new Group().setName(groupName));
        if (type == CREATE) {
            group.getMembers().clear();
        }

        for (String name: PersonsResolver.resolvePersonsFromTokens(personsTokens, groupRepository)) {
            group.addMember(repositories.getPersonRepository().findByNameOrCreate(name));
        }
        groupRepository.save(group);
    }

    /**
     * show the group asked for with the command or print error, if group not found.
     * @param repository the group repository.
     */
    private void showGroup(GroupRepository repository) {
        var groupOpt = repository.findByName(groupName);
        if (groupOpt.isEmpty()) {
            printer.printError(UNKNOWN_GROUP);
        } else {
            Set<Person> members = groupOpt.get().getMembers();
            if (members.isEmpty()) {
                printer.printInfo(EMPTY_GROUP);
            }
            members.stream().map(Person::getName).forEach(printer::printInfo);
        }
    }

    enum Type {
        SHOW, CREATE, ADD, REMOVE
    }
}