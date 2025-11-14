package com.kraftadmin.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JSONDataExporter implements DataExporter {
    private final ObjectMapper objectMapper;

    public JSONDataExporter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * @param data
     * @return
     */
    @Override
    public byte[] export(List<?> data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error exporting JSON", e);
        }
    }

    /**
     * @return
     */
    @Override
    public String getContentType() {
        return "application/json";
    }

    /// @return
    @Override
    public String getFileExtension() {
        return ".json";
    }

    /**
     * @return
     */
    @Override
    public String getFormatKey() {
        return "json";
    }
}
