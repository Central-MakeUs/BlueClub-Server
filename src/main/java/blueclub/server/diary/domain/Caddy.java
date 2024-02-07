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
public class Caddy {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", referencedColumnName = "id")
    private Diary diary;

    private Long rounding;

    private Long caddyFee;

    @ColumnDefault("0")
    private Long overFee;

    @ColumnDefault("false")
    private Boolean topdressing;

    public void update(Long rounding, Long caddyFee, Long overFee, Boolean topdressing) {
        this.rounding = rounding;
        this.caddyFee = caddyFee;
        this.overFee = overFee;
        this.topdressing = topdressing;
    }
}
