package de.cofinpro.splitter.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * List derived class, that holds all date sorted (guaranteed, if addOrdered - method used exclusively) transactions
 * between two persons (a PersonPair).
 */
public class PairTransactions extends ArrayList<MoneyTransfer> {

    /**
     * adds a new Money Transfer in a way, that the list contains ascending-date MoneyTransfers.
     * @param moneyTransfer the new Money Transfer to add
     */
    public void addOrdered(MoneyTransfer moneyTransfer) {
        int index = Collections.binarySearch(this, moneyTransfer, Comparator.comparing(MoneyTransfer::getDate));
        if (index >= 0) {
            add(index + 1, moneyTransfer);
        } else {
            // if binary Search does not find a transfer to this date, the ordered position is taken (cf. Collections.binarySearch)
            add(-(index + 1), moneyTransfer);
        }
    }

    /**
     * get the balance at the given balanceDate (including transactions up to that day) of the transactions between
     * the PersonPair - as seen from the "first" person perspective. (i.e. positive balance = first has to get money).
     * The balance is converted from cents into fractional currency.
     * @param balanceDate a given date to balance at
     * @return the balance sum as double
     */
    public double getBalance(LocalDate balanceDate) {
        return stream().filter(moneyTransfer -> moneyTransfer.getDate().compareTo(balanceDate) <= 0)
                .mapToLong(MoneyTransfer::getAmount).sum() / 100.0;
    }
}
