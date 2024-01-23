package blueclub.server.diary.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
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
}
