package blueclub.server.user.service;

import blueclub.server.auth.service.JwtService;
import blueclub.server.user.domain.Job;
import blueclub.server.user.domain.User;
import blueclub.server.user.dto.request.AddUserDetailsRequest;
import blueclub.server.user.dto.request.UpdateUserDetailsRequest;
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

    public void addUserDetails(UserDetails userDetails, AddUserDetailsRequest addUserDetailsRequest) {
        User user = userFindService.findByUserDetails(userDetails);
        user.addDetails(
                addUserDetailsRequest.nickname(),
                Job.findByTitle(addUserDetailsRequest.jobTitle()),
                addUserDetailsRequest.monthlyTargetIncome(),
                addUserDetailsRequest.tosAgree()
        );
    }

    public void updateUserDetails(UserDetails userDetails, UpdateUserDetailsRequest updateUserDetailsRequest) {

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
