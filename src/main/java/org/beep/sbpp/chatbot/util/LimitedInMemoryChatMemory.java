package org.beep.sbpp.chatbot.util;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.util.List;


/**
 * ✅ Sliding Window 기반으로 최근 N개 메시지만 유지하는 InMemoryChatMemory 래퍼 클래스
 */
public class LimitedInMemoryChatMemory implements ChatMemory {

    // 실제 메시지 저장/조회 역할을 담당할 Spring AI InMemoryChatMemory 위임 인스턴스
    private final InMemoryChatMemory delegate = new InMemoryChatMemory();

    // 유지할 최근 메시지 최대 개수
    private final int windowSize;

    public LimitedInMemoryChatMemory(int windowSize) {
        this.windowSize = windowSize;
    }

    /**
     * 메시지 추가 시 내부적으로 Sliding Window 처리를 적용:
     * 1) delegate에 메시지 추가
     * 2) 최근 메시지를 모두 가져와 windowSize 초과 시 오래된 메시지 제거 후 다시 저장
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        // 새 메시지 추가
        delegate.add(conversationId, messages);

        // 현재 저장된 모든 메시지 가져오기
        List<Message> recentMessages = delegate.get(conversationId, Integer.MAX_VALUE);

        // windowSize 초과 시 오래된 메시지 제거
        if (recentMessages.size() > windowSize) {
            int removeCount = recentMessages.size() - windowSize;

            // delegate 메모리 클리어 후 최근 메시지만 다시 저장
            delegate.clear(conversationId);
            delegate.add(conversationId, recentMessages.subList(removeCount, recentMessages.size()));
        }
    }

    /**
     * 최근 lastN개의 메시지를 가져오기
     */
    @Override
    public List<Message> get(String conversationId, int lastN) {
        return delegate.get(conversationId, lastN);
    }

    /**
     * 특정 대화 ID의 메시지 기록 제거
     */
    @Override
    public void clear(String conversationId) {
        delegate.clear(conversationId);
    }
}