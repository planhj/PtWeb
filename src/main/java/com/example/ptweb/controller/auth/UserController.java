package com.example.ptweb.controller.auth;

import cn.dev33.satoken.stp.StpUtil;
import com.example.ptweb.entity.User;
import com.example.ptweb.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
@Slf4j
@RestController
@RequestMapping("/users") // 统一入口路径
public class UserController {

    @Autowired
    private UserService userService;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/avatars/";


    @PutMapping("/{id}")
    public String updateUser(@PathVariable long id, @RequestBody User user) {
        if (id != user.getId()) {
            return "User ID in path and body do not match.";
        }
        userService.updateUser(user);
        return "User updated successfully.";
    }

    @PostMapping("/avatar")
    public ResponseEntity<String> uploadAvatar( @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        log.info('[' + "{}]头像", file.getOriginalFilename());
        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create upload directory");
                }
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File destFile = new File(uploadDir, fileName);
            file.transferTo(destFile);

            String fileUrl = "/resources/avatars/" + fileName;

            User user = userService.getUser(StpUtil.getLoginIdAsLong());
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            user.setAvatar(fileUrl);
            userService.updateUser(user);

            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            e.printStackTrace(); // 输出错误堆栈信息
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }
}
