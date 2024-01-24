package blueclub.server.diary.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "jobType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "CADDY", value = UpdateCaddyDiaryRequest.class),
        @JsonSubTypes.Type(name = "RIDER", value = UpdateRiderDiaryRequest.class),
        @JsonSubTypes.Type(name = "DAYWORKER", value = UpdateDayworkerDiaryRequest.class)
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class UpdateDiaryRequest {

    @NotBlank(message = "근무 형태는 필수입니다")
    private String worktype;

    private String memo;

    @NotNull(message = "총 수입은 필수입니다")
    private Long income;

    private Long expenditure;

    private Long saving;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "근무 날짜는 필수입니다")
    private LocalDate date;

    private List<String> imageUrlList;

    public enum JobType {
        CADDY, RIDER, DAYWORKER;
    }
}
