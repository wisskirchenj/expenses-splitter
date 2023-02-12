package de.cofinpro.splitter.persistence;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * entity for storing a group with name and members (Person instances in ManyToMany relationship).
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "`GROUP`")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "GROUP_MEMBERS",
            joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderBy("name ASC")
    private Set<Person> members = new LinkedHashSet<>();

    public void addMember(Person person) {
        members.add(person);
    }

    public void removeAll(Collection<String> names) {
        names.forEach(personName -> members.remove(new Person(personName)));
    }
}
