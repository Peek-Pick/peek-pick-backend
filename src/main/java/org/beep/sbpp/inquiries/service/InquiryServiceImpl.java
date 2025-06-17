package org.beep.sbpp.inquiries.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.beep.sbpp.inquiries.controller.InquiryNotFoundException;
import org.beep.sbpp.inquiries.dto.InquiryRequestDTO;
import org.beep.sbpp.inquiries.dto.InquiryResponseDTO;
import org.beep.sbpp.inquiries.entities.Inquiry;
import org.beep.sbpp.inquiries.entities.InquiryImage;
import org.beep.sbpp.inquiries.enums.InquiryStatus;
import org.beep.sbpp.inquiries.repository.InquiryRepository;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.entities.UserProfileEntity;
import org.beep.sbpp.users.repository.UserProfileRepository;
import org.beep.sbpp.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    private InquiryResponseDTO toDto(Inquiry n) {

        Long uid = n.getUserEntity().getUserId();

        UserProfileEntity userProfile = userProfileRepository.findByUserId(uid)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        return InquiryResponseDTO.builder()
                .inquiryId(n.getInquiryId())
                .userId(uid)
                .userNickname(userProfile.getNickname())
                .userProfileImgUrl(userProfile.getProfileImgUrl())
                .title(n.getTitle())
                .content(n.getContent())
                .type(n.getType())
                .status(n.getStatus())
                .isDelete(n.getIsDelete())
                .regDate(n.getRegDate())
                .modDate(n.getModDate())
                .imgUrls(n.getImages().stream()
                        .map(InquiryImage::getImgUrl)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public InquiryResponseDTO createInquiry(InquiryRequestDTO dto, Long uid) {
        UserEntity user = userRepository.findById(uid)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        Inquiry inquiry = Inquiry.builder()
                .userEntity(user)
                .type(dto.getType())
                .title(dto.getTitle())
                .content(dto.getContent())
                .status(InquiryStatus.PENDING)
                .isDelete(false)
                .build();

        inquiryRepository.save(inquiry);
        return toDto(inquiry);
    }

    @Override
    public InquiryResponseDTO updateInquiry(Long id, InquiryRequestDTO dto, Long uid) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의 없음: " + id));

        if (!inquiry.getUserEntity().getUserId().equals(uid) || inquiry.getIsDelete()) {
            throw new RuntimeException("권한이 없습니다.");
        }

        inquiry.setTitle(dto.getTitle());
        inquiry.setContent(dto.getContent());
        inquiry.setType(dto.getType());
        inquiry.setModDate(LocalDateTime.now());

        inquiryRepository.save(inquiry);
        return toDto(inquiry);
    }

    @Override
    public void deleteInquiry(Long id, Long uid) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의 없음: " + id));

        if (!inquiry.getUserEntity().getUserId().equals(uid) || inquiry.getIsDelete()) {
            throw new RuntimeException("권한이 없습니다.");
        }

        inquiry.setIsDelete(true);      // soft delete
        inquiry.setModDate(LocalDateTime.now());

        inquiryRepository.save(inquiry);
    }

    @Override
    @Transactional(readOnly = true)
    public InquiryResponseDTO getInquiry(Long id, Long uid) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new InquiryNotFoundException(id));

        if (!inquiry.getUserEntity().getUserId().equals(uid) || inquiry.getIsDelete()) {
            throw new RuntimeException("권한이 없습니다.");
        }

        return toDto(inquiry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InquiryResponseDTO> getInquiryList(Pageable pageable) {
        return inquiryRepository
                .findByIsDeleteFalse(pageable)
                .map(this::toDto);
    }

    @Override
    public void addImageUrls(Long inquiryId, Long uid, List<String> urls) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new InquiryNotFoundException(inquiryId));

        if (!inquiry.getUserEntity().getUserId().equals(uid) || inquiry.getIsDelete()) {
            throw new RuntimeException("권한이 없습니다.");
        }

        // 기존 URL은 유지, 신규 URL만 추가
        List<String> existing = inquiry.getImages().stream()
                .map(InquiryImage::getImgUrl)
                .collect(Collectors.toList());

        boolean modified = false;
        for (String url : urls) {
            if (!existing.contains(url)) {
                InquiryImage img = InquiryImage.builder()
                        .imgUrl(url)
                        .build();
                inquiry.addImage(img);
                modified = true;
            }
        }

        if (modified) {
            inquiry.setModDate(LocalDateTime.now());  // 수정일 업데이트
        }

        inquiryRepository.save(inquiry);
    }

    @Override
    public void deleteImages(Long inquiryId, Long uid, List<String> urls) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new InquiryNotFoundException(inquiryId));

        if (!inquiry.getUserEntity().getUserId().equals(uid) || inquiry.getIsDelete()) {
            throw new RuntimeException("권한이 없습니다.");
        }

        List<InquiryImage> imagesToRemove = inquiry.getImages().stream()
                .filter(img -> urls.contains(img.getImgUrl()))
                .collect(Collectors.toList());

        if (!imagesToRemove.isEmpty()) {
            imagesToRemove.forEach(img -> {
                inquiry.getImages().remove(img);
                img.setInquiry(null); // orphanRemoval=true 이므로 삭제됨
            });

            inquiry.setModDate(LocalDateTime.now());  // 수정일 업데이트
        }

        inquiryRepository.save(inquiry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InquiryResponseDTO> getFilteredInquiries(
            boolean includeDeleted,
            String category,
            String keyword,
            String status,
            Pageable pageable
    ) {
        Specification<Inquiry> spec = Specification.where(null);

        // 1) 삭제 포함 여부
        if (!includeDeleted) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isDelete"), false));
        }

        // 2) 상태 필터
        if (!status.isEmpty()) {
            try {
                InquiryStatus st = InquiryStatus.valueOf(status.toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), st));
            } catch (IllegalArgumentException ex) {
                try {
                    throw new BadRequestException("잘못된 상태 값입니다: " + status);
                } catch (BadRequestException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // 3) 키워드/카테고리 필터
        if (!keyword.isBlank()) {
            switch (category) {
                case "all":
                    // 1) 제목 + 본문
                    Specification<Inquiry> titleSpec   = (root, query, cb) ->
                            cb.like(root.get("title"), "%" + keyword + "%");
                    Specification<Inquiry> contentSpec = (root, query, cb) ->
                            cb.like(root.get("content"), "%" + keyword + "%");

                    // 2) 작성자 닉네임 매칭 userId 리스트
                    List<Long> uids = userProfileRepository
                            .findByNicknameContaining(keyword)
                            .stream()
                            .map(UserProfileEntity::getUserId)
                            .toList();
                    Specification<Inquiry> writerSpec = null;
                    if (!uids.isEmpty()) {
                        writerSpec = (root, query, cb) ->
                                root.get("userEntity").get("userId").in(uids);
                    }

                    // 3) 문의번호 (숫자일 때만)
                    Specification<Inquiry> idSpec = null;
                    try {
                        Long idVal = Long.valueOf(keyword);
                        idSpec = (root, query, cb) ->
                                cb.equal(root.get("inquiryId"), idVal);
                    } catch (NumberFormatException ignored) {}

                    // 4) OR 조합
                    List<Specification<Inquiry>> ors = new ArrayList<>();
                    ors.add(titleSpec);
                    ors.add(contentSpec);
                    if (writerSpec != null) ors.add(writerSpec);
                    if (idSpec     != null) ors.add(idSpec);

                    spec = spec.and(ors.stream()
                            .reduce(Specification::or)
                            .orElse((root, q, cb) -> cb.disjunction())
                    );
                    break;

                case "title":
                    spec = spec.and((root, query, cb) ->
                            cb.like(root.get("title"), "%" + keyword + "%")
                    );
                    break;

                case "titleContent":
                    spec = spec.and((root, query, cb) ->
                            cb.or(
                                    cb.like(root.get("title"), "%" + keyword + "%"),
                                    cb.like(root.get("content"), "%" + keyword + "%")
                            )
                    );
                    break;

                case "writer":
                    List<Long> matchedUids = userProfileRepository
                            .findByNicknameContaining(keyword)
                            .stream()
                            .map(UserProfileEntity::getUserId)
                            .toList();

                    if (!matchedUids.isEmpty()) {
                        spec = spec.and((root, query, cb) ->
                                root.get("userEntity").get("userId").in(matchedUids)
                        );
                    } else {
                        spec = spec.and((root, query, cb) -> cb.disjunction());
                    }
                    break;

                case "inquiryId":
                    try {
                        Long idVal = Long.valueOf(keyword);
                        spec = spec.and((root, query, cb) ->
                                cb.equal(root.get("inquiryId"), idVal)
                        );
                    } catch (NumberFormatException ex) {
                        spec = spec.and((root, query, cb) -> cb.disjunction());
                    }
                    break;

                default:
                    // do nothing
            }
        }

        return inquiryRepository.findAll(spec, pageable)
                .map(this::toDto);
    }
}