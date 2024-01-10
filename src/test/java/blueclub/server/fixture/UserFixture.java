package blueclub.server.fixture;

import blueclub.server.auth.domain.Role;
import blueclub.server.user.domain.Email;
import blueclub.server.user.domain.Job;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserFixture {
    WIZ(Email.from("wiz@naver.com"), "김위즈", "위즈", "010-0000-0000", "test.png",
            Job.CADDY, 2023, true, Role.USER);

    private final Email email;
    private final String name;
    private final String nickname;
    private final String phoneNumber;
    private final String profileImage;
    private final Job job;
    private final Integer jobStart;
    private final boolean tosAgree;
    private final Role role;
}
