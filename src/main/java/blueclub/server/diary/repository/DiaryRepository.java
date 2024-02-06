package blueclub.server.diary.repository;

import blueclub.server.diary.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface DiaryRepository extends JpaRepository<Diary, Long>, DiaryQueryRepository {

    Boolean existsByWorkAt(LocalDate workAt);
}
