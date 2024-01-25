package blueclub.server.diary.domain;

import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponseStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Worktype {

    WORKING("근무"),
    LEAVE_EARLY("조퇴"),
    DAY_OFF("휴무");

    private final String key;

    public static Worktype findByKey(String key) {
        for (Worktype worktype : Worktype.values()) {
            if (worktype.getKey().equals(key)) {
                return worktype;
            }
        }
        throw new BaseException(BaseResponseStatus.WORKTYPE_NOT_FOUND_ERROR);
    }
}
