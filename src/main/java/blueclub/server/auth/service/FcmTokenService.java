package blueclub.server.auth.service;

import blueclub.server.auth.domain.FcmToken;
import blueclub.server.auth.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;

    public void saveFcmToken(Long userId, String fcmToken) {
        fcmTokenRepository.deleteById(userId);
        fcmTokenRepository.save(FcmToken.createFcmToken(userId, fcmToken));
    }

    public void deleteFcmToken(Long userId) {
        fcmTokenRepository.deleteById(userId);
    }

    public List<FcmToken> findAllOnlineUsers() {
        return fcmTokenRepository.findAll();
    }
}
