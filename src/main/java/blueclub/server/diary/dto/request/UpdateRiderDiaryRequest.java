package blueclub.server.diary.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRiderDiaryRequest extends UpdateDiaryRequest {

        @NotNull(message = "배달 건수는 필수입니다")
        @Min(value = 0, message = "배달 건수는 0건 이상으로 입력해주세요")
        @Max(value = 999, message = "배달 건수는 999건 이하로 입력해주세요")
        private Long numberOfDeliveries;

        @NotNull(message = "배달 수입은 필수입니다")
        private Long incomeOfDeliveries;

        @Min(value = 0, message = "프로모션 건수는 0건 이상으로 입력해주세요")
        @Max(value = 999, message = "프로모션 건수는 999건 이하로 입력해주세요")
        private Long numberOfPromotions;

        private Long incomeOfPromotions;
}
