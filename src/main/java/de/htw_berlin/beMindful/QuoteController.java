package de.htw_berlin.beMindful;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@CrossOrigin(origins = {"https://bemindful-frontend.onrender.com", "http://localhost:5173"})
public class QuoteController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/quote")
    public Object getQuote() {
        return restTemplate.getForObject("https://zenquotes.io/api/today", Object.class);
    }
}
