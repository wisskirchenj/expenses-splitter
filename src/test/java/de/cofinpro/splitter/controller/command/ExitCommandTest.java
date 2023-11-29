package de.cofinpro.splitter.controller.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ExitCommandTest {

    @Test
    void test_isExit_returns_true() {
        ExitCommand exitCommand = new ExitCommand();
        exitCommand.execute(null);
        assertTrue(exitCommand.isExit());
    }

}