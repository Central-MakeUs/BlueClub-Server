package blueclub.server.user.controller;

import blueclub.server.global.response.BaseResponse;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.user.dto.request.AddUserDetailsRequest;
import blueclub.server.user.dto.request.UpdateAgreementRequest;
import blueclub.server.user.dto.request.UpdateUserDetailsRequest;
import blueclub.server.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/details")
    public ResponseEntity<BaseResponse> addUserDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddUserDetailsRequest addUserDetailsRequest
    ) {
        userService.addUserDetails(userDetails, addUserDetailsRequest);
        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.SUCCESS);
    }

    @DeleteMapping("/withdrawal")
    public ResponseEntity<BaseResponse> withdrawUser(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        userService.withdrawUser(userDetails);
        return BaseResponse.toResponseEntity(BaseResponseStatus.DELETED);
    }

    @PatchMapping("/details")
    public ResponseEntity<BaseResponse> updateUserDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateUserDetailsRequest updateUserDetailsRequest
    ) {
        userService.updateUserDetails(userDetails, updateUserDetailsRequest);
        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.SUCCESS);
    }

    @PostMapping("/agreement")
    public ResponseEntity<BaseResponse> updateAgreement(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateAgreementRequest updateAgreementRequest
    ) {
        userService.updateAgreement(userDetails, updateAgreementRequest);
        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.SUCCESS);
    }
}
