package de.cofinpro.splitter.model;

import lombok.Value;

/**
 * immutable objects, that represent a pair of persons, e.g. between which transactions are possible.
 * The lexicographically lower name is stored as first, the other as second.
 */
@Value
public class PersonPair {
    String first;
    String second;

    public PersonPair(String aName, String otherName) {
        this.first = aName.compareTo(otherName) < 0 ? aName : otherName;
        this.second = aName.compareTo(otherName) > 0 ? aName : otherName;
    }
}
