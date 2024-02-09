package blueclub.server.fixture.diary;

import blueclub.server.diary.domain.Worktype;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static blueclub.server.user.domain.Job.CADDY;

@Getter
@RequiredArgsConstructor
public enum CaddyDiaryFixture {
    CADDY_DIARY(10L, Worktype.WORKING.getValue(), "memo1", 200000L, 0L, 0L, LocalDate.now(),
            List.of("https://blueclubs3.s3.ap-northeast-2.amazonaws.com/diary/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-06-13%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%201.44.20.png"),
            CADDY.getTitle(),
            3L, 200000L, 0L, true),
    CADDY_DIARY_TWO(5L, Worktype.LEAVE_EARLY.getValue(), "memo1", 100000L, 0L, 0L, LocalDate.now().minusDays(5),
            List.of("https://blueclubs3.s3.ap-northeast-2.amazonaws.com/diary/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-06-13%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%201.44.20.png"),
            CADDY.getTitle(),
            1L, 200000L, 0L, true),
    CADDY_DIARY_THREE(1L, Worktype.WORKING.getValue(), "memo1", 320000L, 0L, 0L, LocalDate.now().minusDays(10),
            List.of("https://blueclubs3.s3.ap-northeast-2.amazonaws.com/diary/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-06-13%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%201.44.20.png"),
            CADDY.getTitle(),
            4L, 200000L, 0L, true),
    CADDY_DIARY_FOUR(6L, Worktype.WORKING.getValue(), "memo1", 550000L, 0L, 0L, LocalDate.now().minusDays(3),
            List.of("https://blueclubs3.s3.ap-northeast-2.amazonaws.com/diary/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-06-13%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%201.44.20.png"),
            CADDY.getTitle(),
            6L, 200000L, 0L, true);

    private final Long diaryId;
    private final String worktype;
    private final String memo;
    private final Long income;
    private final Long expenditure;
    private final Long saving;
    private final LocalDate date;
    private final List<String> imageUrlList;
    private final String jobTitle;

    private final Long rounding;
    private final Long caddyFee;
    private final Long overFee;
    private final Boolean topdressing;
}
