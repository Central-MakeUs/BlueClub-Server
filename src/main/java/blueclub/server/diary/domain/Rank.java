package blueclub.server.diary.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Rank {

    UNDER_FIVE_PERCENT("상위 5%"),
    UNDER_THIRTY_PERCENT("상위 30%"),
    ELSE("기타");

    private final String key;
}
