package com.bowerzlabs.data;

import java.util.List;

public interface DataExporter {
    byte[] export(List<?> data);
    String getContentType(); // e.g., "application/json", "text/csv"
    String getFileExtension(); // e.g., ".json"
    String getFormatKey(); // e.g., "json", "csv", etc.
}
