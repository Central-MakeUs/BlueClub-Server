package blueclub.server.notice.service;

import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.notice.domain.Notice;
import blueclub.server.notice.dto.request.UpdateNoticeRequest;
import blueclub.server.notice.dto.response.GetNoticeDetailsResponse;
import blueclub.server.notice.dto.response.GetNoticeListResponse;
import blueclub.server.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;

    private static final Integer PAGE_SIZE = 4;

    public void addNotice(UpdateNoticeRequest updateNoticeRequest) {
        Notice notice = Notice.builder()
                .title(updateNoticeRequest.title())
                .content(updateNoticeRequest.content())
                .build();
        noticeRepository.save(notice);
    }

    @Transactional(readOnly = true)
    public GetNoticeDetailsResponse getNoticeDetails(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOTICE_NOT_FOUND_ERROR));
        return GetNoticeDetailsResponse.builder()
                .title(notice.getTitle())
                .content(notice.getContent())
                .createAt(notice.getCreateAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<GetNoticeListResponse> getNoticeList(Long id) {
        if (id == -1)
            return noticeRepository.findNoticeList(LocalDateTime.now(), PAGE_SIZE);

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOTICE_NOT_FOUND_ERROR));
        return noticeRepository.findNoticeList(notice.getCreateAt(), PAGE_SIZE);
    }

    public void updateNotice(Long id, UpdateNoticeRequest updateNoticeRequest) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOTICE_NOT_FOUND_ERROR));
        notice.updateNotice(updateNoticeRequest.title(), updateNoticeRequest.content());
    }

    public void deleteNotice(Long id) {
        if (!noticeRepository.existsById(id))
            throw new BaseException(BaseResponseStatus.NOTICE_NOT_FOUND_ERROR);
        noticeRepository.deleteById(id);
    }
}
