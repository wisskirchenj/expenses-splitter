package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.ExpensesModel;
import de.cofinpro.splitter.model.Group;
import de.cofinpro.splitter.model.Groups;

import java.util.Arrays;

/**
 * LineCommand implementation of "group" command to create and display (show) groups of persons.
 */
public class GroupCommand implements LineCommand {

    private static final String UNKNOWN_GROUP = "Unknown group";

    private final ConsolePrinter printer;
    private boolean isCreate = false;
    private final boolean invalid;
    private String groupName;
    private String[] groupMembers;

    public GroupCommand(ConsolePrinter printer, String[] arguments) {
        this.printer = printer;
        invalid = !validClArgumentsProcessed(arguments);
    }

    /**
     * validates and processes the arguments given:
     * - not less than 2 arguments,
     * - groupname (arg 1) is UPPERCASE
     * - if "show" (arg 0) selected, then only groupname given
     * - if "create" (arg 0) selected, then more than 2 arguments needed, where arguments from 3 on define the group members.
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
        if (arguments[0].equalsIgnoreCase("show") && arguments.length == 2) {
            return true;
        }
        if (!arguments[0].equalsIgnoreCase("create") || arguments.length == 2) {
            return false;
        }
        isCreate = true;
        return validGroupMembersProcessed(Arrays.copyOfRange(arguments, 2, arguments.length));
    }

    /**
     * format validates and processes the group members given into the field level array group members.
     * @param memberArgs the last arguments of the command line, that should specify the group members
     * @return false, if syntax error, true if members could be processed.
     */
    private boolean validGroupMembersProcessed(String[] memberArgs) {
        String groupArg = String.join("", memberArgs);
        if (!groupArg.matches("\\((\\w+,)*+\\w+\\)")) {
            return false;
        }
        groupArg = groupArg.replaceAll("[()]", "");
        groupMembers = groupArg.split(",");
        return true;
    }

    @Override
    public void execute(ExpensesModel expensesModel) {
        if (invalid) {
            printer.printError(ERROR_INVALID);
            return;
        }
        if (isCreate) {
            createGroup(expensesModel.getGroups());
        } else {
            showGroup(expensesModel.getGroups());
        }
    }

    private void createGroup(Groups groups) {
        groups.put(groupName, new Group(groupMembers));
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
            group.forEach(printer::printInfo);
        }
    }
}
