package com.askfar.fakepaymentprovider;

import com.askfar.fakepaymentprovider.config.RestResponsePage;
import com.askfar.fakepaymentprovider.dto.response.TransactionResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Testcontainers
@ContextConfiguration
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = FakePaymentProviderApplication.class)
public abstract class AbstractTest {

    @Autowired
    protected WebTestClient webClient;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected ParameterizedTypeReference<RestResponsePage<TransactionResponseDto>> responseType = new ParameterizedTypeReference<>() {
    };

    protected static final String BASE_PATH = "/api/v1/payments";

    protected static final String AUTHORIZATION_HEADER = "Authorization";

    protected static final String AUTHORIZATION_VAL = "Basic bWVyY2hhbnRJZD1QUk9TRUxZVEU6c2VjcmV0S2V5PWIyZWVlYTNlMjc4MzRiNzQ5OWRkN2UwMTE0M2EyM2Rk";

    protected static final String NOT_CORRECT_AUTHORIZATION_VAL = "Basic qWV1Y2hhbnRJZD2QUk2TRUxZVEU6c5VjcmV0S2V5PWIyZWVlYTNlMjc5MzRiNzQ5OWRkN2UwMTE0M2EyM2Rk";

    public static String getFileText(String filePath) throws IOException {
        return IOUtils.toString(Objects.requireNonNull(AbstractTest.class.getClassLoader().getResourceAsStream(filePath)), StandardCharsets.UTF_8);
    }
}
