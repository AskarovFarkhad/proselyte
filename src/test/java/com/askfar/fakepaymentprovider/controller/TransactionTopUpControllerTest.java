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

class TransactionTopUpControllerTest extends AbstractTest {

    private static final String TRANSACTION_GET_DETAILS_URL = "/transaction/%s/details";

    private static final String TRANSACTION_GET_LIST_URL = "/transaction/list";

    @Test
    void findTopUpDetails() throws IOException {
        WebTestClient.BodySpec<TransactionResponseDto, ?> bodySpec =
                webClient.get().uri(String.format(BASE_PATH + TRANSACTION_GET_DETAILS_URL, "e3480b72-c82c-45cd-beb7-a8155b04b7e0"))
                         .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                         .header(AUTHORIZATION_HEADER, AUTHORIZATION_VAL)
                         .exchange()
                         .expectStatus().isOk()
                         .expectBody(TransactionResponseDto.class);

        TransactionResponseDto expected = objectMapper.readValue(getFileText("json/get-transaction-details-response.json"), TransactionResponseDto.class);
        TransactionResponseDto response = bodySpec.returnResult().getResponseBody();

        assertThat(response).isNotNull().isEqualTo(expected);
    }

    @Test
    void findTopUpDetails_NotFound() {
        webClient.get().uri(String.format(BASE_PATH + TRANSACTION_GET_DETAILS_URL, "e3480b72-c82c-45cd-beb7-a2155b04b7e0"))
                 .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                 .header(AUTHORIZATION_HEADER, AUTHORIZATION_VAL)
                 .exchange()
                 .expectStatus().isNotFound();
    }

    @Test
    void findTopUpDetails_Unauthorized() {
        webClient.get().uri(String.format(BASE_PATH + TRANSACTION_GET_DETAILS_URL, "e3480b72-c82c-45cd-beb7-a2155b04b7e0"))
                 .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                 .header(AUTHORIZATION_HEADER, NOT_CORRECT_AUTHORIZATION_VAL)
                 .exchange()
                 .expectStatus().isUnauthorized();
    }

    @Test
    void findTopUpAllByToday() {
        WebTestClient.BodySpec<RestResponsePage<TransactionResponseDto>, ?> bodySpec =
                webClient.get().uri(BASE_PATH + TRANSACTION_GET_LIST_URL)
                         .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                         .header(AUTHORIZATION_HEADER, AUTHORIZATION_VAL)
                         .exchange()
                         .expectStatus().isOk()
                         .expectBody(responseType);

        PageImpl<TransactionResponseDto> responsePage = bodySpec.returnResult().getResponseBody();

        assertThat(responsePage).isNotNull().isNotEmpty();

        List<TransactionResponseDto> pageContent = responsePage.getContent();

        assertThat(responsePage.getTotalElements()).isEqualTo(2L);
        assertThat(pageContent.get(0).getTransactionId()).isEqualTo(UUID.fromString("69ef394d-ad72-47b9-b5be-2445181b7532"));
        assertThat(pageContent.get(1).getTransactionId()).isEqualTo(UUID.fromString("69ef394d-ad72-47b9-b5be-2445181b7522"));
        assertThat(pageContent.get(0).getAmount()).isEqualTo(new BigDecimal("20.24"));
        assertThat(pageContent.get(1).getAmount()).isEqualTo(new BigDecimal("200.54"));

        assertThat(pageContent.get(0).getStatus()).isEqualTo(TransactionStatus.SUCCESS);
        assertThat(pageContent.get(1).getStatus()).isEqualTo(TransactionStatus.FAILED);

        assertThat(pageContent.get(0).getCardData().getCardNumber()).isEqualTo("2222***2222");
        assertThat(pageContent.get(1).getCardData().getCardNumber()).isEqualTo("4444***4444");
    }

    @Test
    void findTopUpAllByDateTime() {
    }

    @Test
    void createTopUp() {
    }
}