package com.kraftadmin.data;

import java.util.List;

public class CSVDataExporter implements DataExporter {
    @Override
    public byte[] export(List<?> data) {
        return new byte[0];
    }

    @Override
    public String getContentType() {
        return "text/csv";
    }

    @Override
    public String getFileExtension() {
        return ".csv";
    }

    @Override
    public String getFormatKey() {
        return "csv";
    }
}
