package org.main.hackerthon.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor // 디폴트 생성자
@Table(name = "region")
public class Region {

  @Id
  @Column(name = "id")
  private Long id; // 지역 순번

  @Column(name = "region_parent")
  private String parentRegion; // 시, 도

  @Column(name = "region_child")
  private String childRegion; // 시, 군, 구

  private int nx; // x좌표

  private int ny; // y좌표

  @Embedded
  private Weather weather; // 지역 날씨 정보

  @OneToOne
  private Product product;

  // 날씨 정보 제외하고 지역 생성
  public Region(Long id, String parentRegion, String childRegion, int nx, int ny) {
    this.id = id;
    this.parentRegion = parentRegion;
    this.childRegion = childRegion;
    this.nx = nx;
    this.ny = ny;
  }

  // 날씨 갱신
  public void updateRegionWeather(Weather weather) {
    this.weather = weather;
  }

  @Override
  public String toString() {
    return parentRegion + " " + childRegion;
  }
}