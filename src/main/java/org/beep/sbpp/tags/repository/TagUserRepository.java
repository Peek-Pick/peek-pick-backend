package org.beep.sbpp.tags.repository;

import org.beep.sbpp.tags.entities.TagUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagUserRepository extends JpaRepository<TagUserEntity, Long> {

    @Query("SELECT tu FROM TagUserEntity tu WHERE tu.user.userId = :userId")
    List<TagUserEntity> findByUserUserId(@Param("userId") Long userId);

    void deleteByUser_UserId(Long userId);

//    @Query("""
//        select tu.tag.tagName
//        from TagUserEntity tu
//        where tu.user.userId = :userId
//    """)
//    List<TagName> findTagNamesByUserId(@Param("userId") Long userId);

}
