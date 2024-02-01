package blueclub.server.reminder.repository;

import blueclub.server.reminder.dto.response.GetReminderListResponse;
import blueclub.server.reminder.dto.response.QGetReminderListResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static blueclub.server.reminder.domain.QReminder.reminder;

@Transactional
@RequiredArgsConstructor
public class ReminderQueryRepositoryImpl implements ReminderQueryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<GetReminderListResponse> findReminderList(LocalDateTime createAt, Integer pageSize) {
        return queryFactory.selectDistinct(new QGetReminderListResponse(reminder.id, reminder.title, reminder.content, reminder.createAt))
                .from(reminder)
                .where(reminder.createAt.before(createAt))
                .orderBy(reminder.createAt.desc())
                .limit(pageSize)
                .fetch();
    }
}
