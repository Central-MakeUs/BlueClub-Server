package blueclub.server.auth.controller;

import blueclub.server.auth.domain.SocialType;
import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/auth/{socialType}")
    public ResponseEntity<?> redirect(@PathVariable String socialType) {
        HttpHeaders headers = new HttpHeaders();
        if (socialType.equals(SocialType.KAKAO.toString().toLowerCase())) {
            headers.setLocation(URI.create("/oauth2/authorization/kakao"));
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        }
        if (socialType.equals(SocialType.NAVER.toString().toLowerCase())) {
            headers.setLocation(URI.create("/oauth2/authorization/naver"));
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        }
        throw new BaseException(BaseResponseStatus.BAD_GATEWAY);
    }
}
