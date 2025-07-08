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
import java.util.Optional;

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

    @Override
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

        // 2. 이메일 파싱
        String email = parseEmailFromIdToken(idToken);

        // 3. 사용자 조회
        Optional<UserEntity> optionalUser = loginRepository.findByEmailAndIsSocialTrue(email);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();

            log.info("Google 로그인 시도: userId={}, email={}, status={}, banUntil={}",
                    user.getUserId(), user.getEmail(), user.getStatus(), user.getBanUntil());

            // 벤 상태 처리
            if (user.getStatus() == Status.BANNED && user.getBanUntil() != null) {
                return LoginResponseDTO.builder()
                        .email(user.getEmail())
                        .status(user.getStatus())
                        .banUntil(user.getBanUntil())
                        .isNew(false)
                        .build();
            }

            // 정상 로그인 처리
            String jwtAccessToken = jwtUtil.createToken(user.getUserId(), user.getEmail(), "USER", 60);
            String jwtRefreshToken = jwtUtil.createToken(user.getUserId(), user.getEmail(), "USER", 60 * 24 * 7);

            return LoginResponseDTO.builder()
                    .email(user.getEmail())
                    .accessToken(jwtAccessToken)
                    .refreshToken(jwtRefreshToken)
                    .status(user.getStatus())
                    .banUntil(user.getBanUntil())
                    .isNew(false)
                    .build();

        } else {
            // 👤 신규 사용자
            log.info("구글 첫 로그인 사용자 - email={}", email);
            return LoginResponseDTO.builder()
                    .email(email)
                    .isNew(true)
                    .build();
        }
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