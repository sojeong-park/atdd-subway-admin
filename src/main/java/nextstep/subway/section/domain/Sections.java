package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    private static final int FIRST_INDEX = 0;
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final String NOT_CORRECT_EXEPTION = "일치하는 역이 없어 구간을 추가할수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.ALL})
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
        addSectionListEqualDownStation(upStationSection, section);
        addSectionListEqualUpStation(upStationSection, section);
        addOneSectionList(section);
    }

    /**
     * 기존 구간이 1개만 있는 경우 신규 구간 추가
     * @param section
     */
    public void addOneSectionList(Section section) {
        if (sections.size() == ONE) {
            int upStationSize = findStation(section.getUpStation()).size();
            int downStationSize = findStation(section.getDownStation()).size();
            if (upStationSize == ONE) {
                canInputLast(section);
            }
            if (downStationSize == ONE) {
                canInputFirst(section);
            }
        }
    }

    /**
     * 기존 구간이 2개 이상이고 신규 구간의 상행선과 일치한경우
     * @param upStationSection
     * @param section
     */
    public void addSectionListEqualUpStation(List<Section> upStationSection, Section section) {
        if (sections.size() > ONE && upStationSection.size() >= ONE) {
            int index = sections.indexOf(upStationSection.get(FIRST_INDEX));
            boolean isUpdate = canInputFirstOrLast(index, section);
            inputMiddleUpStation(isUpdate, section, index);
        }
    }

    /**
     * 기존 구간이 2개 이상이고 신규 구간의 하행선과 일치한경우
     * @param upStationSection
     * @param section
     */
    public void addSectionListEqualDownStation(List<Section> upStationSection, Section section) {
        if (sections.size() > ONE && upStationSection.size() == ZERO) {
            addDownStation(section);
        }
    }

    public void addDownStation(Section section) {
        List<Section> hasDownStation = findStation(section.getDownStation());
        if (hasDownStation.size() == ZERO){
            throw new RuntimeException(NOT_CORRECT_EXEPTION);
        }
        int index = sections.indexOf(hasDownStation.get(FIRST_INDEX));
        boolean isUpdate = canInputFirstOrLast(index, section);
        inputMiddleDownStation(isUpdate, section, index);
    }

    public boolean canInputFirstOrLast(int index, Section section) {
        if (index == FIRST_INDEX) {
           return canInputFirst(section);
        }
        if (index == sections.size() - ONE) {
            return canInputLast(section);
        }
        isSmallDistance(index, section);
        return false;
    }

    public boolean canInputFirst(Section section) {
        if (section.getDownStation().equals(sections.get(FIRST_INDEX).getUpStation())) {
            sections.add(FIRST_INDEX, section);
            return true;
        }
        isSmallDistance(FIRST_INDEX, section);
        return false;
    }

    public boolean canInputLast(Section section) {
        boolean isUpdate = sameNewUpStationToOldDownStation(section);
        sameNewUpStationToOldUpStation(isUpdate, section);
        isSmallDistance(sections.size() - ONE, section);
        return false;
    }


    public boolean sameNewUpStationToOldDownStation(Section section) {
        Station inputNewUpStation = section.getUpStation();
        Section oldSection = sections.get(sections.size() - ONE);
        if (inputNewUpStation.equals(oldSection.getDownStation())) {
            sections.add(section);
            return true;
        }
        return false;
    }

    public boolean sameNewUpStationToOldUpStation(Boolean isUpdate, Section section) {
        Station inputNewUpStation = section.getUpStation();
        Station inputNewDownStation = section.getDownStation();
        Section oldSection = sections.get(sections.size() - ONE);
        if (!isUpdate && inputNewUpStation.equals(oldSection.getUpStation())) {
            sections.add(FIRST_INDEX, section);
            sections.get(ONE).updateUpStation(inputNewDownStation);
            return true;
        }
        return false;
    }
    public void inputMiddleUpStation(boolean isUpdate, Section section, int index) {
        if (!isUpdate) {
            sections.add(index + ONE, section);
            sections.get(index + TWO).updateUpStation(section.getDownStation());
        }
    }

    public void inputMiddleDownStation(boolean isUpdate, Section section, int index) {
        if (!isUpdate) {
            sections.add(index + ONE, section);
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

    public void isSmallDistance(int index, Section section) {
        sections.get(index).smallDistance(section.getDistance());
    }
}
