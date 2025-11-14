package com.kraftadmin.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataExporterRegistry {
    private final Map<String, DataExporter> exporterMap = new HashMap<>();

    @Autowired
    public DataExporterRegistry(List<DataExporter> exporters) {
        for (DataExporter exporter : exporters) {
            exporterMap.put(exporter.getFormatKey().toLowerCase(), exporter);
        }
    }

    public DataExporter getExporter(String format) {
        DataExporter exporter = exporterMap.get(format.toLowerCase());
        if (exporter == null) {
            throw new IllegalArgumentException("Unsupported export format: " + format);
        }
        return exporter;
    }
}
