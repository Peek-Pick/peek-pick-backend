package org.beep.sbpp.tags.service;

import org.beep.sbpp.tags.dto.TagDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface TagService {

    List<TagDTO> getAllTagNames();
}
