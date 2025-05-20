package org.beep.sbpp.tags.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.users.entities.UserEntity;

@Entity
@Table(name = "tbl_user_tag")
@ToString(exclude = {"tag", "user"})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_tag_id")
    private Long userTagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private TagEntity tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
