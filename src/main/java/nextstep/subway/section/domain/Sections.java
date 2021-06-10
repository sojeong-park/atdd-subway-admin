package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public void findAddIndex(Section section) {
        Map<String, Object> result = new HashMap<>();
        int index = 0;
        Station newStation = new Station();
        boolean isUpdate = false;
        boolean updateComplete = false;
        for (Section currentSection : sections) {
            currentSection.validDuplication(section);
            upStationEquals(currentSection, section);
            downStationEquals(currentSection, section);
//            if (currentSection.canAddSection(section.getUpStation())) {
//                index = sections.indexOf(currentSection);
//                isUpdate = currentSection.isSmallDistance(section.getDistance());
//                newStation = section.getDownStation();
//                if (isUpdate) {
//                    if (index == 0) {
//                        if (section.getDownStation().equals(sections.get(0).getUpStation())) {
//                            sections.add(0, section);
//                            updateComplete = true;
//                        }
//                    }
//                    if (index == sections.size()-1) {
//                        if (section.getUpStation().equals(sections.get(sections.size()-1).getDownStation())) {
//                            sections.add(section);
//                            updateComplete = true;
//                        }
//                    }
//                    if (!updateComplete) {
//                        //상항선 일치시 변경 노드
//                        sections.add(index + 1, section);
//                        sections.get(index + 2).updateUpStation(newStation);
//                    }
//                }
//                break;
//            }
//
//            if (currentSection.canAddSection(section.getDownStation())) {
//                index = sections.indexOf(currentSection);
//                isUpdate = currentSection.isSmallDistance(section.getDistance());
//                newStation = section.getUpStation();
//                if (isUpdate) {
//                    if (index == 0) {
//                        if (section.getDownStation().equals(sections.get(0).getUpStation())) {
//                            sections.add(0, section);
//                            updateComplete = true;
//                        }
//                    }
//                    if (index == sections.size()-1) {
//                        if (section.getUpStation().equals(sections.get(sections.size()-1).getDownStation())) {
//                            sections.add(section);
//                            updateComplete = true;
//                        }
//                    }
//                    if (!updateComplete) {
//                        //하행선 일치시 변경 노드
//                        sections.add(index+1, section);
//                        sections.get(index).updateDownStation(section.getUpStation());
//                    }
//                }
//                break;
//            }
        }
    }

    public void upStationEquals(Section currentSection, Section section) {
        if (currentSection.canAddSection(section.getUpStation())) {
            int index = sections.indexOf(currentSection);
            boolean isUpdate = canInputFirstOrLast(index, section);
            canInputMiddleUpStation(isUpdate,section, index);
        }
    }

    public void downStationEquals(Section currentSection, Section section) {
        if (currentSection.canAddSection(section.getDownStation())) {
            int index = sections.indexOf(currentSection);
            boolean isUpdate = canInputFirstOrLast(index, section);
            canInputMiddleDownStation(isUpdate, section, index);
        }
    }
    public boolean canInputFirstOrLast(int index, Section section) {
        if (index == 0) {
           return canInputFirst(section);
        }
        if (index == sections.size()-1) {
            return canInputLast(section);
        }
        return false;
    }

    public boolean canInputFirst(Section section) {
        if (section.getDownStation().equals(sections.get(0).getUpStation())) {
            sections.add(0, section);
            return true;
        }
        return false;
    }

    public boolean canInputLast(Section section) {
        if (section.getUpStation().equals(sections.get(sections.size()-1).getDownStation())) {
            sections.add(section);
            return true;
        }
        return false;
    }

    public void canInputMiddleUpStation(boolean isUpdate, Section section, int index) {
        if (!isUpdate) {
            sections.add(index + 1, section);
            sections.get(index + 2).updateUpStation(section.getDownStation());
        }
    }

    public void canInputMiddleDownStation(boolean isUpdate, Section section, int index) {
        if (!isUpdate) {
            sections.add(index+1, section);
            sections.get(index).updateDownStation(section.getUpStation());
        }
    }

    public void addSectionTest(Section section) {
        List<Section> hasUpStation = findHasStation(section.getUpStation());
        if (hasUpStation.size() != 0) {
            int index = sections.indexOf(hasUpStation.get(0));
            boolean isUpdate = canInputFirstOrLast(index, section);
            canInputMiddleUpStation(isUpdate, section, index);
        }
        List<Section> hasDownStation = findHasStation(section.getDownStation());
        if (hasDownStation.size()==0){
            throw new RuntimeException("일치하는 역이 없어 구간을 추가할수 없습니다.");
        }
        int index = sections.indexOf(hasDownStation.get(0));
        boolean isUpdate = canInputFirstOrLast(index, section);
        canInputMiddleDownStation(isUpdate, section, index);
    }

    public List<Section> findHasStation(Station station) {
        return sections.stream()
                .filter(it -> it.getDownStation().equals(station) || it.getUpStation().equals(station))
                .collect(Collectors.toList());
    }
}
