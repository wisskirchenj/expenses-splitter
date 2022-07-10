package de.cofinpro.splitter.model;

import de.cofinpro.splitter.persistence.GroupRepository;
import de.cofinpro.splitter.persistence.PersonRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * container bean that represents the overall data model gathered throughout the application run,
 * i.e. created groups and transactions, which are in turn autowired.
 */
@Component
@Data
public class Repositories {

    private final Transactions transactions;
    private final GroupRepository groupRepository;
    private final PersonRepository personRepository;

    @Autowired
    public Repositories(Transactions transactions, GroupRepository groupRepository, PersonRepository personRepository) {
        this.transactions = transactions;
        this.groupRepository = groupRepository;
        this.personRepository = personRepository;
    }
}
