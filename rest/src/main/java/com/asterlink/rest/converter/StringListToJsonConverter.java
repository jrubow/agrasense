// src/main/java/com/asterlink/rest/converter/StringListToJsonConverter.java
package com.asterlink.rest.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

@Converter
public class StringListToJsonConverter implements AttributeConverter<List<String>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null; // Or "[]" if you prefer an empty JSON array for null/empty lists
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            // Handle error, e.g., log it or throw a runtime exception
            throw new RuntimeException("Error converting List<String> to JSON string", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return new ArrayList<>(); // Return empty list for null or empty string
        }
        try {
            // Read value as a List.class or a specific type token if needed
            return objectMapper.readValue(dbData, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (JsonProcessingException e) {
            // Handle error, e.g., log it or return an empty list
            throw new RuntimeException("Error converting JSON string to List<String>", e);
        }
    }
}