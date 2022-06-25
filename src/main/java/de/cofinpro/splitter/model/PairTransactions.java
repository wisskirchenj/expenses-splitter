package de.cofinpro.splitter.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * List derived class, that holds a date sorted (guaranteed, if addOrdered - method used exclusively) transaction list.
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

    public int getBalance(LocalDate balanceDate) {
        return stream().filter(moneyTransfer -> moneyTransfer.getDate().compareTo(balanceDate) <= 0)
                .mapToInt(MoneyTransfer::getAmount).sum();
    }
}
