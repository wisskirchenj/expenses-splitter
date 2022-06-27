package de.cofinpro.splitter.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * container bean that represents the overall data model gathered throughout the application run,
 * i.e. created groups and transactions, which are in turn autowired.
 */
@Component
@Data
public class ExpensesModel {

    private final Transactions transactions;
    private final Groups groups;

    @Autowired
    public ExpensesModel(Transactions transactions, Groups groups) {
        this.transactions = transactions;
        this.groups = groups;
    }
}
