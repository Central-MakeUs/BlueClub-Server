package blueclub.server.global;

import blueclub.server.global.config.JpaAuditingConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import({// QueryDslConfig.class,
        JpaAuditingConfig.class})
@ActiveProfiles("test")
public class RepositoryTest {
}
