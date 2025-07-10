package com.bowerzlabs.formfields.strategies;

// First, create a simple OptionItem class
public class OptionItem {
    private final Object value;
    private final String displayText;

    public OptionItem(Object value, String displayText) {
        this.value = value;
        this.displayText = displayText;
    }

    public Object getValue() {
        return value;
    }

    public String getDisplayText() {
        return displayText;
    }

    @Override
    public String toString() {
        return displayText;
    }
}
