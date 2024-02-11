package blueclub.server.diary.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetDiaryIdResponse {
    private Long id;
}
