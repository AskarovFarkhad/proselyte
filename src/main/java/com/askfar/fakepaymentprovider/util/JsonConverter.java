package com.askfar.fakepaymentprovider.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    public static class JsonConvertingException extends RuntimeException {
        public JsonConvertingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
