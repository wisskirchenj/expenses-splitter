package de.cofinpro.splitter.controller;

import de.cofinpro.splitter.persistence.GroupRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
     * @return empty array, if syntax error or the persons resolved.
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
     * @param repository the group repository
     * @return the collection of persons after resolving groups, additions and subtractions
     */
    public static Collection<String> resolvePersonsFromTokens(String[] tokens, GroupRepository repository) {
        Map<Boolean, List<String>> tokensGrouped = Arrays.stream(tokens)
                .collect(Collectors.partitioningBy(mem -> mem.startsWith("-")));
        if (tokensGrouped.get(false) == null) {
            return Collections.emptySet();
        }
        List<String> result = new ArrayList<>(tokensGrouped.get(false).stream()
                .flatMap(token -> repository.getPersons(token).orElse(List.of(token)).stream())
                .sorted().distinct()
                .toList());
        if (tokensGrouped.get(true) != null) {
            result.removeAll(tokensGrouped.get(true).stream().map(mem -> mem.substring(1))
                    .flatMap(token -> repository.getPersons(token).orElse(List.of(token)).stream())
                    .sorted().distinct().toList());
        }
        return result;
    }

}