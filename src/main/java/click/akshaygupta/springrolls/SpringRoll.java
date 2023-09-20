package click.akshaygupta.springrolls;

import java.security.Principal;

import org.springframework.data.annotation.Id;

public record SpringRoll(@Id Long id, String name, String ingredients, String spiciness, String dietryType,
        String countryOfOrigin, int caloriesPerPiece, double price, String owner) {

    public static SpringRoll createWithOwner(String name, String ingredients, String spiciness, String dietaryType,
            String countryOfOrigin, int caloriesPerPiece, double price, Principal principal) {
        // Set the owner (principal's name) automatically here
        String ownerName = principal != null ? principal.getName() : null;
        return new SpringRoll(null, name, ingredients, spiciness, dietaryType, countryOfOrigin, caloriesPerPiece, price,
                ownerName);
    }
}
