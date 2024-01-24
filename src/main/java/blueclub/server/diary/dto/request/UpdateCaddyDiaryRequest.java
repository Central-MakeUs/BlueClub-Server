package blueclub.server.diary.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCaddyDiaryRequest extends UpdateDiaryRequest {

        @NotNull(message = "라운딩 수는 필수입니다")
        @Min(value = 0, message = "라운딩 수는 0건 이상으로 입력해주세요")
        @Max(value = 999, message = "라운딩 수는 999건 이하로 입력해주세요")
        private Long rounding;

        @NotNull(message = "캐디피 수입은 필수입니다")
        private Long caddyFee;

        private Long overFee;

        private Boolean topdressing;
}
