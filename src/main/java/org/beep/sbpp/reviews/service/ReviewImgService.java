package org.beep.sbpp.reviews.service;

import org.beep.sbpp.reviews.dto.ReviewImgDTO;

import java.util.List;

public interface ReviewImgService {
    ReviewImgDTO getImgOne(Long reviewId);

    List<ReviewImgDTO> getImgAll(Long reviewId);

    Long registerImg(ReviewImgDTO reviewImgDTO);

    Long deleteImg(Long reviewImgId);

    Long modifyImg(Long reviewId, List<Long> deleteImgIds, List<String> newImgUrls);
}