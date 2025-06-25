package org.beep.sbpp.chatbot.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.users.entities.UserEntity;

@Entity
@Table(name = "tbl_chatbot_faq")
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatbotFaqEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faq_id")
    private Long faqId;

    @Column(name = "question", nullable = false)
    private String question;

    @Column(name = "answer", nullable = false)
    private String answer;

    @Column(length = 50)
    private String category;


}
