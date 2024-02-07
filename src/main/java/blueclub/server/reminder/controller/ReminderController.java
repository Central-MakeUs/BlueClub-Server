package blueclub.server.reminder.controller;

import blueclub.server.global.response.BaseResponse;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.reminder.dto.request.UpdateReminderRequest;
import blueclub.server.reminder.service.ReminderService;
import com.google.firebase.messaging.FirebaseMessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reminder")
public class ReminderController {

    private final ReminderService reminderService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("")
    public ResponseEntity<BaseResponse> addReminder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateReminderRequest updateReminderRequest
    ) throws FirebaseMessagingException {
        reminderService.addReminder(updateReminderRequest);
        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PatchMapping("/{reminderId}")
    public ResponseEntity<BaseResponse> updateReminder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("reminderId") Long id,
            @Valid @RequestBody UpdateReminderRequest updateReminderRequest
    ) {
        reminderService.updateReminder(id, updateReminderRequest);
        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.SUCCESS);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping("/{reminderId}")
    public ResponseEntity<BaseResponse> deleteReminder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("reminderId") Long id
    ) {
        reminderService.deleteReminder(id);
        return BaseResponse.toResponseEntity(BaseResponseStatus.DELETED);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public ResponseEntity<BaseResponse> getReminderList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "reminderId", defaultValue = "-1", required = false) Long id
    ) {
        return BaseResponse.toResponseEntityContainsResult(reminderService.getReminderList(id));
    }
}
