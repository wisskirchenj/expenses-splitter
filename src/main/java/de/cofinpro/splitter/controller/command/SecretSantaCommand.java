package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.ExpensesModel;
import de.cofinpro.splitter.model.Group;
import de.cofinpro.splitter.model.Groups;
import de.cofinpro.splitter.model.PersonPair;

import java.util.*;

import static de.cofinpro.splitter.controller.command.GroupCommand.UNKNOWN_GROUP;

/**
 * LineCommand implementation of the "secretSanta" command, that shuffles a random
 * git assignment avoiding slef- and mutual assignments
 */
public class SecretSantaCommand implements LineCommand {

    private static final Random RANDOM = new Random();
    private final ConsolePrinter printer;
    private String groupName;
    private final boolean invalid;

    public SecretSantaCommand(ConsolePrinter printer, String[] arguments) {
        this.printer = printer;
        invalid = !validClArgumentsProcessed(arguments);
        if (!invalid) {
            this.groupName = arguments[0];
        }
    }

    private boolean validClArgumentsProcessed(String[] arguments) {
        return arguments.length == 1 && arguments[0].equals(arguments[0].toUpperCase());
    }

    @Override
    public void execute(ExpensesModel expensesModel) {
        if (invalid) {
            printer.printError(ERROR_INVALID);
            return;
        }
        executeGiftAssignment(expensesModel.getGroups());
    }

    private void executeGiftAssignment(Groups groups) {
        Group group = groups.get(groupName);
        if (group == null) {
            printer.printError(UNKNOWN_GROUP);
        } else if (group.size() <= 1) {
            printer.printError("group too small for santa gifts");
        } else if (group.size() == 2) {
            mutualGifts(group);
        } else {
            shuffleGiftPairs(group);
        }
    }


    private void shuffleGiftPairs(Group group) {
        final Map<String, String> giftAssignments = new HashMap<>();
        do {
            giftAssignments.clear();
            tryShuffle(giftAssignments, group);
        } while (giftAssignmentNotValid(giftAssignments, group));
        group.forEach(person -> printGiftToLine(person, giftAssignments.get(person)));
    }

    private boolean giftAssignmentNotValid(Map<String, String> assignments, Group group) {
        for (String person: group) {
            String receiver = assignments.get(person);
            if (person.equals(receiver) || person.equals(assignments.get(receiver))) {
                return true;
            }
        }
        return false;
    }

    private void tryShuffle(Map<String, String> assignments, Group group) {
        List<String> receivers = new ArrayList<>(group);
        for (String person: group) {
            int remainingReceivers = receivers.size();
            String receiver = receivers.get(RANDOM.nextInt(remainingReceivers));
            assignments.put(person, receiver);
            receivers.remove(receiver);
        }
    }

    private void mutualGifts(Group group) {
        PersonPair pair = new PersonPair(group.first(), group.last());
        printGiftToLine(pair.getFirst(), pair.getSecond());
        printGiftToLine(pair.getSecond(), pair.getFirst());
    }

    private void printGiftToLine(String from, String to) {
        printer.printInfo("%s gift to %s".formatted(from, to));
    }
}
