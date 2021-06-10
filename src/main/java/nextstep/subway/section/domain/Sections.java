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

    public void inputSection(Section section) {
        validDuplicationSection(section);
        List<Section> upStationSection = findStation(section.getUpStation());
        if (sections.size() > 1 && upStationSection.size() == 0) {
            addDownStation(section);
        }
        if (sections.size() > 1 && upStationSection.size() >= 1) {
            int index = sections.indexOf(upStationSection.get(0));
            boolean isUpdate = canInputFirstOrLast(index, section);
            inputMiddleUpStation(isUpdate, section, index);
        }
        if (sections.size() == 1) {
            int upStationSize = findStation(section.getUpStation()).size();
            int downStationSize = findStation(section.getDownStation()).size();
            if (upStationSize == 1) {
                canInputLast(section);
            }
            if (downStationSize == 1) {
                canInputFirst(section);
            }

        }
    }

    public void addDownStation(Section section) {
        List<Section> hasDownStation = findStation(section.getDownStation());
        if (hasDownStation.size()==0){
            throw new RuntimeException("일치하는 역이 없어 구간을 추가할수 없습니다.");
        }
        int index = sections.indexOf(hasDownStation.get(0));
        boolean isUpdate = canInputFirstOrLast(index, section);
        inputMiddleDownStation(isUpdate, section, index);
    }

    public boolean canInputFirstOrLast(int index, Section section) {
        if (index == 0) {
           return canInputFirst(section);
        }
        if (index == sections.size()-1) {
            return canInputLast(section);
        }
        sections.get(index).isSmallDistance(section.getDistance());
        return false;
    }

    public boolean canInputFirst(Section section) {
        if (section.getDownStation().equals(sections.get(0).getUpStation())) {
            sections.add(0, section);
            return true;
        }
        sections.get(0).isSmallDistance(section.getDistance());
        return false;
    }

    public boolean canInputLast(Section section) {
        if (section.getUpStation().equals(sections.get(sections.size()-1).getDownStation())) {
            sections.add(section);
            return true;
        }
        sections.get(sections.size()-1).isSmallDistance(section.getDistance());
        return false;
    }

    public void inputMiddleUpStation(boolean isUpdate, Section section, int index) {
        if (!isUpdate) {
            sections.add(index + 1, section);
            sections.get(index + 2).updateUpStation(section.getDownStation());
        }
    }

    public void inputMiddleDownStation(boolean isUpdate, Section section, int index) {
        if (!isUpdate) {
            sections.add(index+1, section);
            sections.get(index).updateDownStation(section.getUpStation());
        }
    }

    public List<Section> findStation(Station station) {
        return sections.stream()
                .filter(it -> it.getDownStation().equals(station) || it.getUpStation().equals(station))
                .collect(Collectors.toList());
    }

    public void validDuplicationSection(Section section) {
        for (Section currentSection : sections) {
            currentSection.validDuplication(section);
        }
    }
}
