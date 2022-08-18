package ch.mav.schedario.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.OffsetDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
abstract class Auditable {

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    private OffsetDateTime createdDate;

    @Column(name = "modified_date")
    @LastModifiedDate
    private OffsetDateTime modifiedDate;
}
