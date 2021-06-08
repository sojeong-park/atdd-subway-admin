package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        if (hasSection(section)) {
            throw new RuntimeException("연관된 억이 없어 추가할수 없습니다.");
        }
        sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    // 주어진 upstation과 downstation의 값을 받아와 해당 값과 일치하는 역들이 있는지 확인한다.
    // 두 값과 완전히 동일한 SECTION이 있다면 등록불가하다.
    // 두 값과 하나라도 일치하지 않는다면 등록불가하다.
    public boolean hasSection(Section section) {
        for (Section currentSection : sections) {
            currentSection.validDuplication(section);
            //일치하는 항목이 하나라도 있으면  멈춘다. 그자리에 추가한다.
        }
        return false;
    }
}
