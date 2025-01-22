package com.example.coffeetica.coffee.specification;

import com.example.coffeetica.coffee.models.RoasteryEntity;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class RoasterySpecification {

    public static Specification<RoasteryEntity> filterByAttributes(
            String name,
            String country,
            Integer minFoundingYear,
            Integer maxFoundingYear
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (country != null && !country.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("country"), country));
            }
            if (minFoundingYear != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("foundingYear"), minFoundingYear));
            }
            if (maxFoundingYear != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("foundingYear"), maxFoundingYear));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}