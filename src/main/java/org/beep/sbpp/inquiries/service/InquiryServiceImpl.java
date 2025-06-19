package org.beep.sbpp.inquiries.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Page<InquiryResponseDTO> getInquiryListByUser(Long uid, Pageable pageable) {
        return inquiryRepository
                .findByUserEntity_UserIdAndIsDeleteFalse(uid, pageable)
                .map(this::toDto);
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

        // 삭제 여부 필터
        if (!includeDeleted) {
            spec = spec.and((root, query, cb) -> cb.isFalse(root.get("isDelete")));
        }

        // 상태 필터 (e.g. WAITING, COMPLETED)
        if (!status.isEmpty()) {
            try {
                InquiryStatus inquiryStatus = InquiryStatus.valueOf(status.toUpperCase());
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), inquiryStatus));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("잘못된 문의 상태값입니다: " + status);
            }
        }

        // 키워드 & 카테고리 필터
        if (!keyword.isBlank()) {
            switch (category) {
                case "all": {
                    List<Specification<Inquiry>> orSpecs = new ArrayList<>();

                    // 내용 필터
                    orSpecs.add((root, query, cb) ->
                            cb.like(root.get("content"), "%" + keyword + "%"));

                    // 작성자 닉네임 필터
                    List<Long> matchedUserIds = userProfileRepository.findByNicknameContaining(keyword)
                            .stream().map(UserProfileEntity::getUserId).toList();
                    if (!matchedUserIds.isEmpty()) {
                        orSpecs.add((root, query, cb) ->
                                root.get("userEntity").get("userId").in(matchedUserIds));
                    }

                    // 문의 ID 필터
                    try {
                        Long inquiryId = Long.parseLong(keyword);
                        orSpecs.add((root, query, cb) ->
                                cb.equal(root.get("inquiryId"), inquiryId));
                    } catch (NumberFormatException ignored) {}

                    // or 조건들 병합
                    spec = spec.and(orSpecs.stream()
                            .reduce(Specification::or)
                            .orElse((root, query, cb) -> cb.disjunction()));

                    break;
                }

                case "content":
                    spec = spec.and((root, query, cb) ->
                            cb.like(root.get("content"), "%" + keyword + "%"));
                    break;

                case "writer": {
                    List<Long> matchedUserIds = userProfileRepository.findByNicknameContaining(keyword)
                            .stream().map(UserProfileEntity::getUserId).toList();

                    if (!matchedUserIds.isEmpty()) {
                        spec = spec.and((root, query, cb) ->
                                root.get("userEntity").get("userId").in(matchedUserIds));
                    } else {
                        // 일치하는 닉네임 없음 → 결과 없음
                        spec = spec.and((root, query, cb) -> cb.disjunction());
                    }
                    break;
                }

                case "inquiryId":
                    try {
                        Long inquiryId = Long.parseLong(keyword);
                        spec = spec.and((root, query, cb) ->
                                cb.equal(root.get("inquiryId"), inquiryId));
                    } catch (NumberFormatException e) {
                        spec = spec.and((root, query, cb) -> cb.disjunction());
                    }
                    break;

                default:
                    break;
            }
        }

        return inquiryRepository.findAll(spec, pageable)
                .map(this::toDto);
    }

}