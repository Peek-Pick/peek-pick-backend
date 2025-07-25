package org.beep.sbpp.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(value = AuditingEntityListener.class)
public class BaseEntity {
    @CreatedDate
    @Column(name = "reg_date", updatable = false)
    protected LocalDateTime regDate;

    @LastModifiedDate
    @Column(name ="mod_date")
    protected LocalDateTime modDate;
}
