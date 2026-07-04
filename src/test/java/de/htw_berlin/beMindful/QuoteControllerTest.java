package de.htw_berlin.beMindful;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Testet den QuoteController ohne echten Netzwerkaufruf: die ZenQuotes-Antwort
 * wird über MockRestServiceServer simuliert.
 */
class QuoteControllerTest {

    @Test
    void getQuote_returnsBodyFromZenQuotes() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(requestTo("https://zenquotes.io/api/today"))
                .andRespond(withSuccess(
                        "[{\"q\":\"Bleib achtsam.\",\"a\":\"beMindful\"}]",
                        MediaType.APPLICATION_JSON));

        QuoteController controller = new QuoteController(restTemplate);
        Object result = controller.getQuote();

        assertThat(result).isInstanceOf(List.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> first = ((List<Map<String, Object>>) result).get(0);
        assertThat(first).containsEntry("q", "Bleib achtsam.");
        assertThat(first).containsEntry("a", "beMindful");
        server.verify();
    }
}
