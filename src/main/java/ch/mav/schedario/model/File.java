package ch.mav.schedario.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "files")
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class File {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private Status status = Status.NEW;

  @Column(name = "created_date", nullable = false, updatable = false)
  @CreatedDate
  private OffsetDateTime createdDate;

  @Column(name = "modified_date")
  @LastModifiedDate
  private OffsetDateTime modifiedDate;

  @Column(name = "title")
  private String title;

  @Column(name = "notes")
  private String notes;

  @Column(name = "checksum", nullable = false)
  private long checksum;

  @Column(name = "path", nullable = false)
  private String path;

  @Column(name = "size")
  private long size;

  @Column(name = "created")
  private OffsetDateTime created;

  @Column(name = "thumbnail")
  @Lob
  private byte[] thumbnail;
}
