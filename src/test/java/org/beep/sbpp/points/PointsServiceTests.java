package org.beep.sbpp.points;

import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.points.dto.PointLogsDTO;
import org.beep.sbpp.admin.points.dto.PointStoreAddDTO;
import org.beep.sbpp.admin.points.dto.PointStoreDTO;
import org.beep.sbpp.admin.points.dto.PointStoreListDTO;
import org.beep.sbpp.points.enums.PointProductType;
import org.beep.sbpp.points.service.PointService;
import org.beep.sbpp.admin.points.service.AdminPointService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Slf4j
@TestPropertySource(properties = {
        "GOOGLE_CLIENT_ID=test-client-id",
        "GOOGLE_CLIENT_SECRET=test-secret",
        "GOOGLE_REDIRECT_URI=http://localhost:8080/oauth2/callback",
        "JWT_SECRET=1232131334554434343424242Ts",
        "ORS_API_KEY=5b3ce3cd95264ab486685303274a065cc46"
})
public class PointsServiceTests {

    @Autowired
    private AdminPointService service;

    @Autowired
    private PointService pointService;

    @Test
    public void addPointStore() {

        PointStoreAddDTO dto = new PointStoreAddDTO();
        dto.setItem("Test");
        dto.setDescription("Test");
        dto.setPrice(1000);
        dto.setProductType(PointProductType.CU);

        Long id = service.add(dto);

        log.info(id.toString());
    }

    @Test
    public void readPointStore() {

        PointStoreDTO dto = service.read(11L);
        log.info(dto.toString());
    }

    @Test
    public void listPointStore() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("pointstoreId").descending());
        String category = "item";
        String keyword = "cu";
        Boolean hidden = false;

        Page<PointStoreListDTO> dtos = service.list(pageable, category, keyword, hidden);

        dtos.forEach(arr -> log.info(arr.toString()));
    }

    @Test
    public void updatePointStore() {
        PointStoreAddDTO dto = new PointStoreAddDTO();
        dto.setPointstoreId(11L);
        dto.setItem("Test");
        dto.setDescription("Test");
        dto.setPrice(2000);
        dto.setProductType(PointProductType.CU);

        service.modify(dto);
    }

    @Test
    public void removePointStore() {
        service.delete(11L);
    }

    @Test
    public void redeemPoint() {

        int remaining = pointService.redeemPoints(1L, 2L);
        log.info("남은 포인트: {}", remaining);

    }

    @Test
    public void pointLogsList() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("pointLogId").descending());

        Page<PointLogsDTO> dtos = pointService.pointLogsList(10L, pageable);

        dtos.forEach(arr -> log.info(arr.toString()));

    }


}
