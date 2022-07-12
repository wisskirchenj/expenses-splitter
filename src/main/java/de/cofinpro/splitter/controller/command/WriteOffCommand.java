package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.model.Repositories;

import java.time.LocalDate;

/**
 * **
 * LineCommand implementation of the "writeOff" command, taking a limit date argument
 * which deletes all transactions from the database before or equal the limit date.
 */
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
