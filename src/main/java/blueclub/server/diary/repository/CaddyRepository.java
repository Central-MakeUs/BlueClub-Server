package blueclub.server.diary.repository;

import blueclub.server.diary.domain.Caddy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaddyRepository extends JpaRepository<Caddy, Long> {
}
