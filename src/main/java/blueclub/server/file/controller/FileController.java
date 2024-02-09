package blueclub.server.file.controller;

import blueclub.server.file.service.FileService;
import blueclub.server.global.response.BaseResponse;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@PreAuthorize("hasRole('USER')")
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final UserService userService;
    private final FileService fileService;

    @PutMapping("/profile")
    public ResponseEntity<BaseResponse> updateProfileImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart(value = "image", required = false) MultipartFile multipartFile
    ) {
        userService.uploadProfileImage(userDetails, multipartFile);
        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.SUCCESS);
    }

    @GetMapping("/home/banner")
    public ResponseEntity<BaseResponse> getHomeBanner(
            HttpServletRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return BaseResponse.toResponseEntityContainsResult(fileService.getHomeBanner(request));
    }
}
