package blueclub.server.fixture.diary;

import blueclub.server.diary.domain.Worktype;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static blueclub.server.user.domain.Job.DAYWORKER;

@Getter
@RequiredArgsConstructor
public enum DayworkerDiaryFixture {
    DAYWORKER_DIARY(Worktype.WORKING.getValue(), "memo3", 300000L, 0L, 0L, LocalDate.now().minusDays(2),
            List.of("https://blueclubs3.s3.ap-northeast-2.amazonaws.com/diary/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202023-06-13%20%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB%201.44.20.png"),
            DAYWORKER.getTitle(),
            "00건설", 300000L, "창호", 1.0);

    private final String worktype;
    private final String memo;
    private final Long income;
    private final Long expenditure;
    private final Long saving;
    private final LocalDate date;
    private final List<String> imageUrlList;
    private final String jobTitle;

    private final String place;
    private final Long dailyWage;
    private final String typeOfJob;
    private final Double numberOfWork;
}
