package in.devopsbuddy.web.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    String uploadProfileImage(MultipartFile imageFile, String username) throws IOException;
}
