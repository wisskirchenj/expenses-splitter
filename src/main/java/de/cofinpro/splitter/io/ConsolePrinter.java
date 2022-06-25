package de.cofinpro.splitter.io;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ConsolePrinter {

    private static final String NO_REPAYMENTS = "No repayments";

    public void printError(String errorMessage) {
        log.error(errorMessage);
    }

    public void printInfo(String message) {
        log.info(message);
    }

    public void printOwes(List<String> balances) {
        if (balances.isEmpty()) {
            printInfo(NO_REPAYMENTS);
        } else {
            balances.forEach(this::printInfo);
        }
    }
}
