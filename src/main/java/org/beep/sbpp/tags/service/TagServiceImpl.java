package org.beep.sbpp.tags.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.tags.dto.TagDTO;
import org.beep.sbpp.tags.entities.TagEntity;
import org.beep.sbpp.tags.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<TagDTO> getAllTagNames() {

        log.info("***** getAllTagNames *****");

        List<TagEntity> entities = tagRepository.findAll();

        return entities.stream().map(TagDTO::new).toList();
    }
}
