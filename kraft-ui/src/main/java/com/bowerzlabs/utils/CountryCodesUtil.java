package com.bowerzlabs.utils;

import java.util.List;

public class CountryCodesUtil {
    private List<CountryCode> countryCodeList;

    public static List<CountryCode> getCountryCodeList(){
        return List.of(
          new CountryCode("Kenya", "KE", "+254")
        );
    }
}
