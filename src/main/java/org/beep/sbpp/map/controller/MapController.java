package org.beep.sbpp.map.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1/map")
@Slf4j
@RequiredArgsConstructor
public class MapController {

    // ORS API 키
    @Value("${ors.api.key}")
    private String orsApiKey;



    //길찾기 정보 보내기
    @GetMapping("/directions")
    public ResponseEntity<String> getDirections( //출발지(위도, 경도), 도착지(위도, 경도)
            @RequestParam double startLat,
            @RequestParam double startLng,
            @RequestParam double endLat,
            @RequestParam double endLng) {

        // ORS API Endpoint (도보 경로)
        String url = "https://api.openrouteservice.org/v2/directions/foot-walking";

        // headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", orsApiKey); // 인증 키
        headers.setContentType(MediaType.APPLICATION_JSON); // 요청 본문 타입

        // body (GeoJSON 형식)
        String body = String.format("""
            {
                "coordinates": [
                    [%f, %f],
                    [%f, %f]
                ]
            }
        """, startLng, startLat, endLng, endLat); // [longitude, latitude] 순서

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        // ORS API POST 호출
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return ResponseEntity.ok(response.getBody());
    }
}
