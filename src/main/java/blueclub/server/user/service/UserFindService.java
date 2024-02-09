package blueclub.server.user.service;

import blueclub.server.auth.domain.SocialType;
import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.user.domain.User;
import blueclub.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFindService {

    private final UserRepository userRepository;

    public User findByUserDetails(UserDetails userDetails) {
        return userRepository.findBySocialTypeAndSocialId(SocialType.valueOf(userDetails.getUsername()), userDetails.getPassword())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND_ERROR));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND_ERROR));
    }
}
