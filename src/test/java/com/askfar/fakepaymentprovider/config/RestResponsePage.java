package com.askfar.fakepaymentprovider.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class RestResponsePage<T> extends PageImpl<T> {

    @Serial
    private static final long serialVersionUID = 867755909294344407L;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestResponsePage(
            @JsonProperty("content") List<T> content,
            @JsonProperty("pageable") JsonNode pageable,
            @JsonProperty("totalElements") Long totalElements,
            @JsonProperty("last") boolean last,
            @JsonProperty("totalPages") int totalPages,
            @JsonProperty("size") int size,
            @JsonProperty("number") int number,
            @JsonProperty("sort") JsonNode sort,
            @JsonProperty("numberOfElements") int numberOfElements,
            @JsonProperty("first") boolean first,
            @JsonProperty("empty") boolean empty) {

        super(content, PageRequest.of(number, size), totalElements);
    }

    public RestResponsePage(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public RestResponsePage(List<T> content) {
        super(content);
    }

    public RestResponsePage() {
        super(new ArrayList<>());
    }
}