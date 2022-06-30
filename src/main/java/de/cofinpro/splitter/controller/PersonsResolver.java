package de.cofinpro.splitter.controller;

import de.cofinpro.splitter.model.Group;
import de.cofinpro.splitter.model.Groups;

import java.util.*;
import java.util.stream.Collectors;

/**
 * class, that offers stativ methods to tokenize and resolve the complicated token syntax for group and purchase commands
 * with +/- additions and subtractions of groups or members in parentheses.
 */
public class PersonsResolver {

    private PersonsResolver() {
        // no instances
    }

    /**
     * format validates and processes the group members (incl. other groups) given into the field level array
     * group members.
     * @param args the last arguments of the command line, that should specify persons (members and groups)
     * @return false, if syntax error, true if members could be processed.
     */
    public static String[] tokenizePersonsArguments(String[] args) {
        String groupArg = String.join("", args);
        if (!groupArg.matches("\\(([+-]?\\w+,)*+[+-]?\\w+\\)")) {
            return new String[0];
        }
        groupArg = groupArg.replaceAll("[(+)]", "");
        return groupArg.split(",");
    }

    /**
     * streams all persons tokens into two groups, ones that don't start with "-", which are processed first (with flatmap)
     * to stream their group members if necessary - and the ones, that start with "-" and which are removed from the
     * result set after "flatmapping".
     * @param tokens the tokens associated to persons, previously tokenized
     * @param groups the map of all groups created so far
     * @return the collection of persons after resolving groups, additions and subtractions
     */
    public static Collection<String> resolvePersonsFromTokens(String[] tokens, Groups groups) {
        Map<Boolean, List<String>> membersGrouped = Arrays.stream(tokens)
                .collect(Collectors.groupingBy(mem -> mem.startsWith("-")));
        if (membersGrouped.get(false) == null) {
            return Collections.emptySet();
        }
        List<String> result = new ArrayList<>(membersGrouped.get(false).stream()
                .flatMap(member -> groups.getOrDefault(member, new Group(member)).stream())
                .sorted().distinct()
                .toList());
        if (membersGrouped.get(true) != null) {
            result.removeAll(membersGrouped.get(true).stream().map(mem -> mem.substring(1))
                    .flatMap(member -> groups.getOrDefault(member, new Group(member)).stream())
                    .sorted().distinct().toList());
        }
        return result;
    }
}
