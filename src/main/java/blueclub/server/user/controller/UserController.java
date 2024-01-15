package blueclub.server.user.controller;

import blueclub.server.global.response.BaseResponse;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.user.dto.request.AddDetailsRequest;
import blueclub.server.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/details")
    public ResponseEntity<BaseResponse> addUserDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AddDetailsRequest addDetailsRequest) {
        userService.addUserDetails(userDetails, addDetailsRequest);
        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.SUCCESS);
    }

    @DeleteMapping("/withdrawal")
    public ResponseEntity<BaseResponse> withdrawUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.withdrawUser(userDetails);
        return BaseResponse.toResponseEntity(BaseResponseStatus.DELETED);
    }
}
