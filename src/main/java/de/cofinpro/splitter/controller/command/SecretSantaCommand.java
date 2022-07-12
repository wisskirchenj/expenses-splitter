package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.controller.PersonsResolver;
import de.cofinpro.splitter.io.ConsolePrinter;
import de.cofinpro.splitter.model.PersonPair;
import de.cofinpro.splitter.model.Repositories;
import de.cofinpro.splitter.persistence.GroupRepository;

import java.util.*;

import static de.cofinpro.splitter.controller.command.GroupCommand.UNKNOWN_GROUP;

/**
 * LineCommand implementation of the "secretSanta" command, that shuffles a random
 * gift assignment avoiding slef- and mutual assignments
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
    public void execute(Repositories repositories) {
        if (invalid) {
            printer.printError(ERROR_INVALID);
            return;
        }
        executeGiftAssignment(repositories.getGroupRepository());
    }

    private void executeGiftAssignment(GroupRepository repository) {
        var groupOpt = repository.findByName(groupName);
        if (groupOpt.isEmpty()) {
            printer.printError(UNKNOWN_GROUP);
            return;
        }
        Collection<String> members = PersonsResolver
                .resolvePersonsFromTokens(new String[] {groupName}, repository);
        if (members.size() <= 1) {
            printer.printError("group too small for santa gifts");
        } else if (members.size() == 2) {
            mutualGifts(members);
        } else {
            shuffleGiftPairs(members);
        }
    }


    private void shuffleGiftPairs(Collection<String> group) {
        final Map<String, String> giftAssignments = new HashMap<>();
        do {
            giftAssignments.clear();
            tryShuffle(giftAssignments, group);
        } while (giftAssignmentNotValid(giftAssignments, group));
        group.forEach(person -> printGiftToLine(person, giftAssignments.get(person)));
    }

    private boolean giftAssignmentNotValid(Map<String, String> assignments, Collection<String> group) {
        for (String person: group) {
            String receiver = assignments.get(person);
            if (person.equals(receiver) || person.equals(assignments.get(receiver))) {
                return true;
            }
        }
        return false;
    }

    private void tryShuffle(Map<String, String> assignments, Collection<String> group) {
        List<String> receivers = new ArrayList<>(group);
        for (String person: group) {
            int remainingReceivers = receivers.size();
            String receiver = receivers.get(RANDOM.nextInt(remainingReceivers));
            assignments.put(person, receiver);
            receivers.remove(receiver);
        }
    }

    private void mutualGifts(Collection<String> group) {
        String[] persons = group.toArray(new String[0]);
        PersonPair pair = new PersonPair(persons[0], persons[1]);
        printGiftToLine(pair.getFirst(), pair.getSecond());
        printGiftToLine(pair.getSecond(), pair.getFirst());
    }

    private void printGiftToLine(String from, String to) {
        printer.printInfo("%s gift to %s".formatted(from, to));
    }
}
