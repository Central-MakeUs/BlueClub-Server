package blueclub.server.fixture;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

public class AuthenticationFixture {
    public static final Authentication authentication = new TestingAuthenticationToken(
            "wiz@naver.com",
            null,
            "ROLE_USER"
    );
}
