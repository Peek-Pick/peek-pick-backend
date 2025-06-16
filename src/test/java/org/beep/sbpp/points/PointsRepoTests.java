package org.beep.sbpp.points;

import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.points.dto.PointLogsDTO;
import org.beep.sbpp.admin.points.dto.PointStoreListDTO;
import org.beep.sbpp.points.entities.PointStoreEntity;
import org.beep.sbpp.points.repository.PointLogsRepository;
import org.beep.sbpp.admin.points.repository.AdminPointRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import static org.beep.sbpp.points.enums.PointProductType.CU;

@SpringBootTest
@Slf4j
@TestPropertySource(properties = {
        "GOOGLE_CLIENT_ID=test-client-id",
        "GOOGLE_CLIENT_SECRET=test-secret",
        "GOOGLE_REDIRECT_URI=http://localhost:8080/oauth2/callback"
})
public class PointsRepoTests {

    @Autowired(required = false)
    AdminPointRepository storeRepository;

    @Autowired(required = false)
    PointLogsRepository pointLogsRepository;

    @Test
    public void insertStore() {

        for(int i = 1; i <= 20; i++) {
            PointStoreEntity store = PointStoreEntity.builder()
                    .item("Product" + i)
                    .price(5000)
                    .description("테스트용 상품입니다. 상품 설명은 추후 업데이트 예정입니다.")
                    .productType(CU)
                    .build();

            storeRepository.save(store);
        }//end for
    }

    @Test
    public void listStore() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("pointstoreId").descending());

        Page<PointStoreListDTO> result = storeRepository.list(pageable);

        result.forEach(arr -> log.info(arr.toString()));
    }

    @Test
    public void readStore() {
        PointStoreEntity result = storeRepository.selectOne(13L);

        log.info(result.toString());
    }

    @Test
    public void modifyStore() {
        PointStoreEntity product = storeRepository.selectOne(5L);

        product.changePrice(3000);

        storeRepository.save(product);
    }

    @Test
    public void deleteStore() {
        PointStoreEntity product = storeRepository.selectOne(4L);

        product.softDelete();

        storeRepository.delete(product);
    }

    @Test
    public void pointLogs() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("pointLogId").descending());

        Page<PointLogsDTO> result = pointLogsRepository.pointLogsList(10L, pageable);

        result.forEach(arr -> log.info(arr.toString()));
    }

}
