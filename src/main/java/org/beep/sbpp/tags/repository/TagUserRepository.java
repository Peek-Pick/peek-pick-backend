package org.beep.sbpp.tags.repository;

import org.beep.sbpp.tags.entities.TagUserEntity;
import org.beep.sbpp.tags.enums.TagName;
import org.beep.sbpp.users.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagUserRepository extends JpaRepository<TagUserEntity, Long> {

    List<TagUserEntity> findByUserUserId(Long userId);

    void deleteByUserId(Long userId);

//    @Query("""
//        select tu.tag.tagName
//        from TagUserEntity tu
//        where tu.user.userId = :userId
//    """)
//    List<TagName> findTagNamesByUserId(@Param("userId") Long userId);

}
