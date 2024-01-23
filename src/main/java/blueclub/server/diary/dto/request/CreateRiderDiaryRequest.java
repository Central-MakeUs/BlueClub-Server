package blueclub.server.diary.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
public record CreateRiderDiaryRequest(
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

        @NotNull(message = "배달 건수는 필수입니다")
        @Min(value = 0, message = "배달 건수는 0건 이상으로 입력해주세요")
        @Max(value = 999, message = "배달 건수는 999건 이하로 입력해주세요")
        Long numberOfDeliveries,
        @NotNull(message = "배달 수입은 필수입니다")
        Long incomeOfDeliveries,
        @Min(value = 0, message = "프로모션 건수는 0건 이상으로 입력해주세요")
        @Max(value = 999, message = "프로모션 건수는 999건 이하로 입력해주세요")
        Long numberOfPromotions,
        Long incomeOfPromotions
) {
}
