package de.cofinpro.splitter.controller.command;

import de.cofinpro.splitter.io.ConsolePrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;

@MockitoSettings
class PurchaseCommandTest {

    @Mock
    ConsolePrinter mockPrinter;

    @BeforeEach
    void setUp() {
    }

    @Test
    void test_prints_error_message_when_amount_is_zero() {
        LocalDate date = LocalDate.now();
        String[] arguments = {"payer", "refunder", "0", "(TEAM,", "Elon,", "-GIRLS)"};
        PurchaseCommand purchaseCommand = new PurchaseCommand(mockPrinter, date, false, arguments);
        purchaseCommand.execute(null);
        verify(mockPrinter).printError(LineCommand.ERROR_INVALID);
    }

    @Test
    void test_prints_error_message_when_less_than_4_arguments_are_given() {
        LocalDate date = LocalDate.now();
        String[] arguments = {"payer", "refunder"};
        PurchaseCommand purchaseCommand = new PurchaseCommand(mockPrinter, date, false, arguments);
        purchaseCommand.execute(null);
        verify(mockPrinter).printError(LineCommand.ERROR_INVALID);
    }

    @Test
    void test_prints_error_message_when_amount_argument_is_not_a_valid_decimal() {
        LocalDate date = LocalDate.now();
        String[] arguments = {"payer", "refunder", "invalid", "person1", "person2"};
        PurchaseCommand purchaseCommand = new PurchaseCommand(mockPrinter, date, false, arguments);
        purchaseCommand.execute(null);
        verify(mockPrinter).printError(LineCommand.ERROR_INVALID);
    }

    @Test
    void test_prints_error_message_when_no_group() {
        LocalDate date = LocalDate.now();
        String[] arguments = {"payer", "refunder", "10.0", "invalidgroup"};
        PurchaseCommand purchaseCommand = new PurchaseCommand(mockPrinter, date, false, arguments);
        purchaseCommand.execute(null);
        verify(mockPrinter).printError(LineCommand.ERROR_INVALID);
    }
}