package de.cofinpro.splitter.controller;

import de.cofinpro.splitter.controller.command.*;
import de.cofinpro.splitter.io.ConsolePrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Component
public class CommandLineInterpreter {

    private final Scanner scanner;
    private final ConsolePrinter printer;
    private final DateTimeFormatter dateTimeFormatter;

    @Autowired
    public CommandLineInterpreter(Scanner scanner, ConsolePrinter printer, DateTimeFormatter dateTimeFormatter) {
        this.scanner = scanner;
        this.printer = printer;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    /**
     * core method of the CommandLineInterpreter that parses the next user input line by use of the given scanner (constructor).
     * It recognizes the defined commands and - if the argument number for the command matches - it creates and
     * returns a new Command. In ay other case an UnknownCommand is created and returned.
     * @return the created command found as parse result.
     */
    public LineCommand parseNext() {
        List<String> tokens = new ArrayList<>(Arrays.asList(scanner.nextLine().split("\\s+")));
        LocalDate commandDate = getDateAndRemoveFromList(tokens);
        if (tokens.isEmpty()) {
            return new UnknownCommand(printer);
        }
        String[] commandArgs = tokens.subList(1, tokens.size()).toArray(String[]::new);
        return switch (tokens.get(0).toLowerCase()) {
            case "borrow" -> new BorrowCommand(printer, commandDate, commandArgs);
            case "repay" -> new RepayCommand(printer,  commandDate, commandArgs);
            case "balance" -> new BalanceCommand(printer, commandDate, commandArgs);
            case "help" -> tokens.size() == 1 ? new HelpCommand(printer) : new InvalidCommand(printer);
            case "exit" -> tokens.size() == 1 ? new ExitCommand() : new InvalidCommand(printer);
            default -> new UnknownCommand(printer);
        };
    }

    private LocalDate getDateAndRemoveFromList(List<String> tokens) {
        try {
            LocalDate date = LocalDate.parse(tokens.get(0), dateTimeFormatter);
            tokens.remove(0);
            return date;
        } catch (DateTimeParseException parseException) {
            return LocalDate.now();
        }
    }
}
