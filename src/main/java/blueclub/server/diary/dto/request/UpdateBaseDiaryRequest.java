package blueclub.server.diary.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.DEDUCTION,
        defaultImpl = UpdateBaseDiaryRequest.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(UpdateBaseDiaryRequest.class),
        @JsonSubTypes.Type(UpdateCaddyDiaryRequest.class),
        @JsonSubTypes.Type(UpdateRiderDiaryRequest.class),
        @JsonSubTypes.Type(UpdateDayworkerDiaryRequest.class)
})
@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBaseDiaryRequest {

    @NotBlank(message = "근무 형태는 필수입니다")
    private String worktype;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "근무 날짜는 필수입니다")
    private LocalDate date;
}
