package org.project.name.online.book.store.repository.book.spec;

import jakarta.persistence.criteria.Predicate;
import org.project.name.online.book.store.model.Book;
import org.project.name.online.book.store.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "price";
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            Double priceFrom = Double.parseDouble(params[0]);
            Double priceTo = Double.parseDouble(params[1]);
            Predicate priceGt = criteriaBuilder.gt(root.get("price"), priceFrom);
            Predicate priceLt = criteriaBuilder.lt(root.get("price"), priceTo);
            Predicate pricePredicate = criteriaBuilder.and(priceGt, priceLt);
            return query.where(pricePredicate).getRestriction();
        };
    }
}
