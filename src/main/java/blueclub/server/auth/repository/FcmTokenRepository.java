package blueclub.server.auth.repository;

import blueclub.server.auth.domain.FcmToken;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FcmTokenRepository extends CrudRepository<FcmToken, Long> {
    @Override
    List<FcmToken> findAll();
}
