package com.kraftadmin.data;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class XLSDataExporter implements DataExporter {
    @Override
    public byte[] export(List<?> data) {
        return new byte[0];
    }

    @Override
    public String getContentType() {
        return "application/vnd.ms-excel";
    }

    @Override
    public String getFileExtension() {
        return ".xls";
    }

    @Override
    public String getFormatKey() {
        return "xls";
    }
}
