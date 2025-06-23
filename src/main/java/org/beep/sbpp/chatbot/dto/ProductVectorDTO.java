package org.beep.sbpp.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVectorDTO {

    private Long productId;
    private String name;
    private String description;
    private String category;
    private String mainTag;
    private String allergens;
    private String barcode;
    private String imgUrl;

}
