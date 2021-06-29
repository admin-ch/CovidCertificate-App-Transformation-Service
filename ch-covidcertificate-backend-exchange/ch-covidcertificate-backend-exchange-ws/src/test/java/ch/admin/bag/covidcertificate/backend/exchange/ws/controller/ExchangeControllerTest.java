package ch.admin.bag.covidcertificate.backend.exchange.ws.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

class ExchangeControllerTest extends BaseControllerTest {

    private final String BASE_URL = "/exchange/v1/";

    @Test
    void helloTest() throws Exception {
        final MockHttpServletResponse response =
                mockMvc.perform(get(BASE_URL).accept(MediaType.TEXT_PLAIN))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse();

        assertNotNull(response);
        assertEquals("Hello from CH CovidCertificate Exchange WS", response.getContentAsString());
    }
}
