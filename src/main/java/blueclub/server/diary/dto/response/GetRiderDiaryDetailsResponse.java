package blueclub.server.diary.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record GetRiderDiaryDetailsResponse(
        String worktype,
        String memo,
        List<String> imageUrlList,
        Long income,
        Long expenditure,
        Long saving,

        Long numberOfDeliveries,
        Long incomeOfDeliveries,
        Long numberOfPromotions,
        Long incomeOfPromotions
) {
}
