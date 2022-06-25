package de.cofinpro.splitter.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommandLineInterpreterTest {

    @MockBean
    Scanner scanner;

    @InjectMocks
    CommandLineInterpreter commandLineInterpreter;

    @BeforeEach
    void setUp() {
    }

    @Test
    void parseNext() {
    }
}