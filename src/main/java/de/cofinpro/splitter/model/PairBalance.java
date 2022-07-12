package de.cofinpro.splitter.model;

/**
 * interface for spring data jpa projection holding the result set of the native @Query group by
 * join select, that does the balances calculations.
 */
public interface PairBalance {

    String getFirstPerson();

    String getSecondPerson();

    Long getBalance();
}
