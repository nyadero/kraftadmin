package com.bowerzlabs.utils;

import java.util.Currency;
import java.util.Set;

public class CurrencyUtils {
    Set<Currency> currencies;

    public static Set<Currency> getCurrencies() {
        return Currency.getAvailableCurrencies();
    }
}
