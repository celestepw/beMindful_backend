package de.htw_berlin.beMindful;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import de.htw_berlin.beMindful.security.JwtAuthFilter;
import de.htw_berlin.beMindful.security.SecurityConfig;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-Slice-Test für den ActivityController. Das ActivityRepository wird gemockt,
 * es wird also keine echte Datenbank gebraucht.
 *
 * Die SecurityAutoConfiguration wird ausgeschlossen, damit die Security-Filterkette
 * (JWT) im Test nicht greift und wir keinen echten Token mitschicken müssen. Zusätzlich
 * werden JwtAuthFilter und SecurityConfig aus dem Slice ausgeschlossen, da sie sonst
 * (als Filter- bzw. Security-Bean) geladen würden und JwtUtil erwarten.
 *
 * Der Controller liest den eingeloggten User über den Authentication-Parameter aus.
 * Da Security hier deaktiviert ist, geben wir dem Request per .principal(...) selbst
 * eine Authentication mit – Spring MVC injiziert sie in den Controller.
 */
@WebMvcTest(controllers = ActivityController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthFilter.class, SecurityConfig.class}))
class ActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActivityRepository repository;

    @Test
    void getActivities_returns200WithData() throws Exception {
        ActivityEntry entry = new ActivityEntry("Atemübung");
        entry.setMood("gut");
        when(repository.findByOwner("testuser")).thenReturn(List.of(entry));

        Authentication auth =
                new UsernamePasswordAuthenticationToken("testuser", null, Collections.emptyList());

        mockMvc.perform(get("/activities").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Atemübung"))
                .andExpect(jsonPath("$[0].mood").value("gut"));
    }
}
