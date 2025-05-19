package org.beep.sbpp.reviews.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.reviews.dto.ReviewImgDTO;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.beep.sbpp.reviews.entities.ReviewImgEntity;
import org.beep.sbpp.reviews.repository.ReviewImgRepository;
import org.beep.sbpp.reviews.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewImgServiceImpl implements ReviewImgService {
    private final ReviewImgRepository reviewImgRepository;
    private final ReviewRepository reviewRepository;

    // 조회 실패시 오류 메시지 수정 필요
    @Override
    public ReviewImgDTO getImgOne(Long reviewId) {
        List<ReviewImgDTO> reviewImgDTOS = reviewImgRepository.selectImgAll(reviewId);

        if (reviewImgDTOS != null && !reviewImgDTOS.isEmpty()) {
            Optional<ReviewImgDTO> reviewImgDTO = reviewImgDTOS.stream().findFirst();
            return reviewImgDTO.get();
        } else {
            throw new IllegalArgumentException("No data found to getImg. reviewId: " + reviewId);
        }
    }

    // 조회 실패시 오류 메시지 수정 필요
    @Override
    public List<ReviewImgDTO> getImgAll(Long reviewId) {
        List<ReviewImgDTO> reviewImgDTOS = reviewImgRepository.selectImgAll(reviewId);

        if (reviewImgDTOS != null) {
            return reviewImgDTOS;
        } else {
            throw new IllegalArgumentException("No data found to getImg. reviewId: " + reviewId);
        }
    }

    // 등록 실패시 오류 메시지 수정 필요
    @Override
    public Long registerImg(ReviewImgDTO reviewImgDTO) {
        ReviewEntity reviewEntity = reviewRepository.findById(reviewImgDTO.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No data found to findById. reviewId: " + reviewImgDTO.getReviewId()));

        ReviewImgEntity reviewImgEntity = ReviewImgEntity.builder()
                .reviewEntity(reviewEntity)
                .imgUrl(reviewImgDTO.getImgUrl())
                .build();

        ReviewImgEntity saved = reviewImgRepository.save(reviewImgEntity);

        return saved.getReviewImgId();
    }

    // 삭제 실패시 오류 메시지 수정 필요
    @Override
    public Long deleteImg(Long reviewImgId) {
        Optional<ReviewImgEntity> optional = reviewImgRepository.findById(reviewImgId);

        if (optional.isPresent()) {
            reviewImgRepository.deleteById(reviewImgId);
            return reviewImgId;
        } else {
            throw new IllegalArgumentException("No data found to deleteImg. reviewImgId: " + reviewImgId);
        }
    }

    @Override
    public Long modifyImg(Long reviewId, List<Long> deleteImgIds, List<String> newImgUrls) {
        if (deleteImgIds != null) {
            for (Long imgId : deleteImgIds) {
                deleteImg(imgId);
            }
        }

        if (newImgUrls != null) {
            for (String url : newImgUrls) {
                ReviewImgDTO dto = ReviewImgDTO.builder()
                        .reviewId(reviewId)
                        .imgUrl(url)
                        .build();
                
                registerImg(dto);
            }
        }

        return reviewId;
    }
}