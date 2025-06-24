// 📄 src/main/java/org/beep/sbpp/products/service/ProductSearchService.java
package org.beep.sbpp.products.service;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.products.document.ProductSearchDocument;
import org.beep.sbpp.products.repository.ProductSearchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductSearchRepository searchRepository;

    public void save(ProductSearchDocument doc) {
        searchRepository.save(doc);
    }

    public List<ProductSearchDocument> findAll() {
        return StreamSupport.stream(searchRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}
