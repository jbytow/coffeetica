package com.example.coffeetica.coffee.specification;

import com.example.coffeetica.coffee.models.CoffeeEntity;
import com.example.coffeetica.coffee.models.enums.FlavorProfile;
import com.example.coffeetica.coffee.models.enums.Region;
import com.example.coffeetica.coffee.models.enums.RoastLevel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Provides static methods for building JPA {@link Specification}s
 * to filter coffee results by various attributes.
 */
public class CoffeeSpecification {

    public static Specification<CoffeeEntity> filterByAttributes(
            String name,
            String countryOfOrigin,
            Region region,
            RoastLevel roastLevel,
            FlavorProfile flavorProfile,
            Set<String> flavorNotes,
            String processingMethod,
            Integer minProductionYear,
            Integer maxProductionYear,
            String roasteryName
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Name (case-insensitive, contains)
            if (name != null && !name.isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                ));
            }

            // Country of Origin (case-insensitive, contains)
            if (countryOfOrigin != null && !countryOfOrigin.isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("countryOfOrigin")),
                        "%" + countryOfOrigin.toLowerCase() + "%"
                ));
            }

            // Region, RoastLevel, FlavorProfile (enums)
            if (region != null) {
                predicates.add(criteriaBuilder.equal(root.get("region"), region));
            }
            if (roastLevel != null) {
                predicates.add(criteriaBuilder.equal(root.get("roastLevel"), roastLevel));
            }
            if (flavorProfile != null) {
                predicates.add(criteriaBuilder.equal(root.get("flavorProfile"), flavorProfile));
            }

            // Flavor Notes (case-insensitive, partial match)
            // This example uses OR logic to see if any note matches
            if (flavorNotes != null && !flavorNotes.isEmpty()) {
                predicates.add(criteriaBuilder.or(
                        flavorNotes.stream()
                                .filter(Objects::nonNull)
                                .map(note -> criteriaBuilder.like(
                                        criteriaBuilder.lower(root.join("flavorNotes")),
                                        "%" + note.toLowerCase() + "%"
                                ))
                                .toArray(Predicate[]::new)
                ));
            }

            // Processing Method (case-insensitive, contains)
            if (processingMethod != null && !processingMethod.isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("processingMethod")),
                        "%" + processingMethod.toLowerCase() + "%"
                ));
            }

            // Production Year range
            if (minProductionYear != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("productionYear"),
                        minProductionYear
                ));
            }
            if (maxProductionYear != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("productionYear"),
                        maxProductionYear
                ));
            }

            // Roastery name (case-insensitive match)
            if (roasteryName != null && !roasteryName.isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("roastery").get("name")),
                        roasteryName.toLowerCase()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}