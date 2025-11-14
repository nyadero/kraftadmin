package com.kraftadmin.constants;

public enum SearchOperation {
    EQUALS,              // For all types
    CONTAINS,            // For String
    STARTS_WITH,         // For String
    ENDS_WITH,           // For String
    GREATER_THAN,        // For numbers, dates
    LESS_THAN,           // For numbers, dates
    BEFORE,              // Alias for LESS_THAN on dates
    AFTER,               // Alias for GREATER_THAN on dates
    BETWEEN              // For date/numeric ranges
}
