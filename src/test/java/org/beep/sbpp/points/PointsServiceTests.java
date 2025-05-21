package org.beep.sbpp.points;

import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.points.dto.PointStoreAddDTO;
import org.beep.sbpp.points.dto.PointStoreDTO;
import org.beep.sbpp.points.dto.PointStoreListDTO;
import org.beep.sbpp.points.enums.PointProductType;
import org.beep.sbpp.points.service.PointStoreService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@SpringBootTest
@Slf4j
public class PointsServiceTests {

    @Autowired
    private PointStoreService service;

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

        Page<PointStoreListDTO> dtos = service.list(pageable);

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
}
