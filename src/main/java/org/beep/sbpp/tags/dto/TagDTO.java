package org.beep.sbpp.tags.dto;

import lombok.Data;
import org.beep.sbpp.tags.entities.TagEntity;

@Data
public class TagDTO {

    private String tagName;
    private String category;

    public TagDTO(TagEntity entity) {
        this.tagName = entity.getTagName().name();
        this.category = entity.getCategory().name();
    }
}
