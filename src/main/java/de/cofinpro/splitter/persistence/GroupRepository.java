package de.cofinpro.splitter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    default Optional<List<String>> getPersons(String token) {
        return findByName(token)
                .map(group -> group.getMembers().stream().map(Person::getName).toList());
    }

    Optional<Group> findByName(String groupName);
}