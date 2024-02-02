package blueclub.server.fixture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public enum ReminderFixture {

    FIRST_REMINDER(1L, "알림 제목1", "알림 내용1", LocalDateTime.now().minusDays(5)),
    SECOND_REMINDER(2L, "알림 제목2", "알림 내용2", LocalDateTime.now().minusDays(4)),
    THIRD_REMINDER(3L, "알림 제목3", "알림 내용3", LocalDateTime.now().minusDays(3)),
    FOURTH_REMINDER(4L, "알림 제목4", "알림 내용4", LocalDateTime.now().minusDays(2)),
    FIFTH_REMINDER(5L, "알림 제목5", "알림 내용5", LocalDateTime.now().minusDays(1));

    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createAt;
}
