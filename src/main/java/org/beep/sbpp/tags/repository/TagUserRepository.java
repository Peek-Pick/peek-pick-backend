package org.beep.sbpp.tags.repository;

import org.beep.sbpp.tags.entities.TagUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagUserRepository extends JpaRepository<TagUserEntity, Long> {

    List<TagUserEntity> findByUserUserId(Long userId);

    void deleteByUser_UserId(Long userId);

//    @Query("""
//        select tu.tag.tagName
//        from TagUserEntity tu
//        where tu.user.userId = :userId
//    """)
//    List<TagName> findTagNamesByUserId(@Param("userId") Long userId);

}
