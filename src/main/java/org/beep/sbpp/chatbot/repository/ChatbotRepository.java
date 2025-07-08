package org.beep.sbpp.chatbot.repository;

import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatbotRepository extends JpaRepository<ProductBaseEntity, Long> {
}
