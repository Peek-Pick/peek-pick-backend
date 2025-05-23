package org.beep.sbpp.tags.controller;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.tags.dto.TagDTO;
import org.beep.sbpp.tags.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagDTO>> getAllTags() {

        List<TagDTO> tags = tagService.getAllTagNames();

        return ResponseEntity.ok(tags);
    }
}
