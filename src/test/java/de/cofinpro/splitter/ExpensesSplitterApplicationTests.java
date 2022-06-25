package de.cofinpro.splitter;

import de.cofinpro.splitter.controller.CommandLineInterpreter;
import de.cofinpro.splitter.controller.SplitterCommandLineRunner;
import de.cofinpro.splitter.controller.command.ExitCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Scanner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ExpensesSplitterApplicationTests {

    @MockBean
    CommandLineInterpreter commandLineInterpreter;

    @SpyBean
    @InjectMocks
    SplitterCommandLineRunner commandLineRunner;

    //@Test
    void whenContextLoads_ThenRunnerRuns() {
        when(commandLineInterpreter.parseNext()).thenReturn(new ExitCommand());
        verify(commandLineRunner).run(any());
    }
}
