package blueclub.server.diary.repository;

import blueclub.server.diary.domain.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderRepository extends JpaRepository<Rider, Long> {
}
