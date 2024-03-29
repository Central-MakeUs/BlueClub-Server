package blueclub.server.diary.domain;

import blueclub.server.global.entity.BaseTimeEntity;
import blueclub.server.user.domain.Job;
import blueclub.server.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Builder
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Diary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = Worktype.WorktypeConverter.class)
    private Worktype worktype;

    private String memo;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> image;

    @ColumnDefault("0")
    private Long income;

    @ColumnDefault("0")
    private Long expenditure;

    @ColumnDefault("0")
    private Long saving;

    private LocalDate workAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private Job job;

    @OneToOne(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private Caddy caddy;

    @OneToOne(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private Rider rider;

    @OneToOne(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private Dayworker dayworker;

    public void setCaddy(Caddy caddy) {
        this.caddy = caddy;
    }

    public void setRider(Rider rider) {
        this.rider = rider;
    }

    public void setDayworker(Dayworker dayworker) {
        this.dayworker = dayworker;
    }

    public void update(Worktype worktype, String memo, List<String> image, Long income, Long expenditure, Long saving, Job job) {
        this.worktype = worktype;
        this.memo = memo;
        this.image = image;
        this.income = income;
        this.expenditure = expenditure;
        this.saving = saving;
        this.job = job;
    }

    public void unlink() {
        this.caddy = null;
        this.rider = null;
        this.dayworker = null;
    }
}
