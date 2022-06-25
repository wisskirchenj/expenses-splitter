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

    public void addTransaction(PersonPair personPair, MoneyTransfer moneyTransfer) {
        PairTransactions pairTransactions = getOrDefault(personPair, new PairTransactions());
        pairTransactions.addOrdered(moneyTransfer);
        put(personPair, pairTransactions);
    }

    public List<String> getBalances(LocalDate balanceDate) {
        return entrySet().stream().map(entry -> getOwesText(entry, balanceDate))
                .filter(not(String::isEmpty)).sorted().toList();
    }

    private String getOwesText(Entry<PersonPair, PairTransactions> personPairTransactionsEntry, LocalDate balanceDate) {
        int balance = personPairTransactionsEntry.getValue().getBalance(balanceDate);
        StringBuilder builder = new StringBuilder();
        if (balance < 0) {
            builder.append(personPairTransactionsEntry.getKey().getFirst()).append(" owes ")
                    .append(personPairTransactionsEntry.getKey().getSecond()).append(" ").append(Math.abs(balance));
            return builder.toString();
        }
        if (balance > 0) {
            builder.append(personPairTransactionsEntry.getKey().getSecond()).append(" owes ")
                    .append(personPairTransactionsEntry.getKey().getFirst()).append(" ").append(balance);
            return builder.toString();
        }
        return "";
    }
}
