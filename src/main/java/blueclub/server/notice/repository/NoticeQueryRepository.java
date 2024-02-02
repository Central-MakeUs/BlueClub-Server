package blueclub.server.notice.repository;

import blueclub.server.notice.dto.response.GetNoticeListResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface NoticeQueryRepository {
    List<GetNoticeListResponse> findNoticeList(LocalDateTime createAt, Integer pageSize);
}
