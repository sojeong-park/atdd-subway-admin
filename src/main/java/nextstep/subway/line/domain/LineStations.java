package nextstep.subway.line.domain;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class LineStations {
    @OneToMany
    @JoinColumn(name = "line_id")
    private List<LineStation> lineStations = new ArrayList<>();
}
