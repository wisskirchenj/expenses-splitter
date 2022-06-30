package de.cofinpro.splitter.model;

import java.util.Arrays;
import java.util.TreeSet;

/**
 * model class representing a person group. It's more or less a lexicographically ordered set of names
 * with some convenience methods.
 */
public class Group extends TreeSet<String> {

    public Group(String... names) {
        addAll(Arrays.stream(names).sorted().toList());
    }

    public static Group empty() {
        return new Group();
    }

    @Override
    public String toString() {
        return String.join("\n", this);
    }
}
