package org.main.hackerthon.api.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@NoArgsConstructor // 디폴트 생성자
@Table(name = "picture")
public class Picture {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long pictureId; // PK

  @Column(name = "image_path", nullable = false)
  private String imagePath; // 사진 경로

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  private Product product; // FK

  @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime createdAt;

  // 생성자
  public Picture(Product product, String imagePath) {
    this.product = product;
    this.imagePath = imagePath;
    this.createdAt = LocalDateTime.now();
  }
}