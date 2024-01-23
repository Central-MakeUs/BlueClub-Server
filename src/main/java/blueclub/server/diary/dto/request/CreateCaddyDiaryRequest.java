package blueclub.server.diary.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
public record CreateCaddyDiaryRequest(
        @NotBlank(message = "근무 형태는 필수입니다")
        String worktype,
        String memo,
        @NotNull(message = "총 수입은 필수입니다")
        Long income,
        Long expenditure,
        Long saving,
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "근무 날짜는 필수입니다")
        LocalDate date,

        @NotNull(message = "라운딩 수는 필수입니다")
        @Min(value = 0, message = "라운딩 수는 0건 이상으로 입력해주세요")
        @Max(value = 999, message = "라운딩 수는 999건 이하로 입력해주세요")
        Long rounding,
        @NotNull(message = "캐디피 수입은 필수입니다")
        Long caddyFee,
        Long overFee,
        Boolean topdressing
) {
}