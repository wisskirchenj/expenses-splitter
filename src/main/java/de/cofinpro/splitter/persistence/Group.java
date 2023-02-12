package de.cofinpro.splitter.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
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
