package de.cofinpro.splitter.model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * model class representing a person group. It's more or less a lexicographically ordered list of names
 * with som e convenience methods.
 */
public class Group extends ArrayList<String> {

    public Group(String... names) {
        addAll(Arrays.stream(names).sorted().toList());
    }

    @Override
    public String toString() {
        return String.join("\n", this);
    }
}
