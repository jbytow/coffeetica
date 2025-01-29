package com.example.coffeetica.coffee.specification;

import com.example.coffeetica.coffee.models.CoffeeEntity;
import com.example.coffeetica.coffee.models.enums.FlavorProfile;
import com.example.coffeetica.coffee.models.enums.Region;
import com.example.coffeetica.coffee.models.enums.RoastLevel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
            String roasteryName //
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Name (case insensitive, contains)
            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                ));
            }

            // Country of Origin (case insensitive, contains)
            if (countryOfOrigin != null && !countryOfOrigin.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("countryOfOrigin")),
                        "%" + countryOfOrigin.toLowerCase() + "%"
                ));
            }

            // Region (case sensitive - enum)
            if (region != null) {
                predicates.add(criteriaBuilder.equal(root.get("region"), region));
            }

            // Roast Level (case sensitive - enum)
            if (roastLevel != null) {
                predicates.add(criteriaBuilder.equal(root.get("roastLevel"), roastLevel));
            }

            // Flavor Profile (case sensitive - enum)
            if (flavorProfile != null) {
                predicates.add(criteriaBuilder.equal(root.get("flavorProfile"), flavorProfile));
            }

            // Flavor Notes (case insensitive, contains)
            if (flavorNotes != null && !flavorNotes.isEmpty()) {
                predicates.add(
                        criteriaBuilder.or(
                                flavorNotes.stream()
                                        .map(note -> criteriaBuilder.like(
                                                criteriaBuilder.lower(root.join("flavorNotes")),
                                                "%" + note.toLowerCase() + "%"
                                        ))
                                        .toArray(Predicate[]::new)
                        )
                );
            }

            // Processing Method (case insensitive, contains)
            if (processingMethod != null && !processingMethod.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("processingMethod")),
                        "%" + processingMethod.toLowerCase() + "%"
                ));
            }

            // Production Year (numeric comparisons)
            if (minProductionYear != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("productionYear"), minProductionYear
                ));
            }
            if (maxProductionYear != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("productionYear"), maxProductionYear
                ));
            }

            // Roastery Name (case insensitive, matches)
            if (roasteryName != null && !roasteryName.isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("roastery").get("name")),
                        roasteryName.toLowerCase()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}