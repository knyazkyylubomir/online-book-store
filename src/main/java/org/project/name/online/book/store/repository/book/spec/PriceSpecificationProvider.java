package org.project.name.online.book.store.repository.book.spec;

import jakarta.persistence.criteria.Predicate;
import org.project.name.online.book.store.model.Book;
import org.project.name.online.book.store.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    private static final int PRICE_FROM_INDEX = 0;
    private static final int PRICE_TO_INDEX = 1;

    @Override
    public String getKey() {
        return "price";
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            Double priceFrom = Double.parseDouble(params[PRICE_FROM_INDEX]);
            Double priceTo = Double.parseDouble(params[PRICE_TO_INDEX]);
            Predicate priceGt = criteriaBuilder.greaterThanOrEqualTo(root.get("price"), priceFrom);
            Predicate priceLt = criteriaBuilder.lessThanOrEqualTo(root.get("price"), priceTo);
            Predicate pricePredicate = criteriaBuilder.and(priceGt, priceLt);
            return query.where(pricePredicate).getRestriction();
        };
    }
}
