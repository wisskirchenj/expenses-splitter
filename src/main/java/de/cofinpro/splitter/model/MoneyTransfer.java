package de.cofinpro.splitter.model;

import lombok.Value;

import java.time.LocalDate;

/**
 * immutable DTO that represents a money transfer between a person pair at a given date. Positive amount
 * relates to first person of PersonPair received the money.
 */
@Value
public class MoneyTransfer {
    LocalDate date;
    int amount;
}
