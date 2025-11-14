package com.kraftadmin.utils;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KraftUtilsTest {
    String testString = "heLLOWorlD";

    @BeforeEach
    void setUp() {

    }

    @Test
    void snakeToCamel() {
        assertEquals("helloWorld", KraftUtils.snakeToCamel(testString));
    }

    @Test
    void camelToSnake() {
        assertEquals("hello_world", KraftUtils.camelToSnake(testString));
    }

    @Test
    void formatLabel() {
    }

    @Test
    void unformatLabel() {
        assertEquals("heLLOWorlD", KraftUtils.unformatLabel(testString));
    }

    @Test
    void isKotlinClass() {
    }
}
