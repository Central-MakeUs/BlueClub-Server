package blueclub.server.global.infra;

import blueclub.server.file.service.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/health/file")
@RequiredArgsConstructor
public class S3HealthCheck {

    private final S3UploadService s3UploadService;

    @PostMapping("")
    public ResponseEntity<String> create(
            @RequestPart(value = "testImage", required = false) MultipartFile multipartFile) {

        String fileName = "";
        if (multipartFile != null) { // 파일 업로드한 경우에만

            try { // 파일 업로드
                fileName = s3UploadService.upload(multipartFile, "test"); // S3 버킷의 images 디렉토리 안에 저장됨
                return ResponseEntity.ok(fileName);
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
        return ResponseEntity.ok("Least One Image Required.");
    }
}
