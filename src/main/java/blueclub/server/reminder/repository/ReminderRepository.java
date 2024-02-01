package blueclub.server.reminder.repository;

import blueclub.server.reminder.domain.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderRepository extends JpaRepository<Reminder, Long>, ReminderQueryRepository {
}
