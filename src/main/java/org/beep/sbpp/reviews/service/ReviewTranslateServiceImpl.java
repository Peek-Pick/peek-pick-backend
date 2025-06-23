package org.beep.sbpp.reviews.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.beep.sbpp.reviews.repository.ReviewRepository;
import org.beep.sbpp.users.entities.UserProfileEntity;
import org.beep.sbpp.users.enums.Nationality;
import org.beep.sbpp.users.repository.UserProfileRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@Transactional
public class ReviewTranslateServiceImpl implements ReviewTranslateService {
    private final ReviewRepository reviewRepository;
    private final UserProfileRepository userProfileRepository;
    private final ChatClient chatClient;

    public ReviewTranslateServiceImpl(ReviewRepository reviewRepository,
                                      UserProfileRepository userProfileRepository,
                                      ChatClient.Builder chatClientBuilder) {
        this.reviewRepository = reviewRepository;
        this.userProfileRepository = userProfileRepository;
        this.chatClient = chatClientBuilder
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
    }

    @Override
    public String translate(Long userId, Long reviewId) {

        // 유저 프로필 조회
        UserProfileEntity userProfileEntity = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. userProfileId: " + userId));

        // 리뷰 존재 확인
        ReviewEntity reviewEntity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("No data found to get. reviewId: " + reviewId));

        // 국적, 리뷰
        Nationality nationality = userProfileEntity.getNationality();
        String comment = reviewEntity.getComment();

        // gpt 버전 옵션으로 명시
        OpenAiChatOptions options = new OpenAiChatOptions();
        options.setModel("gpt-3.5-turbo");

        String promptMessage = """
            The following is a user-written review. Please translate it into {lang} in a **natural and fluent way**.
            
            - If the review contains profanity or inappropriate expressions, that’s okay.
            - **Please soften or rephrase such expressions appropriately to suit the target language and culture.**
            - **Do not include any explanations or system messages.** Only return the translated sentence.
            
            {review}
        """;

        // 리뷰 요약 프롬프트
        PromptTemplate template = new PromptTemplate(promptMessage);
        Prompt prompt = template.create(Map.of(
                "lang", nationality,
                "review", comment
        ), options);

        // chatClient 호출 (String 반환)
        return chatClient.prompt(prompt).call().content();
    }
}
