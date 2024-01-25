package blueclub.server.diary.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record GetCaddyDiaryDetailsResponse (
        String worktype,
        String memo,
        List<String> imageUrlList,
        Long income,
        Long expenditure,
        Long saving,

        Long rounding,
        Long caddyFee,
        Long overFee,
        Boolean topdressing
) {
}
