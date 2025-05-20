package org.beep.sbpp.tags.repository;

import org.beep.sbpp.tags.entities.TagUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagUserRepository extends JpaRepository<TagUserEntity, Long> {

    List<TagUserEntity> findByUser_UserId(Long userId);
}
