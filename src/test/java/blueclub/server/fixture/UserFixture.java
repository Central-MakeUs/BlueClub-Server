package blueclub.server.fixture;

import blueclub.server.auth.domain.Role;
import blueclub.server.auth.domain.SocialType;
import blueclub.server.user.domain.Job;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserFixture {
    WIZ(1L, "wiz@naver.com", "김위즈", "위즈", "010-0000-0000",
            "https://github.com/Central-MakeUs/BlueClub-Server/assets/90232934/ae25da66-5bfa-44be-acbc-6e15cb949198",
            Job.CADDY, 700000L, true, Role.USER, SocialType.NAVER, "testsocialid");

    private final Long id;
    private final String email;
    private final String name;
    private final String nickname;
    private final String phoneNumber;
    private final String profileImage;
    private final Job job;
    private final Long monthlyTargetIncome;
    private final boolean tosAgree;
    private final Role role;
    private final SocialType socialType;
    private final String socialId;
}
