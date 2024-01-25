package blueclub.server.monthlyGoal.domain;

import blueclub.server.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.YearMonth;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MonthlyGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`year_month`")
    private YearMonth yearMonth;

    private Long targetIncome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public void updateMonthlyGoal(Long targetIncome) {
        this.targetIncome = targetIncome;
    }
}
