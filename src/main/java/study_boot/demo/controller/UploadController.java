package study_boot.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    @Value("${upload.path:D:/springboot_project/demo/uploads}")
    private String uploadPath;

    @Value("${server.port:2333}")
    private int serverPort;

    @PostMapping
    public Map<String, String> upload(@RequestParam("file") MultipartFile file) {
        Map<String, String> result = new HashMap<>();
        
        if (file.isEmpty()) {
            result.put("error", "文件为空");
            return result;
        }

        // 创建上传目录
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString().replace("-", "") + suffix;

        // 保存文件
        try {
            File dest = new File(uploadPath, filename);
            file.transferTo(dest);
            
            // 返回完整绝对路径
            String url = "http://localhost:" + serverPort + "/uploads/" + filename;
            result.put("url", url);
            return result;
        } catch (IOException e) {
            result.put("error", "文件上传失败: " + e.getMessage());
            return result;
        }
    }
}
