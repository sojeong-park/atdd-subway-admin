package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //given
        //지하철_역_생성_요청
        createStations();

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createLinesSection(createParamsLineSection("1호선", "blue", 1L, 2L, 7));

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        createStations();
        Map<String, Object> params = createParamsLineSection("1호선", "blue", 1L, 2L, 7);
        createLinesSection(params);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response =  createLinesSection(params);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        //지하철_구간_등록
        createStations();

        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1 =  createLinesSection(createParamsLineSection("1호선", "blue", 1L, 2L, 7));
        ExtractableResponse<Response> createResponse2 =  createLinesSection(createParamsLineSection("2호선", "grean", 3L, 4L, 7));


        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = findAllLines();
        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선과 구간을 조회한다.")
    @Test
    void getLine() {
        // given
        //지하철_역_생성_요청
        createStations();

        // 지하철_노선_등록되어_있음
        createLinesSection(createParamsLineSection("1호선", "blue", 1L, 2L, 7));

        // when
        // 지하철_노선_조회_요청_응답
        ExtractableResponse<Response> response = findLineById(1L);
        // then
        // 지하철_노선_응답됨
        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        StationResponse upStation = lineResponse.getStations().get(0);
        StationResponse downStation = lineResponse.getStations().get(lineResponse.getStations().size()-1);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(upStation.getName()).isEqualTo("구로역");
        assertThat(downStation.getName()).isEqualTo("신도림역");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        //지하철_역_생성_요청
        createStations();

        // 지하철_노선_등록되어_있음
        createLinesSection(createParamsLineSection("1호선", "blue", 1L, 2L, 7));

        // when
        // 지하철_노선_수정_요청
        Map<String, String> changedParams = createParams("2호선", "green");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(changedParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/{id}", 1L)
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().get("name").toString()).isEqualTo(changedParams.get("name"));
        assertThat(response.jsonPath().get("color").toString()).isEqualTo(changedParams.get("color"));
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        //지하철_역_생성_요청
        createStations();

        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = createLinesSection(createParamsLineSection("1호선", "blue", 1L, 2L, 7));

        // when
        // 지하철_노선_제거_요청
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private Map<String, String> createParams(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return params;
    }

    private static Map<String, Object> createParamsLineSection(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }

    private static ExtractableResponse<Response> createLinesSection(Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> findAllLines() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> findLineById(Long id) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    private void createStations() {
        지하철역_등록되어_있음("구로역").as(StationResponse.class);
        지하철역_등록되어_있음("신도림역").as(StationResponse.class);
        지하철역_등록되어_있음("영등포역").as(StationResponse.class);
        지하철역_등록되어_있음("신길역").as(StationResponse.class);
    }

    public static ExtractableResponse<Response> 지하철_노선_등록되어_있음(Map<String, Object> params) {
        return createLinesSection(createParamsLineSection(
                params.get("name").toString()
                , params.get("color").toString()
                , Long.valueOf(params.get("upStation").toString())
                , Long.valueOf(params.get("downStation").toString())
                , Integer.valueOf(params.get("distance").toString()))
        );
    }
}
