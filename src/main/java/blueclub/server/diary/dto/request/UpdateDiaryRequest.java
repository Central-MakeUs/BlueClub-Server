package blueclub.server.diary.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class UpdateDiaryRequest extends UpdateBaseDiaryRequest {

    private String memo;

    @NotNull(message = "총 수입은 필수입니다")
    private Long income;

    private Long expenditure;

    private Long saving;

    private List<String> imageUrlList;
}
