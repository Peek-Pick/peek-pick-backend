package org.beep.sbpp.tags.repository;

import org.beep.sbpp.tags.entities.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TagRepository extends JpaRepository<TagEntity, Long> {
}
