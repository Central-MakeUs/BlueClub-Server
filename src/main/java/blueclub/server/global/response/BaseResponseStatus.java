package blueclub.server.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BaseResponseStatus implements BaseResponseStatusImpl {
    /**
     * 100 : 진행 정보
     */

    /**
     * 200 : 요청 성공
     */
    SUCCESS(HttpStatus.OK, "SUCCESS", "요청에 성공했습니다."),
    CREATED(HttpStatus.CREATED, "CREATED", "요청에 성공했으며 리소스가 정상적으로 생성되었습니다."),
    ACCEPTED(HttpStatus.ACCEPTED, "ACCEPTED", "요청에 성공했으나 처리가 완료되지 않았습니다."),
    DELETED(HttpStatus.NO_CONTENT, "DELETED", "요청에 성공했으며 더 이상 응답할 내용이 존재하지 않습니다."),

    /**
     * 300 : 리다이렉션
     */
    SEE_OTHER(HttpStatus.SEE_OTHER, "REDIRECT_001", "다른 주소로 요청해주세요."),
    RETRY_REQUEST(HttpStatus.FOUND, "REDIRECT_002", "재발급된 AccessToken / RefreshToken 으로 재시도 해주세요."),

    /**
     * 400 : 요청 실패
     */
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "REQUEST_ERROR_001", "잘못된 요청입니다."),
    INVALID_INPUT_DTO(HttpStatus.BAD_REQUEST, "REQUEST_ERROR_002", "잘못된 DTO 형식입니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "REQUEST_ERROR_003", "로그인 후 이용해주세요."),
    INVALID_FILE(HttpStatus.BAD_REQUEST, "REQUEST_ERROR_004", "잘못된 File 형식입니다."),
    INVALID_AUTHORIZATION(HttpStatus.FORBIDDEN, "REQUEST_ERROR_005", "비정상적인 접근입니다."),
    INVALID_ENUM(HttpStatus.BAD_REQUEST, "REQUEST_ERROR_006", "변경할 수 없는 ENUM type 입니다."),
    TOO_MANY_REQUEST_ERROR(HttpStatus.TOO_MANY_REQUESTS, "REQUEST_ERROR_007", "요청이 너무 잦습니다. 잠시 후 다시 시도해주세요."),

    // Auth
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "AUTH_001", "이메일 형식이 올바르지 않습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "AUTH_002", "비밀번호 형식이 올바르지 않습니다."),
    DUPLICATED_NICKNAME(HttpStatus.CONFLICT, "AUTH_003", "해당 닉네임은 중복입니다."),

    // Member
    MEMBER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "MEMBER_001", "존재하지 않는 사용자입니다."),
    MEMBER_STATUS_NOT_VALID_ERROR(HttpStatus.NOT_FOUND, "MEMBER_002", "이미 삭제된 회원입니다"),
    JOB_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "MEMBER_003", "존재하지 않는 직업입니다."),

    // MonthlyGoal

    // Diary
    WORKTYPE_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "DIARY_001", "존재하지 않는 근무 형태입니다."),
    DIARY_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "DIARY_002", "존재하지 않는 근무 일지입니다."),
    DIARY_USER_NOT_MATCH_ERROR(HttpStatus.NOT_FOUND, "DIARY_003", "사용자의 근무 일지가 아닙니다."),
    JOB_USER_NOT_MATCH_ERROR(HttpStatus.NOT_FOUND, "DIARY_004", "입력하신 직업과 사용자의 직업이 일치하지 않습니다."),
    DIARY_ALREADY_EXISTS_ERROR(HttpStatus.NOT_FOUND, "DIARY_005", "이미 근무 일지가 존재하는 날짜입니다."),
    CANT_BOAST_DIARY_ERROR(HttpStatus.BAD_REQUEST, "DIARY_006", "해당 근무 일지는 자랑하기 기능을 이용할 수 없습니다."),

    // Notice
    NOTICE_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "NOTICE_001", "존재하지 않는 공지입니다."),

    // Reminder
    REMINDER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "REMINDER_001", "존재하지 않는 알림입니다."),


    /**
     * 500 : 응답 실패
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "RESPONSE_ERROR_001", "서버와의 연결에 실패했습니다."),
    BAD_GATEWAY(HttpStatus.BAD_GATEWAY, "RESPONSE_ERROR_002", "다른 서버로부터 잘못된 응답이 수신되었습니다."),
    INSUFFICIENT_STORAGE(HttpStatus.INSUFFICIENT_STORAGE, "RESPONSE_ERROR_003", "서버의 용량이 부족해 요청에 실패했습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}

