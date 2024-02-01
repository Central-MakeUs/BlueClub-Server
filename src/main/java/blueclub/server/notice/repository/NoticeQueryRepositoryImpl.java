package blueclub.server.notice.repository;

import blueclub.server.notice.dto.response.GetNoticeListResponse;
import blueclub.server.notice.dto.response.QGetNoticeListResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static blueclub.server.notice.domain.QNotice.notice;

@Transactional
@RequiredArgsConstructor
public class NoticeQueryRepositoryImpl implements NoticeQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<GetNoticeListResponse> findNoticeList(LocalDateTime createAt, Integer pageSize) {
        return queryFactory.selectDistinct(new QGetNoticeListResponse(notice.id, notice.title, notice.content, notice.createAt))
                .from(notice)
                .where(notice.createAt.before(createAt))
                .orderBy(notice.createAt.desc())
                .limit(pageSize)
                .fetch();
    }
}
