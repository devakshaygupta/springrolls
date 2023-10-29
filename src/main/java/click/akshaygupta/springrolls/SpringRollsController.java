package click.akshaygupta.springrolls;

import java.net.URI;
import java.security.Principal;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/springrolls")
public class SpringRollsController {
    private SpringRollRepository springRollRepository;

    public SpringRollsController(SpringRollRepository springRollRepository) {
        this.springRollRepository = springRollRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpringRoll> findSpringRollById(@PathVariable Long id, Principal principal) {
        Optional<SpringRoll> springRollOptional = Optional.ofNullable(springRollRepository.findByIdAndOwner(id, principal.getName()));
        if (springRollOptional.isPresent()) {
            return ResponseEntity.ok(springRollOptional.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    private ResponseEntity<Void> createSpringRolls(@RequestBody SpringRoll newSpringRollRecipe,
            UriComponentsBuilder ucb, Principal principal) {
        SpringRoll savedSpringRoll = springRollRepository.save(newSpringRollRecipe);
        URI locationOfNewCashCard = ucb
                .path("springrolls/{id}")
                .buildAndExpand(savedSpringRoll.id())
                .toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }
}