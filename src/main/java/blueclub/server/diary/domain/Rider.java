package blueclub.server.diary.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Rider {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", referencedColumnName = "id")
    private Diary diary;

    private Long numberOfDeliveries;

    private Long incomeOfDeliveries;

    @ColumnDefault("0")
    private Long numberOfPromotions;

    @ColumnDefault("0")
    private Long incomeOfPromotions;

    public void update(Long numberOfDeliveries, Long incomeOfDeliveries, Long numberOfPromotions, Long incomeOfPromotions) {
        this.numberOfDeliveries = numberOfDeliveries;
        this.incomeOfDeliveries = incomeOfDeliveries;
        this.numberOfPromotions = numberOfPromotions;
        this.incomeOfPromotions = incomeOfPromotions;
    }
}
