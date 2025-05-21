package org.beep.sbpp.reviews.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.beep.sbpp.reviews.dto.*;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.beep.sbpp.reviews.entities.ReviewImgEntity;
import org.beep.sbpp.reviews.repository.ReviewImgRepository;
import org.beep.sbpp.reviews.repository.ReviewRepository;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final UserRepository userRepository;

    // 조회 실패시 오류 메세지 수정 필요
    @Override
    public ReviewSimpleDTO getOne(Long reviewId) {
        ReviewDTO reviewDTO = reviewRepository.selectOne(reviewId);

        if (reviewDTO == null) {
            throw new IllegalArgumentException("No data found to get. reviewId: " + reviewId);
        }

        List<ReviewImgDTO> reviewImgDTOList = reviewImgRepository.selectImgAll(reviewId);

        ReviewSimpleDTO.ReviewSimpleDTOBuilder builder = ReviewSimpleDTO.builder()
                .reviewId(reviewDTO.getReviewId())
                .userId(reviewDTO.getUserId())
                .score(reviewDTO.getScore())
                .comment(reviewDTO.getComment())
                .recommendCnt(reviewDTO.getRecommendCnt())
                .regDate(reviewDTO.getRegDate());

        if (reviewImgDTOList != null && !reviewImgDTOList.isEmpty()) {
            builder.image(reviewImgDTOList.get(0));
        }

        return builder.build();
    }


    // 조회 실패시 오류 메세지 수정 필요
    @Override
    public ReviewDetailDTO getOneDetail(Long reviewId) {
        ReviewDTO reviewDTO = reviewRepository.selectOne(reviewId);

        if (reviewDTO == null) {
            throw new IllegalArgumentException("No data found to get. reviewId: " + reviewId);
        }

        List<ReviewImgDTO> reviewImgDTOList = reviewImgRepository.selectImgAll(reviewId);

        ReviewDetailDTO.ReviewDetailDTOBuilder builder = ReviewDetailDTO.builder()
                .reviewId(reviewDTO.getReviewId())
                .userId(reviewDTO.getUserId())
                .score(reviewDTO.getScore())
                .comment(reviewDTO.getComment())
                .recommendCnt(reviewDTO.getRecommendCnt())
                .regDate(reviewDTO.getRegDate());

        if (reviewImgDTOList != null && !reviewImgDTOList.isEmpty()) {
            builder.images(reviewImgDTOList);
        }

        return builder.build();
    }

    // 등록 실패시 오류 메세지 수정 필요
    @Override
    public Long register(ReviewAddDTO reviewAddDTO) {
        UserEntity userEntity = userRepository.findById(reviewAddDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. userId: " + reviewAddDTO.getUserId()));

        ReviewEntity reviewEntity = ReviewEntity.builder()
                .userEntity(userEntity)
                .comment(reviewAddDTO.getComment())
                .score(reviewAddDTO.getScore())
                .recommendCnt(0)
                .isHidden(false)
                .isDelete(false)
                .build();

        Long reviewId =  reviewRepository.save(reviewEntity).getReviewId();
        log.info("New review id: {}", reviewId);

        if (reviewAddDTO.getFiles() != null) {
            MultipartFile[] files =  reviewAddDTO.getFiles();

            for(MultipartFile file: files) {
                String uuid = UUID.randomUUID().toString();

                String saveFileName = uuid +"_" + file.getOriginalFilename();
                String thumbFileName = "s_" + saveFileName;

                File target = new File("C:\\upload\\" + saveFileName );
                File thumbFile = new File("C:\\upload\\" + thumbFileName);

                try {
                    file.transferTo(target);

                    Thumbnails.of(target)
                            .size(200,200)
                            .toFile(thumbFile);

                    ReviewImgEntity reviewImgEntity = ReviewImgEntity.builder()
                            .reviewEntity(reviewEntity)
                            .imgUrl(saveFileName)
                            .build();

                    Long reviewImgId = reviewImgRepository.save(reviewImgEntity).getReviewImgId();;
                    log.info("Saved reviewImageFiles: {}", reviewImgId);
                } catch (Exception e) {
                    log.info(e.getMessage());
                }
            }
        }

        return reviewId;
    }

    // 수정 실패시 오류 메세지 수정 필요
    @Override
    public Long modify(Long reviewId, ReviewModifyDTO reviewModifyDTO) {
        ReviewEntity reviewEntity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. reviewId: " + reviewId));

        String comment = reviewModifyDTO.getComment();
        Integer score = reviewModifyDTO.getScore();

        int result = reviewRepository.updateOne(reviewId, comment, score);

        if (result <= 0) {
            throw new IllegalArgumentException("Failed to modify. reviewId: " + reviewId);
        }

        log.info("Modified Review: id={} comment='{}' score={}", reviewId, comment, score);

        List<Long> deleteImgIds = reviewModifyDTO.getDeleteImgIds();
        List<String> newImgUrls = reviewModifyDTO.getNewImgUrls();

        if (deleteImgIds != null && !deleteImgIds.isEmpty()) {
            for (Long imgId : deleteImgIds) {
                reviewImgRepository.deleteById(imgId);
                log.info("Deleted ReviewImg: id={}", imgId);
            }
        }

        if (newImgUrls != null && !newImgUrls.isEmpty()) {
            for (String url : newImgUrls) {
                ReviewImgEntity reviewImgEntity = ReviewImgEntity.builder()
                        .reviewEntity(reviewEntity)
                        .imgUrl(url)
                        .build();
                ReviewImgEntity saved = reviewImgRepository.save(reviewImgEntity);
                log.info("Added ReviewImg: id={} url={}", saved.getReviewImgId(), saved.getImgUrl());
            }
        }

        return reviewId;
    }

    // 삭제 실패시 오류 메시지 수정 필요
    @Override
    public Long delete(Long reviewId) {
        List<ReviewImgEntity> reviewImgEntities = reviewImgRepository.findAllByReviewEntity_ReviewId(reviewId);

        if (!reviewImgEntities.isEmpty()) {
            reviewImgRepository.deleteAll(reviewImgEntities);
            log.info("Deleted {} image(s) for reviewId={}", reviewImgEntities.size(), reviewId);
        }

        int result = reviewRepository.deleteOne(reviewId);

        if (result > 0) {
            return reviewId;
        } else {
            throw new IllegalArgumentException("Failed to delete review. reviewId: " + reviewId);
        }
    }
}