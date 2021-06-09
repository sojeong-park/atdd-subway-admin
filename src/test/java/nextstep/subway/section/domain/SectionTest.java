package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static nextstep.subway.section.domain.SectionsTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SectionTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void validDuplication() {
    }

    @Test
    void canAddSection() {
        //given

        Station 강남역 = createStation("강남역");
        Station 광교역 = createStation("광교역");
        Line 신분당선 = createLineSection("신분당선", "red", 강남역, 광교역, 50);

        Station 판교역 = createStation("판교역");
        Section 판교_광교역 = createSection(신분당선, 판교역, 광교역, 5);

        //추가 가능한 리스트 확인
        assertThat(판교_광교역.canAddSection(강남역)).isFalse();
        assertThat(판교_광교역.canAddSection(광교역)).isTrue();

    }
}