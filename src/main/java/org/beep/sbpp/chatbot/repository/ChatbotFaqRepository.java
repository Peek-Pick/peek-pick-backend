package org.beep.sbpp.chatbot.repository;

import org.beep.sbpp.chatbot.entities.ChatbotFaqEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatbotFaqRepository extends JpaRepository<ChatbotFaqEntity, Long> {
}
