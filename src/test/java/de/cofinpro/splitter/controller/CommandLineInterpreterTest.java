package de.cofinpro.splitter.controller;

import de.cofinpro.splitter.controller.command.InvalidCommand;
import de.cofinpro.splitter.controller.command.LineCommand;
import de.cofinpro.splitter.controller.command.UnknownCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandLineInterpreterTest {

    DateTimeFormatter dateFormatter;

    @BeforeEach
    void setup() {
        dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    }

    @Test
    void test_parsenext_invalid_writeoff_command() {
        Scanner scanner = new Scanner("2022.01.01 writeoff arg1");
        CommandLineInterpreter commandLineInterpreter
                = new CommandLineInterpreter(scanner, null, dateFormatter);
        LineCommand result = commandLineInterpreter.parseNext();
        assertTrue(result instanceof InvalidCommand);
    }

    @Test
    void test_parsenext_empty_input_line() {
        Scanner scanner = new Scanner(" ");
        CommandLineInterpreter commandLineInterpreter
                = new CommandLineInterpreter(scanner, null, dateFormatter);
        LineCommand result = commandLineInterpreter.parseNext();
        assertTrue(result instanceof UnknownCommand);
    }
}