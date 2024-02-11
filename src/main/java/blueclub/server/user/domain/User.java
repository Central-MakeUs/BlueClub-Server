package blueclub.server.user.domain;

import blueclub.server.auth.domain.Role;
import blueclub.server.auth.domain.SocialType;
import blueclub.server.diary.domain.Diary;
import blueclub.server.global.entity.BaseTimeEntity;
import blueclub.server.monthlyGoal.domain.MonthlyGoal;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email", name = "unique_email"))
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String name;

    private String nickname;

    private String phoneNumber;

    private String profileImage;

    private Job job;

    private Long monthlyTargetIncome;

    private Boolean tosAgree;

    private Boolean pushAgree;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId; // 로그인한 소셜 타입의 식별자 값

    // 회원 탈퇴시 작성한 근무수첩 모두 삭제
    @OneToMany(mappedBy = "user", cascade = PERSIST, orphanRemoval = true)
    private List<Diary> diaryList = new ArrayList<>();

    // 회원 탈퇴시 작성한 월 목표 수입 모두 삭제
    @OneToMany(mappedBy = "user", cascade = PERSIST, orphanRemoval = true)
    private List<MonthlyGoal> monthlyGoalList = new ArrayList<>();

    public void authorizeUser() {
        this.role = Role.USER;
    }

    public void addDetails(String nickname, Job job, Long monthlyTargetIncome, Boolean tosAgree) {
        this.nickname = nickname;
        this.job = job;
        this.monthlyTargetIncome = monthlyTargetIncome;
        this.tosAgree = tosAgree;
        this.pushAgree = true;
        this.role = Role.USER;
    }

    public void updateDetails(String nickname, Job job, Long monthlyTargetIncome) {
        this.nickname = nickname;
        this.job = job;
        this.monthlyTargetIncome = monthlyTargetIncome;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updateAgreement(Boolean tosAgree, Boolean pushAgree) {
        this.tosAgree = tosAgree;
        this.pushAgree = pushAgree;
    }
}
