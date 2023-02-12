package de.cofinpro.splitter.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

/**
 * balance optimizer implementation to reduce settlements
 */
public class BalanceOptimizer {

    /**
     * central entry point called to optimize a list of pair balances as would be printed by the balance command.
     * First all persons net balances are computed and sorted into (net) owers (i.e. persons owing money) and (net) owees
     * (i.e. persons who are to be repayed). Then these two groups are settled in a clever way.
     * @param balances the balances list to optimize
     * @return the optimized (= shorter list of) pair balances
     */
    public List<PairBalanceRecord> optimize(List<PairBalanceRecord> balances) {
        Map<String, Long> netBalances = calculateNetBalances(balances);
        var oweeBalances = netBalances.entrySet().stream()
                .filter(entry -> entry.getValue() < 0)
                .sorted(comparingByValue())
                .collect(Collectors.toCollection(ArrayList::new));
        var owerBalances = netBalances.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .sorted(Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toCollection(ArrayList::new));

        List<PairBalanceRecord> optimizedBalances = new ArrayList<>();
        while (!owerBalances.isEmpty()) {
            optimizedBalances.add(settleNextPairBalance(owerBalances, oweeBalances));
        }
        return optimizedBalances;
    }

    /**
     * Algorithmic implementation to find as few as possible balances. The method find the next best settlement match and
     * updates the ower- and oweeBalances accordingly, i.e. corrects the amount returnesd as new pair balance.
     * First we look for matching ower and owee amounts and settle them immediately, when found - reducing 2 persons.
     * If no matching amounts can be found, the biggest amount of both owers and owees is settled, reducing 1 person,
     * because one of the contrahents keeps some remaining difference amount.
     * @return the new pair balance to be added to the optimized balance list.
     */
    private PairBalanceRecord settleNextPairBalance(List<Entry<String, Long>> owerBalances, List<Entry<String, Long>> oweeBalances) {
        for (var owerEntry: owerBalances) {
            for (var oweeEntry: oweeBalances) {
                if (owerEntry.getValue().equals(-oweeEntry.getValue())) {
                    owerBalances.remove(owerEntry);
                    oweeBalances.remove(oweeEntry);
                    return new PairBalanceRecord(owerEntry.getKey(), oweeEntry.getKey(), owerEntry.getValue());
                }
            }
        }
        var maxOwer = owerBalances.get(0);
        var maxOwee = oweeBalances.get(0);
        if (maxOwer.getValue() > -maxOwee.getValue()) {
            oweeBalances.remove(maxOwee);
            return new PairBalanceRecord(maxOwer.getKey(), maxOwee.getKey(),
                    maxOwer.setValue(maxOwer.getValue() + maxOwee.getValue()));
        }
        owerBalances.remove(maxOwer);
        maxOwee.setValue(maxOwer.getValue() + maxOwee.getValue());
        return new PairBalanceRecord(maxOwer.getKey(), maxOwee.getKey(), maxOwer.getValue());
    }

    /**
     * calculate all persons net balance from the pair balances list.
     * @param balances the pair balances as selected from the database
     * @return name, amount hashmap of net balances
     */
    Map<String, Long> calculateNetBalances(List<PairBalanceRecord> balances) {
        Map<String, Long> netBalances = new HashMap<>();
        for (PairBalanceRecord balance: balances) {
            netBalances.put(balance.first(), netBalances.getOrDefault(balance.first(), 0L) + balance.balance());
            netBalances.put(balance.second(), netBalances.getOrDefault(balance.second(), 0L) - balance.balance());
        }
        return netBalances;
    }
}
