package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class SectionsTest {
    private static Line 신분당선;
    private static Station 강남역;
    private static Station 양재역;
    private static Station 광교역;
    private static Station 양재숲역;
    private static Section 양재_양재숲역;
    private static Section 양재숲_광교역;
    @BeforeEach
    void setUp() {
        양재역 = createStation("양재역");
        광교역 = createStation("광교역");
        신분당선 = createLineSection("신분당선", "red", 양재역, 광교역, 50);
    }

    @Test
    @DisplayName("동일한 구간 추가시 오류 발생")
    void 동일한_구간_추가시_오류_발생() {
        신분당선 = createLineSection("신분당선", "red", 양재역, 광교역, 50);
        Section 양재_광교역 = createSection(신분당선, 양재역, 광교역, 50);
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

    @Test
    @DisplayName("A->B->C->D 노선에 c->z역 추가하기")
    void 노선_추가() {
        //given
        신분당선_구간_생성();

        //강남역->양재역->양재숲역->광교역 => 강남역->양재역->양재숲역->판교역->광교역
        //when 추가하려는 구간 양재숲역->판교역->광교역 순으로 추가
        Station 판교역 = createStation("판교역");
        Section 양재숲_판교역 = createSection(신분당선, 양재숲역, 판교역, 3);

        Map<String, Object> result = 신분당선.getSections().findAddIndex(양재숲_판교역);
        assertThat(result.get("index")).isEqualTo(1);
        assertThat(result.get("station")).isEqualTo(판교역);
        assertThat(result.get("stationPosition")).isEqualTo("up");
        System.out.println(result);
    }

    @Test
    @DisplayName("리스트 자르기")
    void split_list() {
        //given 기존 구간
        신분당선_구간_생성();

        List<Section> splitSection1 = 신분당선.getSections().splitList(0,1);
        List<Section> splitSection2 = 신분당선.getSections().splitList(1,3);

        assertThat(splitSection1).hasSize(1);
        assertThat(splitSection2).hasSize(2);

        assertThat(splitSection1.get(0).getUpStation().getName()).isEqualTo(강남역.getName());
        assertThat(splitSection1.get(0).getDownStation().getName()).isEqualTo(양재역.getName());
        assertThat(splitSection2.get(1).getUpStation().getName()).isEqualTo(양재숲역.getName());
        assertThat(splitSection2.get(1).getDownStation().getName()).isEqualTo(광교역.getName());
    }

    @Test
    @DisplayName("중간에 구간 추가: 강남역->양재역->양재숲역->광교역 노선에 양재숲역->판교역 추가하기")
    void 노선_중간에_구간_추가() {
        //given
        신분당선_구간_생성();

        //when
        Station 판교역 = createStation("판교역");
        Section 양재숲_판교역 = createSection(신분당선, 양재숲역, 판교역, 3);

        신분당선.getSections().findAddIndex(양재숲_판교역);
        List<Section> 추가된_신분당선 = 신분당선.getSections().getSections();
        for (Section newSection: 추가된_신분당선) {
            System.out.println(newSection.getUpStation().getName()+","+newSection.getDownStation().getName());
        }
    }

    @Test
    @DisplayName("중간에 구간 추가: 강남역->양재역->양재숲역->광교역 노선에 판교역->양재숲역 추가하기")
    void 노선_중간에_구간_추가2() {
        //given
        신분당선_구간_생성();

        //when
        Station 판교역 = createStation("판교역");
        Section 판교숲_양재숲역 = createSection(신분당선, 판교역, 양재숲역, 100);

        신분당선.getSections().findAddIndex(판교숲_양재숲역);
        List<Section> 추가된_신분당선 = 신분당선.getSections().getSections();
        for (Section newSection: 추가된_신분당선) {
            System.out.println(newSection.getUpStation().getName()+","+newSection.getDownStation().getName());
        }
    }

    @Test
    @DisplayName("가장 앞 노선에 구간 추가: 양재->양재숲->판교 노선에 강남->양재 추가")
    void 가장_앞_노선에_구간_추가() {
        //given
        신분당선_구간_생성();

        //when
        Station 교대역 = createStation("교대역");
        Section 교대_강남역 = createSection(신분당선, 교대역, 강남역, 100);

        신분당선.getSections().findAddIndex(교대_강남역);
        List<Section> 추가된_신분당선 = 신분당선.getSections().getSections();
        for (Section newSection: 추가된_신분당선) {
            System.out.println(newSection.getUpStation().getName()+","+newSection.getDownStation().getName());
        }
    }

    @Test
    @DisplayName("가장 뒤 노선에 구간 추가: 양재->양재숲->판교 노선에 판교->정자 추가")
    void 가장_뒤_노선에_구간_추가() {

    }

    public static Section createSection(Line line, Station upStation, Station downStation, int distance) {
        return new Section(line, upStation, downStation, distance);
    }

    public static Line createLineSection(String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(name, color, upStation, downStation, distance);
    }

    public static Station createStation(String name) {
        return new Station(name);
    }

    public static void 신분당선_구간_생성() {
        강남역 = createStation("강남역");
        양재역 = createStation("양재역");
        신분당선 = createLineSection("신분당선", "red", 강남역, 양재역, 50);

        양재숲역 = createStation("양재숲역");
        양재_양재숲역 = createSection(신분당선, 양재역, 양재숲역, 3);
        신분당선.addSections(양재_양재숲역);

        광교역 = createStation("광교역");
        양재숲_광교역 = createSection(신분당선, 양재숲역, 광교역, 5);
        신분당선.addSections(양재숲_광교역);
    }
}