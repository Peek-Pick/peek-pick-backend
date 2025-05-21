package org.beep.sbpp.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.auth.dto.LoginResponseDTO;
import org.beep.sbpp.auth.repository.LoginRepository;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.enums.Status;
import org.beep.sbpp.util.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.client.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final LoginRepository loginRepository;
    private final JWTUtil jwtUtil;  // JWTUtil 주입 추가

    public LoginResponseDTO handleGoogleLogin(String code) {
        // 1. 구글 토큰 요청
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token",
                request,
                JsonNode.class
        );

        String idToken = response.getBody().get("id_token").asText();
        String accessTokenFromGoogle = response.getBody().get("access_token").asText();

        // 2. 이메일 파싱
        String email = parseEmailFromIdToken(idToken);

        // 3. 사용자 조회 또는 자동 회원가입
        UserEntity user = loginRepository.findByEmailAndIsSocialTrue(email)
                .orElseGet(() -> {
                    UserEntity newUser = UserEntity.builder()
                            .email(email)
                            .password(null) // 소셜 로그인 비밀번호 없음
                            .isSocial(true)
                            .isAdmin(false)
                            .status(Status.ACTIVE)
                            .build();
                    return loginRepository.save(newUser);
                });

        log.info("Google 로그인 성공: userId={}, email={}", user.getUserId(), user.getEmail());

        // 5. JWT 토큰 생성 (user 정보를 기반으로)
        String jwtAccessToken = jwtUtil.createToken(user.getUserId(), user.getEmail(), 2); // 60분 유효
        String jwtRefreshToken = jwtUtil.createToken(user.getUserId(), user.getEmail(), 60 * 24 * 7); // 7일 유효

        // 6. (선택) 구글 유저정보 API 호출 - accessToken 사용
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessTokenFromGoogle);
        HttpEntity<Void> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        try {
            ResponseEntity<JsonNode> userInfoResponse = restTemplate.exchange(
                    "https://www.googleapis.com/oauth2/v2/userinfo",
                    HttpMethod.GET,
                    userInfoRequest,
                    JsonNode.class
            );
            JsonNode userInfo = userInfoResponse.getBody();
            log.info("Google 사용자 이름: {}", userInfo.get("name").asText());
        } catch (Exception e) {
            log.warn("구글 사용자 정보 요청 실패: {}", e.getMessage());
        }

        return new LoginResponseDTO(jwtAccessToken, jwtRefreshToken);
    }

    private String parseEmailFromIdToken(String idToken) {
        try {
            String[] chunks = idToken.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(payload).get("email").asText();
        } catch (Exception e) {
            throw new RuntimeException("Invalid id_token", e);
        }
    }
}
