package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.model.Repositories;

import java.time.LocalDate;

public class WriteOffCommand implements LineCommand {

    private final LocalDate limitDate;

    public WriteOffCommand(LocalDate limitDate) {
        this.limitDate = limitDate;
    }

    @Override
    public void execute(Repositories repositories) {
        repositories.getTransactionRepository().deleteAllByDateIsBefore(limitDate.plusDays(1));
    }
}
