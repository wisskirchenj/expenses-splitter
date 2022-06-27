package de.cofinpro.splitter.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
