package click.akshaygupta.springrolls;

import java.net.URI;
import java.security.Principal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringRollsControllerTest {
    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    void setup() {
        // Create a mock Principal object for testing
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = Mockito.mock(Authentication.class);
        Principal principal = Mockito.mock(Principal.class);

        // Set up the default principal's name
        Mockito.when(principal.getName()).thenReturn("johndoe");
        Mockito.when(authentication.getCredentials()).thenReturn("abc123");

        // Set the authentication object in the security context
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);
        securityContext.setAuthentication(authentication);
    }

    @Test
    void testGetSpringRollById() {
        Principal principal = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        ResponseEntity<String> response = restTemplate.withBasicAuth(principal.getName(), (String) authentication.getCredentials()).getForEntity("/springrolls/1",
                String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());

        Number id = documentContext.read("$.id");
        Assertions.assertThat(id).isNotNull();

        String name = documentContext.read("$.name");
        Assertions.assertThat(name).isEqualTo("Vietnamese Spring Rolls");

        Number price = documentContext.read("$.price");
        Assertions.assertThat(price).isEqualTo(4.99);
    }

    @Test
    void testGetSpringRollByUnknownId() {
        Principal principal = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        ResponseEntity<String> response = restTemplate.withBasicAuth(principal.getName(), (String) authentication.getCredentials()).getForEntity("/springrolls/6",
                String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        Assertions.assertThat(response.getBody()).isBlank();
    }

    @Test
    void testGetSpringRollByNonOwner() {
        Principal principal = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Mockito.when(principal.getName()).thenReturn("jackdoe");
        Mockito.when(authentication.getCredentials()).thenReturn("def456");

        ResponseEntity<String> response = restTemplate.withBasicAuth(principal.getName(), (String) authentication.getCredentials()).getForEntity("/springrolls/1",
                String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        Assertions.assertThat(response.getBody()).isBlank();
    }

    @Test
    @DirtiesContext
    void testCreateNewSpringRollRecipe() {
        Principal principal = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Mockito.when(principal.getName()).thenReturn("jackdoe");
        Mockito.when(authentication.getCredentials()).thenReturn("def456");

        SpringRoll newSpringRoll = SpringRoll.createWithOwner("Kimbap", "Seaweed, rice, veggies", "Mild", "Veg",
                "South Korea", 40, 3.00, principal);
        ResponseEntity<Void> createResponse = restTemplate.withBasicAuth(principal.getName(), (String) authentication.getCredentials())
                .postForEntity("/springrolls", newSpringRoll, Void.class);
        Assertions.assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewSpringRoll = createResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate.withBasicAuth(principal.getName(), (String) authentication.getCredentials())
                .getForEntity(locationOfNewSpringRoll, String.class);
        Assertions.assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String dietryType = documentContext.read("$.dietryType");
        Number caloriesPerPiece = documentContext.read("$.caloriesPerPiece");

        Assertions.assertThat(id).isNotNull();
        Assertions.assertThat(dietryType).isEqualTo("Veg");
        Assertions.assertThat(caloriesPerPiece).isEqualTo(40);
    }
}
