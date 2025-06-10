package com.bowerzlabs.utils;

import java.util.List;

public class CountryCodesUtil {
    public static List<CountryCode> getCountryCodeList() {
        return List.of(
                new CountryCode("Kenya", "KE", "+254"),
                new CountryCode("United States", "US", "+1"),
                new CountryCode("United Kingdom", "GB", "+44"),
                new CountryCode("Canada", "CA", "+1"),
                new CountryCode("Germany", "DE", "+49"),
                new CountryCode("France", "FR", "+33"),
                new CountryCode("India", "IN", "+91"),
                new CountryCode("South Africa", "ZA", "+27"),
                new CountryCode("Nigeria", "NG", "+234"),
                new CountryCode("Uganda", "UG", "+256"),
                new CountryCode("Tanzania", "TZ", "+255"),
                new CountryCode("Ethiopia", "ET", "+251"),
                new CountryCode("Ghana", "GH", "+233"),
                new CountryCode("Rwanda", "RW", "+250")
        );
    }
}

