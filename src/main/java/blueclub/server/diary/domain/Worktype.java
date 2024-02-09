package blueclub.server.diary.domain;

import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.global.utils.EnumConverter;
import blueclub.server.global.utils.EnumStandard;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public enum Worktype implements EnumStandard {

    WORKING("근무"),
    LEAVE_EARLY("조퇴"),
    DAY_OFF("휴무");

    private final String value;

    public static Worktype findByValue(String value) {
        for (Worktype worktype : Worktype.values()) {
            if (worktype.getValue().equals(value)) {
                return worktype;
            }
        }
        throw new BaseException(BaseResponseStatus.WORKTYPE_NOT_FOUND_ERROR);
    }

    @Converter
    public static class WorktypeConverter extends EnumConverter<Worktype> {
        public WorktypeConverter() {
            super(Worktype.class);
        }
    }
}
