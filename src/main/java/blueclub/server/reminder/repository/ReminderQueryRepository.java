package blueclub.server.reminder.repository;

import blueclub.server.reminder.dto.response.GetReminderListResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ReminderQueryRepository {
    List<GetReminderListResponse> findReminderList(LocalDateTime createAt, Integer pageSize);
}
