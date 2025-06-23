package com.bowerzlabs.formfields;

import org.junit.Test;

public class FormFieldTest {
    private final String name = "username";

    @Test
    public void name() {
        assert name.equalsIgnoreCase("Username");
    }


}