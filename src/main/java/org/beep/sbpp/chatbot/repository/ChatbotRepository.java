package org.beep.sbpp.chatbot.repository;

import org.beep.sbpp.products.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatbotRepository extends JpaRepository<ProductEntity, Long> {
}
