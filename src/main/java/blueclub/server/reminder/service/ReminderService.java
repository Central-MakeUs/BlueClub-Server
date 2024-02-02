package blueclub.server.reminder.service;

import blueclub.server.auth.domain.FcmToken;
import blueclub.server.auth.service.FcmTokenService;
import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.reminder.domain.Reminder;
import blueclub.server.reminder.dto.request.UpdateReminderRequest;
import blueclub.server.reminder.dto.response.GetReminderListResponse;
import blueclub.server.reminder.repository.ReminderRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReminderService {

    private final FcmTokenService fcmTokenService;
    private final ReminderRepository reminderRepository;
    private static final Integer PAGE_SIZE = 4;

    public void addReminder(UpdateReminderRequest updateReminderRequest) throws FirebaseMessagingException {
        Reminder reminder = Reminder.builder()
                .title(updateReminderRequest.title())
                .content(updateReminderRequest.content())
                .build();
        reminderRepository.save(reminder);

        List<FcmToken> fcmTokenList = fcmTokenService.findAllOnlineUsers();
        for (FcmToken fcmToken: fcmTokenList) {
            sendReminder(fcmToken.getToken(), updateReminderRequest.title(), updateReminderRequest.content());
        }
    }

    public void updateReminder(Long id, UpdateReminderRequest updateReminderRequest) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.REMINDER_NOT_FOUND_ERROR));
        reminder.updateReminder(updateReminderRequest.title(), updateReminderRequest.content());
    }

    public void deleteReminder(Long id) {
        if (!reminderRepository.existsById(id))
            throw new BaseException(BaseResponseStatus.REMINDER_NOT_FOUND_ERROR);
        reminderRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<GetReminderListResponse> getReminderList(Long id) {
        if (id == -1)
            return reminderRepository.findReminderList(LocalDateTime.now(), PAGE_SIZE);

        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.REMINDER_NOT_FOUND_ERROR));
        return reminderRepository.findReminderList(reminder.getCreateAt(), PAGE_SIZE);
    }

    private void sendReminder(String fcmToken, String title, String content) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(content)
                        .build())
                .setToken(fcmToken)
                .build();

        FirebaseMessaging.getInstance().send(message);
    }
}
