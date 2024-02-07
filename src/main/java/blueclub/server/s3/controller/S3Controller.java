package blueclub.server.s3.controller;

import blueclub.server.global.response.BaseResponse;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@PreAuthorize("hasRole('USER')")
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class S3Controller {

    private final UserService userService;

    @PutMapping("/profile")
    public ResponseEntity<BaseResponse> updateProfileImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart(value = "image", required = false) MultipartFile multipartFile
    ) {
        userService.uploadProfileImage(userDetails, multipartFile);
        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.SUCCESS);
    }
}
