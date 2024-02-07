package blueclub.server.notice.controller;

import blueclub.server.global.response.BaseResponse;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.notice.dto.request.UpdateNoticeRequest;
import blueclub.server.notice.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("")
    public ResponseEntity<BaseResponse> addNotice(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateNoticeRequest updateNoticeRequest
    ) {
        noticeService.addNotice(updateNoticeRequest);
        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PatchMapping("/{noticeId}")
    public ResponseEntity<BaseResponse> updateNotice(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("noticeId") Long id,
            @Valid @RequestBody UpdateNoticeRequest updateNoticeRequest
    ) {
        noticeService.updateNotice(id, updateNoticeRequest);
        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.SUCCESS);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<BaseResponse> deleteNotice(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("noticeId") Long id
    ) {
        noticeService.deleteNotice(id);
        return BaseResponse.toResponseEntity(BaseResponseStatus.DELETED);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public ResponseEntity<BaseResponse> getNoticeList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "noticeId", defaultValue = "-1", required = false) Long id
    ) {
        return BaseResponse.toResponseEntityContainsResult(noticeService.getNoticeList(id));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{noticeId}")
    public ResponseEntity<BaseResponse> getNoticeDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("noticeId") Long id
    ) {
        return BaseResponse.toResponseEntityContainsResult(noticeService.getNoticeDetails(id));
    }
}
