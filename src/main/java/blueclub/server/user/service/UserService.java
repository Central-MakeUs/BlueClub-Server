package blueclub.server.user.service;

import blueclub.server.auth.service.JwtService;
import blueclub.server.user.domain.Job;
import blueclub.server.user.domain.User;
import blueclub.server.user.dto.request.AddDetailsRequest;
import blueclub.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserFindService userFindService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public void addUserDetails(UserDetails userDetails, AddDetailsRequest addDetailsRequest) {
        User user = userFindService.findByUserDetails(userDetails);
        user.addDetails(
                addDetailsRequest.nickname(),
                Job.findByTitle(addDetailsRequest.jobTitle()),
                addDetailsRequest.monthlyTargetIncome(),
                addDetailsRequest.tosAgree()
        );
    }

    public void withdrawUser(UserDetails userDetails) {
        User user = userFindService.findByUserDetails(userDetails);
        deleteUser(user);
    }

    // user 관련 정보 삭제 (User, RefreshToken)
    private void deleteUser(User user) {
        jwtService.deleteRefreshToken(user.getId());
        userRepository.delete(user);
    }
}
