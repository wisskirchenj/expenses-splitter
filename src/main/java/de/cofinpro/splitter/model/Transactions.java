package de.cofinpro.splitter.model;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static java.util.function.Predicate.not;

/**
 * HashMap derived bean class, that keeps track of all mutual (PersonPair) transactions.
 */
@Component
public class Transactions extends HashMap<PersonPair, PairTransactions> {

    /**
     * add a transaction for the map key of the PersonPair to their PairTransactions list
     * @param personPair the map key to add to
     * @param moneyTransfer transaction to add
     */
    public void addTransaction(PersonPair personPair, MoneyTransfer moneyTransfer) {
        PairTransactions pairTransactions = getOrDefault(personPair, new PairTransactions());
        pairTransactions.addOrdered(moneyTransfer);
        put(personPair, pairTransactions);
    }

    /**
     * get all mutual balances stored at the given date (including). Empty strings are filtered out.
     * @param balanceDate date to balance
     * @return possibly empty list of "A owes B xy" texts as strings.
     */
    public List<String> getBalances(LocalDate balanceDate) {
        return entrySet().stream().map(entry -> getOwesText(entry, balanceDate))
                .filter(not(String::isEmpty)).sorted().toList();
    }

    /**
     * calculate the "A owes B amount x" text for one map entry
     * @param personPairTransactionsEntry map entry for one person pairs transactions
     * @param balanceDate the date to balance
     * @return the builder build text - or empty string if balance is zero at the date.
     */
    private String getOwesText(Entry<PersonPair, PairTransactions> personPairTransactionsEntry, LocalDate balanceDate) {
        int balance = personPairTransactionsEntry.getValue().getBalance(balanceDate);
        StringBuilder builder = new StringBuilder();
        if (balance > 0) {
            builder.append(personPairTransactionsEntry.getKey().getFirst()).append(" owes ")
                    .append(personPairTransactionsEntry.getKey().getSecond()).append(" ").append(balance);
            return builder.toString();
        }
        if (balance < 0) {
            builder.append(personPairTransactionsEntry.getKey().getSecond()).append(" owes ")
                    .append(personPairTransactionsEntry.getKey().getFirst()).append(" ").append(Math.abs(balance));
            return builder.toString();
        }
        return "";
    }
}