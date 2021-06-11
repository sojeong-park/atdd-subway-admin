package nextstep.subway.section;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nextstep.subway.line.LineAcceptanceTest.라인_조회;
import static nextstep.subway.line.LineAcceptanceTest.지하철_노선_등록되어_있음;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private LineResponse 신분당선;
    private StationResponse 강남역;
    private StationResponse 광교역;
    private Map<String, Object> createParams;
    private StationResponse 양재역;

    @DisplayName("기존 구간 하나인 노선에 구간을 중간에 등록한다.")
    @Test
    void addSection() {
        // given
        기존_구간_한개_등록됨();

        // when
        StationResponse 판교역 = StationAcceptanceTest.지하철역_등록되어_있음("판교역").as(StationResponse.class);
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(강남역.getId(), 판교역.getId(), 10);
        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getStations().get(0).getName()).isEqualTo(강남역.getName());
        assertThat(lineResponse.getStations().get(1).getName()).isEqualTo(판교역.getName());
        assertThat(lineResponse.getStations().get(2).getName()).isEqualTo(판교역.getName());
        assertThat(lineResponse.getStations().get(3).getName()).isEqualTo(광교역.getName());
    }

    @DisplayName("기존 구간 하나인 노선에 구간을 가장 앞에 등록한다.")
    @Test
    void 기존_구간_하나_0번째_인덱스_등록() {
        // given
        기존_구간_한개_등록됨();
        // when
        StationResponse 잠실역 = StationAcceptanceTest.지하철역_등록되어_있음("잠실역").as(StationResponse.class);
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(잠실역.getId(), 강남역.getId(), 10);
        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getStations().get(0).getName()).isEqualTo(잠실역.getName());
        assertThat(lineResponse.getStations().get(1).getName()).isEqualTo(강남역.getName());
        assertThat(lineResponse.getStations().get(2).getName()).isEqualTo(강남역.getName());
        assertThat(lineResponse.getStations().get(3).getName()).isEqualTo(광교역.getName());
    }

    @DisplayName("기존 구간 하나인 노선에 구간을 가장 뒤에 등록한다.")
    @Test
    void 기존_구간_하나_마지막_인덱스_등록() {
        // given
        기존_구간_한개_등록됨();

        // when
        StationResponse 구일역 = StationAcceptanceTest.지하철역_등록되어_있음("구일역").as(StationResponse.class);
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(광교역.getId(), 구일역.getId(), 10);
        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineResponse.getStations().get(0).getName()).isEqualTo(강남역.getName());
        assertThat(lineResponse.getStations().get(1).getName()).isEqualTo(광교역.getName());
        assertThat(lineResponse.getStations().get(2).getName()).isEqualTo(광교역.getName());
        assertThat(lineResponse.getStations().get(3).getName()).isEqualTo(구일역.getName());
    }

    @DisplayName("기존 구간 여러개인 노선에 구간을 중간에 등록한다.")
    @Test
    void 기존_구간_여러개_구간_중간_등록() {
        // given
        기존_구간_여러개_등록됨();

        StationResponse 양재숲역 = StationAcceptanceTest.지하철역_등록되어_있음("양재숲역").as(StationResponse.class);
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(양재역.getId(), 양재숲역.getId(), 2);

        // when
//        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);

        // then
//        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
//        assertThat(lineResponse.getStations().get(0).getName()).isEqualTo(강남역.getName());
//        assertThat(lineResponse.getStations().get(1).getName()).isEqualTo(광교역.getName());
//        assertThat(lineResponse.getStations().get(2).getName()).isEqualTo(광교역.getName());
//        assertThat(lineResponse.getStations().get(3).getName()).isEqualTo(구일역.getName());
    }

    private static ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines/{lineId}/sections", 1L)
                .then().log().all()
                .extract();
    }

    private void 기존_구간_한개_등록됨() {
        강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
        광교역 = StationAcceptanceTest.지하철역_등록되어_있음("광교역").as(StationResponse.class);

        createParams = new HashMap<>();
        createParams.put("name", "신분당선");
        createParams.put("color", "bg-red-600");
        createParams.put("upStation", 강남역.getId() + "");
        createParams.put("downStation", 광교역.getId() + "");
        createParams.put("distance", 20 + "");
        신분당선 = 지하철_노선_등록되어_있음(createParams).as(LineResponse.class);
    }

    private void 기존_구간_여러개_등록됨() {
        기존_구간_한개_등록됨();

        양재역 = StationAcceptanceTest.지하철역_등록되어_있음("양재역").as(StationResponse.class);
        지하철_노선에_지하철역_등록_요청(강남역.getId(), 양재역.getId(), 1);
    }
}
