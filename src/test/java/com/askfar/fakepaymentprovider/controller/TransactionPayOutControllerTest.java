package com.askfar.fakepaymentprovider.controller;

import com.askfar.fakepaymentprovider.AbstractTest;
import com.askfar.fakepaymentprovider.config.RestResponsePage;
import com.askfar.fakepaymentprovider.dto.response.TransactionResponseDto;
import com.askfar.fakepaymentprovider.enums.TransactionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionPayOutControllerTest extends AbstractTest {

    private static final String PAY_OUT_GET_DETAILS_URL = "/payout/%s/details";

    private static final String PAY_OUT_GET_LIST_URL = "/payout/list";

    @Test
    void findTopUpDetails() throws IOException {
        WebTestClient.BodySpec<TransactionResponseDto, ?> bodySpec =
                webClient.get().uri(String.format(BASE_PATH + PAY_OUT_GET_DETAILS_URL, "7f85bf5f-1b93-4bee-914a-a59264e6a344"))
                         .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                         .header(AUTHORIZATION_HEADER, AUTHORIZATION_VAL)
                         .exchange()
                         .expectStatus().isOk()
                         .expectBody(TransactionResponseDto.class);

        TransactionResponseDto expected = objectMapper.readValue(getFileText("json/get-payout-details-response.json"), TransactionResponseDto.class);
        TransactionResponseDto response = bodySpec.returnResult().getResponseBody();

        assertThat(response).isNotNull().isEqualTo(expected);
    }

    @Test
    void findTopUpDetails_NotFound() {
        webClient.get().uri(String.format(BASE_PATH + PAY_OUT_GET_DETAILS_URL, "7f85bf5f-1b93-4bee-114a-a59264e6a344"))
                 .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                 .header(AUTHORIZATION_HEADER, AUTHORIZATION_VAL)
                 .exchange()
                 .expectStatus().isNotFound();
    }

    @Test
    void findTopUpDetails_Unauthorized() {
        webClient.get().uri(String.format(BASE_PATH + PAY_OUT_GET_DETAILS_URL, "7f85bf5f-1b93-4bee-914a-a59264e6a344"))
                 .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                 .header(AUTHORIZATION_HEADER, NOT_CORRECT_AUTHORIZATION_VAL)
                 .exchange()
                 .expectStatus().isUnauthorized();
    }

    @Test
    void findTopUpAllByToday() {
        WebTestClient.BodySpec<RestResponsePage<TransactionResponseDto>, ?> bodySpec =
                webClient.get().uri(BASE_PATH + PAY_OUT_GET_LIST_URL)
                         .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                         .header(AUTHORIZATION_HEADER, AUTHORIZATION_VAL)
                         .exchange()
                         .expectStatus().isOk()
                         .expectBody(responseType);

        PageImpl<TransactionResponseDto> responsePage = bodySpec.returnResult().getResponseBody();

        assertThat(responsePage).isNotNull().isNotEmpty();

        List<TransactionResponseDto> pageContent = responsePage.getContent();

        assertThat(responsePage.getTotalElements()).isEqualTo(2L);
        assertThat(pageContent.get(0).getTransactionId()).isEqualTo(UUID.fromString("57c00c84-1580-43f7-8fea-d89b68d7fcd0"));
        assertThat(pageContent.get(1).getTransactionId()).isEqualTo(UUID.fromString("57c00c84-1580-43f7-8fea-d89b48d7fcd0"));
        assertThat(pageContent.get(0).getAmount()).isEqualTo(new BigDecimal("40.12"));
        assertThat(pageContent.get(1).getAmount()).isEqualTo(new BigDecimal("1500.22"));

        assertThat(pageContent.get(0).getStatus()).isEqualTo(TransactionStatus.FAILED);
        assertThat(pageContent.get(1).getStatus()).isEqualTo(TransactionStatus.FAILED);

        assertThat(pageContent.get(0).getCardData().getCardNumber()).isEqualTo("4444***4444");
        assertThat(pageContent.get(1).getCardData().getCardNumber()).isEqualTo("3333***3333");
    }
}