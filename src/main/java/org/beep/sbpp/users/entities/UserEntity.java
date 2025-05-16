package org.beep.sbpp.users.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_users")
@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    private String email;

    private String password;

    private boolean is_social;




//email, password, is_social, is_admin, status,

}
