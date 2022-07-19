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

/**
 * command line interpreter component to be autowired in the CommandLineRunner. It does the commands parsing, including
 * date recognition if given as optional 1st parameter and the command recognition and split / hand over of arguments.
 */
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
     * core method of the CommandLineInterpreter that parses the next user input line by use of the given scanner (bean).
     * It recognizes the defined commands and creates and returns a new Command. If not recognized, an UnknownCommand
     * is created and returned - also for the no args command, already an InvalidCommand is created, if args are given.
     * @return the created command found as parse result.
     */
    public LineCommand parseNext() {
        List<String> tokens = new ArrayList<>(Arrays.asList(scanner.nextLine().trim().split("\\s+")));
        LocalDate commandDate = getDateAndRemoveFromList(tokens);
        if (tokens.isEmpty()) {
            return new UnknownCommand(printer);
        }
        String[] commandArgs = tokens.subList(1, tokens.size()).toArray(String[]::new);
        return switch (tokens.get(0).toLowerCase()) {
            case "borrow" -> new BorrowCommand(printer, commandDate, commandArgs);
            case "repay" -> new RepayCommand(printer,  commandDate, commandArgs);
            case "balance" -> new BalanceCommand(printer, commandDate, commandArgs);
            case "balanceperfect" -> new BalanceCommand(printer, commandDate, true, commandArgs);
            case "purchase" -> new PurchaseCommand(printer, commandDate, commandArgs);
            case "cashback" -> new PurchaseCommand(printer, commandDate, true, commandArgs);
            case "group" -> new GroupCommand(printer, commandArgs);
            case "secretsanta" -> new SecretSantaCommand(printer, commandArgs);
            case "writeoff" -> tokens.size() == 1 ? new WriteOffCommand(commandDate) : new InvalidCommand(printer);
            case "help" -> tokens.size() == 1 ? new HelpCommand(printer) : new InvalidCommand(printer);
            case "exit" -> tokens.size() == 1 ? new ExitCommand() : new InvalidCommand(printer);
            default -> new UnknownCommand(printer);
        };
    }

    /**
     * if the first token in the list can be parsed into a LocalDate with specified format, this date is returned and
     * the list token removed. If not, the current date is returned
     * @param tokens the list of command line tokens
     * @return parsed date or current, if no parsable date.
     */
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
