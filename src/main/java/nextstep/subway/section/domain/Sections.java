package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
//        if (hasSection(section)) {
//            throw new RuntimeException("연관된 억이 없어 추가할수 없습니다.");
//        }
//        Map<String, Object> indexInfo = findAddIndex(section);
//        int splitIndex = (int) indexInfo.get("index"); // 자를 값
//        List<Section> splitUp = splitList(0, splitIndex);
//        List<Section> splitDown = splitList(splitIndex, sections.size());

        //뒤에 추가하는게 더 쉽다..

        sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    // 주어진 upstation과 downstation의 값을 받아와 해당 값과 일치하는 역들이 있는지 확인한다.
    // 두 값과 완전히 동일한 SECTION이 있다면 등록불가하다.
    // 두 값과 하나라도 일치하지 않는다면 등록불가하다.
    public Map<String, Object> findAddIndex(Section section) {
        Map<String, Object> result = new HashMap<>();
        int index = 0;
        boolean isUpStationChange = false;
        boolean isDownStationChange = false;
        Section inputSection = new Section();
        for (Section currentSection : sections) {
            currentSection.validDuplication(section);
            if (currentSection.canAddSection(section.getUpStation())) {
                index = sections.indexOf(currentSection);
                isUpStationChange = currentSection.isSmallDistance(section.getDistance());;
                break;
            }

            if (currentSection.canAddSection(section.getDownStation())) {
                index = sections.indexOf(currentSection);
                isDownStationChange = currentSection.isSmallDistance(section.getDistance());
                break;
            }

            if (index == 0) {
                if (section.getDownStation().equals(currentSection.getUpStation())) {
                    //add(0, section.getUpStation()) 하고 끝낸다.
                }
            }
            if(index == sections.size()-1) {
                if (section.getUpStation().equals(currentSection.getUpStation())) {
                    //add()하고 끝
                }
            }
        }
        if (isUpStationChange) {
            sections.add(index+1, section);
            sections.get(index+2).updateUpStation(section.getDownStation());
        }
        if (isDownStationChange) {
            sections.add(index+1, section);
            sections.get(index).updateDownStation(section.getUpStation());
        }
        return result;
    }

    public List<Section> splitList(int startIndex, int endIndex) {
        return sections.stream()
                .collect(Collectors.toList())
                .subList(startIndex, endIndex);
    }
}
