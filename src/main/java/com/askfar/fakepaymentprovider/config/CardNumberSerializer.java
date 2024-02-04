package com.askfar.fakepaymentprovider.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class CardNumberSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String cardNumber, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String maskedCardNumber = cardNumber.substring(0, 4) + "***" + cardNumber.substring(cardNumber.length() - 4);
        jsonGenerator.writeString(maskedCardNumber);
    }
}
