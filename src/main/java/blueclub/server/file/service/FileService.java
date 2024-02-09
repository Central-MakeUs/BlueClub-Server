package blueclub.server.file.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {

    public List<String> getHomeBanner(HttpServletRequest request) {
        return List.of(
                request.getRequestURL().toString().replace(request.getRequestURI(), "") + "/image/home/banner.png"
        );
    }
}
