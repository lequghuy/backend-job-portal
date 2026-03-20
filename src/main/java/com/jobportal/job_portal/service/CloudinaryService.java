package com.jobportal.job_portal.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.jobportal.job_portal.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String folderName) {
        try {
            // resource_type = "auto" giúp Cloudinary tự nhận diện ảnh (png, jpg) hoặc file
            // thô (pdf, docx)
            Map<String, Object> options = ObjectUtils.asMap(
                    "folder", "job_portal/" + folderName,
                    "resource_type", "auto",
                    "public_id", UUID.randomUUID().toString());

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);

            // Trả về đường link HTTPS trực tiếp của file trên Cloudinary
            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            log.error("Lỗi upload file lên Cloudinary: ", e);
            throw new ApiException("Không thể tải file lên hệ thống. Vui lòng thử lại!");
        }
    }
}