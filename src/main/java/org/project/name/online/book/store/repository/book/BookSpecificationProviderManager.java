package org.project.name.online.book.store.repository.book;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.project.name.online.book.store.exception.SpecificationProviderException;
import org.project.name.online.book.store.model.Book;
import org.project.name.online.book.store.repository.SpecificationProvider;
import org.project.name.online.book.store.repository.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private final List<SpecificationProvider<Book>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(provider -> provider.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new SpecificationProviderException(
                        "Cannot find correct specification provider for key" + key));
    }
}
