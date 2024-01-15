package blueclub.server.fixture;

import blueclub.server.auth.domain.Role;
import blueclub.server.auth.domain.SocialType;
import blueclub.server.user.domain.Job;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserFixture {
    WIZ(1L, "wiz@naver.com", "김위즈", "위즈", "010-0000-0000", "test.png",
            Job.CADDY, 2023, true, Role.USER, SocialType.KAKAO, "testsocialid");

    private final Long id;
    private final String email;
    private final String name;
    private final String nickname;
    private final String phoneNumber;
    private final String profileImage;
    private final Job job;
    private final Integer jobStart;
    private final boolean tosAgree;
    private final Role role;
    private final SocialType socialType;
    private final String socialId;
}
