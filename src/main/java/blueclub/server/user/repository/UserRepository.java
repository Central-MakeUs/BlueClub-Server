package blueclub.server.user.repository;

import blueclub.server.auth.domain.SocialType;
import blueclub.server.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByNickname(String nickname);
    Boolean existsByEmail(String email);
    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}