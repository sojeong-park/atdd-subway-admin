package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import javax.persistence.*;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section(){}

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public void update(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void updateUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void updateDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public void validDuplication(Section section) {
       if (upStation.equals(section.upStation) && downStation.equals(section.downStation)) {
           throw new RuntimeException("동일한 구간은 추가할수 없습니다.");
       }
    }

    public boolean canAddSection(Station station) {
        if (station.equals(upStation) || station.equals(downStation)) {
            return true;
        }
        return false;
    }

    public void isSmallDistance(int distance) {
        if (this.distance < distance) {
            throw new RuntimeException("기존 구간보다 긴 거리의 구간은 입력 할 수 없습니다.");
        }
    }
}
