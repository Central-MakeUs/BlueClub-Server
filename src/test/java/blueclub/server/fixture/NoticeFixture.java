package blueclub.server.fixture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public enum NoticeFixture {

    FIRST_NOTICE(1L, "공지 제목1", "공지 내용1", LocalDateTime.now().minusDays(5)),
    SECOND_NOTICE(2L, "공지 제목2", "공지 내용2", LocalDateTime.now().minusDays(4)),
    THIRD_NOTICE(3L, "공지 제목3", "공지 내용3", LocalDateTime.now().minusDays(3)),
    FOURTH_NOTICE(4L, "공지 제목4", "공지 내용4", LocalDateTime.now().minusDays(2)),
    FIFTH_NOTICE(5L, "공지 제목5", "공지 내용5", LocalDateTime.now().minusDays(1));

    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createAt;
}
