package blueclub.server.diary.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Builder
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Dayworker {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", referencedColumnName = "id")
    private Diary diary;

    private String place;

    private Long dailyWage;

    private String typeOfJob;

    @ColumnDefault("0.0")
    private Double numberOfWork;

    public void update(String place, Long dailyWage, String typeOfJob, Double numberOfWork) {
        this.place = place;
        this.dailyWage = dailyWage;
        this.typeOfJob = typeOfJob;
        this.numberOfWork = numberOfWork;
    }
}
