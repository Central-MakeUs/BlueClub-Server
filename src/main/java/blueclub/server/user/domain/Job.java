package blueclub.server.user.domain;

import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponseStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Job {
    CADDY("GOLF_CADDY", "골프 캐디"),
    RIDER("DELIVERY_RIDER", "배달 라이더"),
    DELIVERY("DELIVERY_MAN", "택배 기사"),
    CONSULTANT("FINANCIAL_CONSULTANT", "보험 설계사"),
    ETC("ETC_FREELANCER", "기타·프리랜서");

    private final String key;
    private final String title;

    public static Job findByTitle(String title) {
        for (Job job : Job.values()) {
            if (job.getTitle().equals(title)) {
                return job;
            }
        }
        throw new BaseException(BaseResponseStatus.JOB_NOT_FOUND_ERROR);
    }
}
