package blueclub.server.user.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record AddUserDetailsRequest(
        @NotBlank(message = "닉네임을 입력해주세요")
        @Length(max = 10, message = "닉네임은 10글자 이하로 작성해주세요")
        String nickname,
        @NotBlank(message = "직업을 선택해주세요")
        String jobTitle,
        @Min(value = 100000, message = "월 수입 목표는 10만원 이상으로 입력해주세요")
        @Max(value = 99990000, message = "월 수입 목표는 9999만원 이하로 입력해주세요")
        Long monthlyTargetIncome,
        @NotNull(message = "선택약관 동의여부를 입력해주세요")
        Boolean tosAgree
) {
}
