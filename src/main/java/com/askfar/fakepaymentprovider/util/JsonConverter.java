package com.askfar.fakepaymentprovider.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.postgresql.util.PGobject;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonConverter {

    private final ObjectMapper objectMapper;

    public String getJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JsonConvertingException("Failed convert Object to Json ", e);
        }
    }

    public <T> T getObject(String json, Class<T> tClass) {
        try {
            return objectMapper.readValue(json, tClass);
        } catch (JsonProcessingException e) {
            throw new JsonConvertingException("Failed convert Json to Object", e);
        }
    }

    public PGobject toJSONObject(String value) {
        String modifiedValue = value;
        try {
            if (ObjectUtils.isNotEmpty(value)) {
                modifiedValue = value.replaceAll("[\\\\]+u0000", "");
            }
            PGobject result = new PGobject();
            result.setType("json");
            result.setValue(modifiedValue);
            return result;
        } catch (SQLException e) {
            log.error("Failed transform to json", e);
            throw new JsonConvertingException("Failed transform to json", e);
        }
    }

    public static class JsonConvertingException extends RuntimeException {
        public JsonConvertingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
