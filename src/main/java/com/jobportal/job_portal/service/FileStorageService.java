package com.jobportal.job_portal.service;

import com.jobportal.job_portal.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    // Thư mục chứa CV
    private final Path fileStorageLocation = Paths.get("uploads/resumes").toAbsolutePath().normalize();

    public FileStorageService() {
        try {
            // Tự động tạo thư mục nếu chưa tồn tại
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new ApiException("Không thể tạo thư mục để lưu trữ file tải lên.");
        }
    }

    public String storeFile(MultipartFile file) {
        // Kiểm tra file rỗng
        if (file.isEmpty()) {
            throw new ApiException("File tải lên trống!");
        }

        // Lấy tên file gốc
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Kiểm tra định dạng (Chỉ cho phép PDF hoặc DOC/DOCX)
        if (!originalFileName.endsWith(".pdf") && !originalFileName.endsWith(".doc")
                && !originalFileName.endsWith(".docx")) {
            throw new ApiException("Chỉ hỗ trợ định dạng PDF, DOC hoặc DOCX");
        }

        try {
            // Đổi tên file để tránh trùng lặp (dùng UUID)
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            // Copy file vào thư mục đích
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Trả về đường dẫn tương đối để lưu vào DB (VD: /uploads/resumes/xyz.pdf)
            return "/uploads/resumes/" + newFileName;

        } catch (IOException ex) {
            log.error("Lỗi khi lưu file: {}", ex.getMessage());
            throw new ApiException("Không thể lưu file. Vui lòng thử lại!");
        }
    }
}