package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class SectionsTest {
    private Line 신분당선;
    private Section 양재_광교역;
    @BeforeEach
    void setUp() {
        Station 양재역 = createStation("양재역");
        Station 광교역 = createStation("광교역");
        신분당선 = createLineSection("신분당선", "red", 양재역, 광교역, 50);
        양재_광교역 = createSection(신분당선, 양재역, 광교역, 50);

    }

    @Test
    @DisplayName("동일한 구간 추가시 오류 발생")
    void 동일한_구간_추가시_오류_발생() {
        assertThatThrownBy(() -> {
            신분당선.addSections(양재_광교역);
        }).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("동일한 구간은 추가할수 없습니다.");
    }

    @Test
    @DisplayName("상하행 연관된 역이 없을 경우 오류발생")
    void 연관된_역이_없다면_오류_발생() {
        Station 판교역 = createStation("판교역");
        Station 정자역 = createStation("정자역");
        Section 판교_정자역 = createSection(신분당선, 판교역, 정자역, 5);
        assertThatThrownBy(() -> {
            신분당선.addSections(판교_정자역);
        }).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("연관된 억이 없어 추가할수 없습니다.");
    }

    public Section createSection(Line line, Station upStation, Station downStation, int distance) {
        return new Section(line, upStation, downStation, distance);
    }

    public Line createLineSection(String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(name, color, upStation, downStation, distance);
    }

    public Station createStation(String name) {
        return new Station(name);
    }
}