package de.cofinpro.splitter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByName(String name);

    default Person findByNameOrCreate(String name) {
        return findByName(name).orElseGet(() -> save(new Person(name)));
    }
}
