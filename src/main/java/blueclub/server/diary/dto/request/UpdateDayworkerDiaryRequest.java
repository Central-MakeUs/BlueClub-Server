package blueclub.server.diary.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDayworkerDiaryRequest extends UpdateDiaryRequest {

        @NotBlank(message = "현장명은 필수입니다")
        @Length(max = 10, message = "현장명은 10글자 이하로 작성해주세요")
        private String place;

        @NotNull(message = "일당은 필수입니다")
        private Long dailyWage;

        @Length(max = 10, message = "직종은 10글자 이하로 작성해주세요")
        private String typeOfJob;

        @DecimalMin(value = "0.0", message = "공수는 0.0 이상으로 입력해주세요")
        @DecimalMax(value = "3.0", message = "공수는 3.0 이하로 입력해주세요")
        private Double numberOfWork;
}
