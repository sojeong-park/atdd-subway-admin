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
        for (Section currentSection : sections) {
            currentSection.validDuplication(section);
            //일치하는 항목이 하나라도 있으면  멈춘다. 그자리에 추가한다. 너무 많은 상황을 고려하지 말고 중간에 역이 들어가는것만 생각해본다.
            //어디에 추가할지 인덱스 반환하기.
            // 상행과 일치하면 하행의 station으로 업데이트하기
            if (currentSection.canAddSection(section.getUpStation())) {
                //기존 구간 뒤에 넣기
                int index1 = sections.indexOf(currentSection);
                result.put("index", index1);
                result.put("station", section.getDownStation());
                result.put("stationPosition", "up");
                System.out.println("상행선과 일치"+index1+","+currentSection.getUpStation().getName()+","+currentSection.getDownStation().getName());
                break;
//                List<Section> tmp = sections.subList(0, index);
//                tmp.add(section);
//                List<Section> tmp2 = sections.subList(index+1, sections.size());
//                tmp2.get(0).updateDownStation(section.getUpStation());
//                result =  Stream.concat(tmp.stream(), tmp2.stream())
//                        .collect(Collectors.toList());
            }
            //하행과 일치하면 상행의 station으로 업데이트하기 -> 이게 신규값
            if (currentSection.canAddSection(section.getDownStation())) {
                //기존 구간 앞에 넣기
                int index2 = sections.indexOf(currentSection);
                result.put("index", index2);
                result.put("station", section.getUpStation());
                result.put("stationPosition", "down");
                System.out.println("하행선과 일치"+index2+","+currentSection.getDownStation().getName());
                break;
            }
        }
        return result;
    }

    public List<Section> splitList(int startIndex, int endIndex) {
        return sections.stream()
                .collect(Collectors.toList())
                .subList(startIndex, endIndex);
    }
}
