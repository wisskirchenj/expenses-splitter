package de.cofinpro.splitter.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * entity for storing a transaction between two persons on a date for an amount, which may be negative.
 * The lexicographically less person is always stored as first and the other as second to ease work with balances.
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    private Person first;

    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    private Person second;

    @NotNull
    private LocalDate date;

    @NotNull
    private long amount;
}
