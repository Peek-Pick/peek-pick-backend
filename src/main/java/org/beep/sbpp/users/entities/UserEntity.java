package org.beep.sbpp.users.entities;

import jakarta.persistence.*;
import org.beep.sbpp.users.enums.Status;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_users")
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String email;

    private String password;

    @Column(name = "is_social")
    private boolean isSocial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "ban_until")
    private LocalDateTime banUntil;

}
