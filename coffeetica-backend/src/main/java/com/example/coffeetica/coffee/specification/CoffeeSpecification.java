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

            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (countryOfOrigin != null && !countryOfOrigin.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("countryOfOrigin"), countryOfOrigin));
            }
            if (region != null) {
                predicates.add(criteriaBuilder.equal(root.get("region"), region));
            }
            if (roastLevel != null) {
                predicates.add(criteriaBuilder.equal(root.get("roastLevel"), roastLevel));
            }
            if (flavorProfile != null) {
                predicates.add(criteriaBuilder.equal(root.get("flavorProfile"), flavorProfile));
            }
            if (flavorNotes != null && !flavorNotes.isEmpty()) {
                predicates.add(criteriaBuilder.isTrue(
                        root.join("flavorNotes").in(flavorNotes)
                ));
            }
            if (processingMethod != null && !processingMethod.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("processingMethod"), processingMethod));
            }
            if (minProductionYear != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("productionYear"), minProductionYear));
            }
            if (maxProductionYear != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("productionYear"), maxProductionYear));
            }
            if (roasteryName != null && !roasteryName.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("roastery").get("name")), "%" + roasteryName.toLowerCase() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}