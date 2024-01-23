package blueclub.server.diary.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
public record CreateDayworkerDiaryRequest(
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

        @NotBlank(message = "현장명은 필수입니다")
        @Length(max = 10, message = "현장명은 10글자 이하로 작성해주세요")
        String place,
        @NotNull(message = "일당은 필수입니다")
        Long dailyWage,
        @Length(max = 10, message = "직종은 10글자 이하로 작성해주세요")
        String typeOfJob,
        @DecimalMin(value = "0.0", message = "공수는 0.0 이상으로 입력해주세요")
        @DecimalMax(value = "3.0", message = "공수는 3.0 이하로 입력해주세요")
        Double numberOfWork
) {
}
