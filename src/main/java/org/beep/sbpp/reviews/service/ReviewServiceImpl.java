package org.beep.sbpp.reviews.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.beep.sbpp.reviews.dto.ReviewDTO;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.beep.sbpp.reviews.repository.ReviewRepository;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository repository;

    // 조회 실패시 오류 메세지 수정 필요
    @Override
    public ReviewDTO getOne(Long reviewId) {
        ReviewDTO reviewDTO = repository.selectOne(reviewId);

        if (reviewDTO != null) {
            return reviewDTO;
        } else {
            throw new IllegalArgumentException("No data found to get. reviewId: " + reviewId);
        }
    }

    // 등록 실패시 오류 메세지 수정 필요
    @Override
    public Long register(ReviewDTO reviewDTO) {
        ReviewEntity entity = ReviewEntity.builder()
                .comment(reviewDTO.getComment())
                .score(reviewDTO.getScore())
                .recommendCnt(0)
                .isHidden(false)
                .isDelete(false)
                .build();

        return repository.save(entity).getReviewId();
    }

    // 수정 실패시 오류 메세지 수정 필요
    @Override
    public Long modify(Long reviewId, String comment, Integer score) {
        int result = repository.updateOne(reviewId, comment, score);

        if (result > 0) {
            return reviewId;
        }
        else {
            throw new IllegalArgumentException("No data found to modify. reviewId: " + reviewId);
        }
    }

    // 삭제 실패시 오류 메시지 수정 필요
    @Override
    public Long delete(Long reviewId) {
        int result = repository.deleteOne(reviewId);

        if (result > 0) {
            return reviewId;
        } else {
            throw new IllegalArgumentException("No data found to delete. reviewId: " + reviewId);
        }
    }
}