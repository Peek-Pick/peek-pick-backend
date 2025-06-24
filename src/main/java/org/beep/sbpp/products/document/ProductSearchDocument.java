// 📄 src/main/java/org/beep/sbpp/products/document/ProductSearchDocument.java
package org.beep.sbpp.products.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "products")
public class ProductSearchDocument {

    @Id
    private String id;

    private String name;
    private String description;
    private String brand;
    private String barcode;
    private String category;
}
