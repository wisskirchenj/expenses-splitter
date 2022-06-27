package de.cofinpro.splitter.model;

import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * container bean, that stores the groups with their names in a map.
 */
@Component
public class Groups extends HashMap<String, Group> {
}
