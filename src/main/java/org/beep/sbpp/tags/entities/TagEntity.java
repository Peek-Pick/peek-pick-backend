package org.beep.sbpp.tags.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.tags.enums.TagCategoryName;
import org.beep.sbpp.tags.enums.TagName;

@Entity
@Table(name = "tbl_tag")
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long tagId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag_name", unique = true, nullable = false)
    private TagName tagName;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private TagCategoryName category;


}
