package org.main.hackerthon.api.controller;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.main.hackerthon.api.dto.WeatherDto;
import org.main.hackerthon.api.domain.Region;
import org.main.hackerthon.api.domain.Weather;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class WeatherController {


  private final EntityManager em;
  private String serviceKey = "=aPirvKLaMlkMVa50Nl9XLne3d3pSoHN0mC6uwGLbUFI2BUxTYUQXDfLYae3IvBdKa5Zivm%2B2F25tyIljAwTYNQ%3D%3D";

  @GetMapping("/weather")
  @Transactional
  public ResponseEntity<Object> getRegionWeather(@RequestParam Long regionId) {

    // 1. 날씨 정보를 요청한 지역 조회
    Region region = em.find(Region.class, regionId);
    StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst");

    // 2. 요청 시각 조회
    LocalDateTime now = LocalDateTime.of(2024, 7, 25, 0, 0, 0);
    String yyyyMMdd = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    int hour = now.getHour();
    int min = now.getMinute();
    if (min <= 30) { // 해당 시각 발표 전에는 자료가 없음 - 이전시각을 기준으로 해야함
      hour -= 1;
    }
    String hourStr = "300"; // 정시 기준
    String nx = Integer.toString(region.getNx());
    String ny = Integer.toString(region.getNy());
    String currentChangeTime = now.format(DateTimeFormatter.ofPattern("yy.MM.dd ")) + hour;

    // 기준 시각 조회 자료가 이미 존재하고 있다면 API 요청 없이 기존 자료 그대로 넘김
//    Weather prevWeather = region.getWeather();
//    if (prevWeather != null && prevWeather.getLastUpdateTime() != null) {
//      if (prevWeather.getLastUpdateTime().equals(currentChangeTime)) {
//        log.info("기존 자료를 재사용합니다");
//        WeatherDto dto = WeatherDto.builder()
//            .humid(prevWeather.getHumid())
//            .lastUpdateTime(prevWeather.getLastUpdateTime())
//            .rainAmount(prevWeather.getRainAmount())
//            .temp(prevWeather.getTemp())
//            .weatherCondition(prevWeather.getWeatherCondition())
//            .build();
//        return ResponseEntity.ok(dto);
//      }
//    }

    log.info("API 요청 발송 >>> 지역: {}, 연월일: {}, 시각: {}", region, yyyyMMdd, hourStr);

    try {
      urlBuilder.append("?").append(URLEncoder.encode("serviceKey", "UTF-8")).append(serviceKey);
      urlBuilder.append("&").append(URLEncoder.encode("pageNo", "UTF-8")).append("=").append(URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
      urlBuilder.append("&").append(URLEncoder.encode("numOfRows", "UTF-8")).append("=").append(URLEncoder.encode("1000", "UTF-8")); /*한 페이지 결과 수*/
      urlBuilder.append("&").append(URLEncoder.encode("dataType", "UTF-8")).append("=")
          .append(URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
      urlBuilder.append("&").append(URLEncoder.encode("base_date", "UTF-8")).append("=")
          .append(URLEncoder.encode(yyyyMMdd, "UTF-8")); /*‘21년 6월 28일 발표*/
      urlBuilder.append("&").append(URLEncoder.encode("base_time", "UTF-8")).append("=")
          .append(URLEncoder.encode(hourStr, "UTF-8")); /*06시 발표(정시단위) */
      urlBuilder.append("&").append(URLEncoder.encode("nx", "UTF-8")).append("=").append(URLEncoder.encode(nx, "UTF-8")); /*예보지점의 X 좌표값*/
      urlBuilder.append("&").append(URLEncoder.encode("ny", "UTF-8")).append("=").append(URLEncoder.encode(ny, "UTF-8")); /*예보지점의 Y 좌표값*/

      URL url = new URL(urlBuilder.toString());
      log.info("request url: {}", url);

      Weather weather = getWeather(url, currentChangeTime);
      region.updateRegionWeather(weather); // DB 업데이트
      WeatherDto dto = WeatherDto.builder()
          .humid(weather.getHumid())
          .lastUpdateTime(weather.getLastUpdateTime())
          .rainAmount(weather.getRainAmount())
          .temp(weather.getTemp())
          .weatherCondition(weather.getWeatherCondition())
          .build();
      return ResponseEntity.ok(dto);

//    } catch (IOException e) {
//      WeatherDto dto = WeatherDto.builder()
//          .humid(prevWeather.getHumid())
//          .lastUpdateTime(prevWeather.getLastUpdateTime())
//          .rainAmount(prevWeather.getRainAmount())
//          .temp(prevWeather.getTemp())
//          .weatherCondition(prevWeather.getWeatherCondition())
//          .build();
//      return ResponseEntity.ok(dto);
//    }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Weather getWeather(URL url, String currentChangeTime) throws IOException {
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Content-type", "application/json");
    log.error(conn.toString());
    BufferedReader rd;
    if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
      rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    } else {
      rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
    }
    StringBuilder sb = new StringBuilder();
    log.error(rd.toString());
    String line;
    while ((line = rd.readLine()) != null) {
      sb.append(line);
    }
    rd.close();
    conn.disconnect();
    String data = sb.toString();

    // Log the response data for debugging
    System.out.println("Response Data: " + data);

    // Check if the response is a valid JSON
    if (data == null || data.isEmpty() || !data.trim().startsWith("{")) {
      throw new IOException("Invalid JSON response: " + data);
    }

    //// 응답 수신 완료 ////
    //// 응답 결과를 JSON 파싱 ////

    Double temp = null;
    Double humid = null;
    Double rainAmount = null;
    Double weatherCondition = null;

    JSONObject jObject = new JSONObject(data);
    JSONObject response = jObject.getJSONObject("response");
    JSONObject body = response.getJSONObject("body");
    JSONObject items = body.getJSONObject("items");
    JSONArray jArray = items.getJSONArray("item");

    for (int i = 0; i < jArray.length(); i++) {
      JSONObject obj = jArray.getJSONObject(i);
      String category = obj.getString("category");
      double obsrValue = obj.getDouble("obsrValue");

      switch (category) {
        case "T1H":
          temp = obsrValue;
          break;
        case "RN1":
          rainAmount = obsrValue;
          break;
        case "REH":
          humid = obsrValue;
          break;
        case "PTY":
          weatherCondition = obsrValue;
          break;
      }
    }

    Weather weather = new Weather(temp, rainAmount, humid, currentChangeTime, weatherCondition);
    return weather;
  }

}
