package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.controller.PersonsResolver;
import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.ExpensesModel;
import de.cofinpro.splitter.model.Group;
import de.cofinpro.splitter.model.Groups;

import java.util.*;

import static de.cofinpro.splitter.controller.command.GroupCommand.Type.*;

/**
 * LineCommand implementation of "group" command to create and display (show) groups of persons.
 */
public class GroupCommand implements LineCommand {

    private static final String UNKNOWN_GROUP = "Unknown group";

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
    public void execute(ExpensesModel expensesModel) {
        if (invalid) {
            printer.printError(ERROR_INVALID);
            return;
        }
        switch (type) {
            case SHOW -> showGroup(expensesModel.getGroups());
            case REMOVE -> removeFromGroup(expensesModel.getGroups());
            case ADD, CREATE -> addToGroup(expensesModel.getGroups());
        }
    }

    private void removeFromGroup(Groups groups) {
        Group group = groups.get(groupName);
        if (group == null) {
            return;
        }
        group.removeAll(PersonsResolver.resolvePersonsFromTokens(personsTokens, groups));
    }

    private void addToGroup(Groups groups) {
        Group group = groups.getOrDefault(groupName, Group.empty());
        group.addAll(PersonsResolver.resolvePersonsFromTokens(personsTokens, groups));
        groups.put(groupName, group);
    }

    /**
     * show the group asked for with the command or print error, if group not found.
     * @param groups the groups map bean.
     */
    private void showGroup(Groups groups) {
        Group group = groups.get(groupName);
        if (group == null) {
            printer.printError(UNKNOWN_GROUP);
        } else {
            if (group.isEmpty()) {
                printer.printInfo("group is empty");
            }
            group.forEach(printer::printInfo);
        }
    }

    enum Type {
        SHOW, CREATE, ADD, REMOVE
    }
}