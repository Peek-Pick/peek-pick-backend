// 📄 src/main/java/org/beep/sbpp/products/repository/ProductSearchRepository.java
package org.beep.sbpp.products.repository;

import org.beep.sbpp.products.document.ProductSearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductSearchDocument, String> {
}
