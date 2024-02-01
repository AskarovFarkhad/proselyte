package com.askfar.fakepaymentprovider;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
public abstract class AbstractTest {

    protected MockMvc mockMvc;
}
