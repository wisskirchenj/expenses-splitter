package de.cofinpro.splitter.model;

/**
 * own implementation of the interface used as JPA projection in the native @Query in order to work
 * and stream small immutable classes in the balance optimization.
 * @param first name of the first person in the pair balance
 * @param second name of the second person in the pair balance
 * @param balance the balance amount between this pair - may be negative
 */
public record PairBalanceRecord(String first, String second, long balance) {

    public static PairBalanceRecord fromJpa(PairBalance pairBalance) {
        return new PairBalanceRecord(pairBalance.getFirstPerson(), pairBalance.getSecondPerson(), pairBalance.getBalance());
    }
}
