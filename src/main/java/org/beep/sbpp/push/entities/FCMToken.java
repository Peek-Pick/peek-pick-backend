package org.beep.sbpp.push.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_fcm_token")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FCMToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(length = 512, nullable = false)
    private String token;
}