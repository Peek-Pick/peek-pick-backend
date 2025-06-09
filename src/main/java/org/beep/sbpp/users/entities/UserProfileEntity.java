package org.beep.sbpp.users.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.users.enums.Gender;
import org.beep.sbpp.users.enums.Nationality;
import org.springframework.stereotype.Indexed;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_user_profile",
        indexes = @Index(name = "idx_user_nickname", columnList = "nickname"))
@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileEntity extends BaseEntity{

    @Id
    private Long userId;

    @MapsId //pk + fk
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Nationality nationality;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "profile_img_url")
    private String profileImgUrl;
}
