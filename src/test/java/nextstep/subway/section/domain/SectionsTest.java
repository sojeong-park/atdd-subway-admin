package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class SectionsTest {
    private static Line 신분당선;
    private static Station 양재역;
    private static Station 청계산입구역;
    private static Station 광교역;
    private static Station 양재숲역;
    private static Section 양재숲_청계산역;
    private static Section 청계산_광교역;

    @Test
    @DisplayName("구간이 한개 등록된 경우 앞에 추가하기")
    void 구간_한개_앞에_추가() {
        신분당선_구간_한개_생성();

        Section 강남_양재역 = createSection(신분당선, new Station("강남역"), new Station("양재역"),10);
        신분당선.getSections().addOneSectionList(강남_양재역);

        List<Section> 추가된_신분당선 = 신분당선.getSections().getSections();
        assertThat(추가된_신분당선.get(0)).isEqualTo(강남_양재역);
    }

    @Test
    @DisplayName("구간이 한개 등록된 경우 뒤에 추가하기")
    void 구간_한개_뒤에_추가() {
        신분당선_구간_한개_생성();

        Section 양재숲_판교역 = createSection(신분당선, new Station("양재숲역"), new Station("판교역"),10);
        신분당선.getSections().addOneSectionList(양재숲_판교역);

        List<Section> 추가된_신분당선 = 신분당선.getSections().getSections();
        assertThat(추가된_신분당선.get(1)).isEqualTo(양재숲_판교역);
    }

    @Test
    @DisplayName("동일한 구간 추가시 오류 발생")
    void 동일한_구간_추가시_오류_발생() {
        신분당선_구간_여러개_생성();

        assertThatThrownBy(() -> {
            신분당선.getSections().validDuplicationSection(청계산_광교역);
        }).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("동일한 구간은 추가할수 없습니다.");
    }

    @Test
    @DisplayName("상하행 연관된 역이 없을 경우 오류발생")
    void 연관된_역이_없다면_오류_발생() {
        신분당선_구간_여러개_생성();

        Station 판교역 = createStation("판교역");
        Station 정자역 = createStation("정자역");
        Section 판교_정자역 = createSection(신분당선, 판교역, 정자역, 5);

        assertThatThrownBy(() -> {
            신분당선.getSections().inputSection(판교_정자역);
        }).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("일치하는 역이 없어 구간을 추가할수 없습니다.");
    }

    @Test
    @DisplayName("기존 구간의 거리보다 추가하려는 구간의 거리가 더 크다면 오류발생")
    void 구간_오류_발생() {
        신분당선_구간_여러개_생성();

        Station 판교역 = createStation("판교역");
        Section 양재숲_판교역 = createSection(신분당선, 양재숲역, 판교역, 3000);

        assertThatThrownBy(() -> {
            신분당선.getSections().inputSection(양재숲_판교역);
        }).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("기존 구간보다 긴 거리의 구간은 입력 할 수 없습니다.");
    }

    @Test
    @DisplayName("양재역->양재숲역->청계산입구역->광교역 노선에 양재숲역->판교역 추가하기")
    void 노선_추가() {
        //given
        신분당선_구간_여러개_생성();

        Station 판교역 = createStation("판교역");
        Section 양재숲_판교역 = createSection(신분당선, 양재숲역, 판교역, 3);
        신분당선.getSections().inputSection(양재숲_판교역);

        List<Section> 추가된_신분당선 = 신분당선.getSections().getSections();
        for (Section section: 추가된_신분당선) {
            System.out.println(section.getUpStation().getName()+","+section.getDownStation().getName());
        }
        assertThat(추가된_신분당선.get(1)).isEqualTo(양재숲_판교역);
        assertThat(추가된_신분당선.get(2).getUpStation().getName()).isEqualTo(판교역.getName());
    }

    @Test
    @DisplayName("양재역->양재숲역->청계산입구역->광교역 노선에 양재숲역->판교역 추가하기")
    void 노선_중간에_구간_추가() {
        //given
        신분당선_구간_여러개_생성();

        //when
        Station 판교역 = createStation("판교역");
        Section 양재숲_판교역 = createSection(신분당선, 양재숲역, 판교역, 3);
        List<Section> upStationSection = 신분당선.getSections().findStation(양재숲_판교역.getUpStation());
        신분당선.getSections().addSectionListEqualUpStation(upStationSection, 양재숲_판교역);

        //then
        List<Section> 추가된_신분당선 = 신분당선.getSections().getSections();
        for (Section section: 추가된_신분당선) {
            System.out.println(section.getUpStation().getName()+","+section.getDownStation().getName());
        }
        assertThat(추가된_신분당선.get(1)).isEqualTo(양재숲_판교역);
        assertThat(추가된_신분당선.get(2).getUpStation().getName()).isEqualTo(판교역.getName());
    }

    @Test
    @DisplayName("중간에 구간 추가: 양재역->양재숲역->청계산입구역->광교역 노선에 판교역->양재숲역 추가하기")
    void 노선_중간에_구간_추가2() {
        //given
        신분당선_구간_여러개_생성();

        //when
        Station 판교역 = createStation("판교역");
        Section 판교_양재숲역 = createSection(신분당선, 판교역, 양재숲역, 10);
        List<Section> upStationSection = 신분당선.getSections().findStation(판교_양재숲역.getUpStation());
        신분당선.getSections().addSectionListEqualDownStation(upStationSection, 판교_양재숲역);

        //given
        List<Section> 추가된_신분당선 = 신분당선.getSections().getSections();
        for (Section section: 추가된_신분당선) {
            System.out.println(section.getUpStation().getName()+","+section.getDownStation().getName());
        }
        assertThat(추가된_신분당선.get(0).getDownStation().getName()).isEqualTo(판교역.getName());
        assertThat(추가된_신분당선.get(1)).isEqualTo(판교_양재숲역);
    }

    @Test
    @DisplayName("양재역->양재숲역->청계산입구역->광교역 노선에 강남->양재 추가")
    void 가장_앞_노선에_구간_추가() {
        //given
        신분당선_구간_여러개_생성();

        //when
        Station 강남역 = createStation("강남역");
        Section 강남_양재역 = createSection(신분당선, 강남역, 양재역, 10);
        신분당선.getSections().inputSection(강남_양재역);

        //then
        List<Section> 추가된_신분당선 = 신분당선.getSections().getSections();
        for (Section section: 추가된_신분당선) {
            System.out.println(section.getUpStation().getName()+","+section.getDownStation().getName());
        }
        assertThat(추가된_신분당선.get(0)).isEqualTo(강남_양재역);
    }

    @Test
    @DisplayName("양재역->양재숲역->청계산입구역->광교역 노선에 광교역->정자역 추가")
    void 가장_뒤_노선에_구간_추가() {
        //given
        신분당선_구간_여러개_생성();

        //when
        Station 구일역 = createStation("구일역");
        Section 광교_구일역 = createSection(신분당선, 광교역, 구일역, 10);
        신분당선.getSections().inputSection(광교_구일역);

        //then
        List<Section> 추가된_신분당선 = 신분당선.getSections().getSections();
        for (Section section: 추가된_신분당선) {
            System.out.println(section.getUpStation().getName()+","+section.getDownStation().getName());
        }
        assertThat(추가된_신분당선).hasSize(4);
        assertThat(추가된_신분당선.get(추가된_신분당선.size()-1)).isEqualTo(광교_구일역);
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

    public static void 신분당선_구간_한개_생성() {
        양재역 = createStation("양재역");
        양재숲역 = createStation("양재숲역");
        신분당선 = createLineSection("신분당선", "red", 양재역, 양재숲역, 150);
    }

    public static void 신분당선_구간_여러개_생성() {
        양재역 = createStation("양재역");
        양재숲역 = createStation("양재숲역");
        신분당선 = createLineSection("신분당선", "red", 양재역, 양재숲역, 150);

        청계산입구역 = createStation("청계산입구역");
        양재숲_청계산역 = createSection(신분당선, 양재숲역, 청계산입구역, 30);
        신분당선.addSections(양재숲_청계산역);

        광교역 = createStation("광교역");
        청계산_광교역 = createSection(신분당선, 청계산입구역, 광교역, 50);
        신분당선.addSections(청계산_광교역);
    }
}