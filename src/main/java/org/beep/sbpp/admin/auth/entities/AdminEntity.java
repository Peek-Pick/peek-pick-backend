package org.beep.sbpp.admin.auth.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.common.BaseEntity2;

@Entity
@Table(name = "tbl_admin")
@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminEntity extends BaseEntity2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "account_id", nullable = false, unique = true)
    private String accountId;

    @Column(nullable = false)
    private String password;
}
