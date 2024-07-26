package org.main.hackerthon.api.dto;

import java.io.Serializable;
import lombok.Builder;
import org.main.hackerthon.api.domain.Weather;

/**
 * DTO for {@link Weather}
 */
@Builder
public record WeatherDto(Double temp, Double rainAmount, Double humid, String lastUpdateTime, Double weatherCondition) implements Serializable {

}