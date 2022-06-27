package de.cofinpro.splitter.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GroupTest {

    Group group;

    @BeforeEach
    void setUp() {
        group = new Group("Anton", "Klaus", "Berta", "Anne");
    }

    @Test
    void testToString() {
        assertEquals("Anne\nAnton\nBerta\nKlaus", group.toString());
    }
}