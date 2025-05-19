package org.beep.sbpp.users.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.users.enums.Gender;
import org.beep.sbpp.users.enums.Nationality;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_user_profile")
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileEntity extends BaseEntity{

    @Id
    private long userId;

    @MapsId //pk + fk
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Nationality nationality;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "profile_img_url")
    private String profileImageUrl;
}
