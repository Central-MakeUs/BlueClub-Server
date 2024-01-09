package blueclub.server.user.domain;

import blueclub.server.auth.domain.Role;
import blueclub.server.auth.domain.SocialType;
import blueclub.server.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

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

    private Email email;

    private String password;

    private String name;

    private String nickname;

    private String phoneNumber;

    private String profileImage;

    private Job job;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate jobStart;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId; // 로그인한 소셜 타입의 식별자 값

    public void authorizeUser() {
        this.role = Role.USER;
    }
}
