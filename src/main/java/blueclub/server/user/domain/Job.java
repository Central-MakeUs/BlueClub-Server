package blueclub.server.user.domain;

import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponseStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Job {
    CADDY("GOLF_CADDY", "골프캐디"),
    RIDER("DELIVERY_RIDER", "배달라이더"),
    DAYWORKER("DAYWORKER", "일용직노동자");

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
