package de.cofinpro.splitter.model;

import de.cofinpro.splitter.persistence.GroupRepository;
import de.cofinpro.splitter.persistence.PersonRepository;
import de.cofinpro.splitter.persistence.TransactionRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * container bean that gathers all the entity repositories,
 * i.e. for created persons, groups and transactions, which are in turn autowired.
 * The bean is passed to the actual commands for accessing the database.
 */
@Component
@Data
public class Repositories {

    private final TransactionRepository transactionRepository;
    private final GroupRepository groupRepository;
    private final PersonRepository personRepository;

    @Autowired
    public Repositories(TransactionRepository transactionRepository, GroupRepository groupRepository,
                        PersonRepository personRepository) {
        this.transactionRepository = transactionRepository;
        this.groupRepository = groupRepository;
        this.personRepository = personRepository;
    }
}
