package blueclub.server.diary.repository;

import blueclub.server.diary.domain.Dayworker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayworkerRepository extends JpaRepository<Dayworker, Long> {
}
