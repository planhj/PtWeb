package com.example.ptweb.controller.auth;

import com.example.ptweb.entity.EmailChangeToken;
import com.example.ptweb.service.EmailChangeTokenService;
import com.example.ptweb.service.MailService;
import java.time.LocalDateTime;
import com.example.ptweb.controller.auth.dto.request.ChangeEmailRequestDTO;
import cn.dev33.satoken.stp.StpUtil;
import com.example.ptweb.entity.User;
import com.example.ptweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import com.example.ptweb.entity.UserMonthlyStats;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
@RestController
@RequestMapping("/users") // 统一入口路径
public class UserController {

    @Autowired
    private UserService userService;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/avatars/";

    @Autowired
    private EmailChangeTokenService emailChangeTokenService;

    @Autowired
    private MailService mailService;


    @PutMapping("/modify")
    public String updateUser(@RequestBody User user) {
        long userId = StpUtil.getLoginIdAsLong();
        User dbUser = userService.getUser(userId);
        if (dbUser == null) {
            return "User not found.";
        }
        if (user.getUsername() != null) {
            dbUser.setUsername(user.getUsername());
        }
        if (user.getSignature() != null) {
            dbUser.setSignature(user.getSignature());
        }
        userService.updateUser(dbUser);
        return "User updated successfully.";
    }

    @PostMapping("/change-email")
    public String requestChangeEmail(@RequestBody ChangeEmailRequestDTO request) {
        long userId = StpUtil.getLoginIdAsLong();
        String token = UUID.randomUUID().toString();
        String newEmail = request.getNewEmail();
        emailChangeTokenService.saveToken(userId, newEmail, token, LocalDateTime.now().plusMinutes(30));
        mailService.send(newEmail, "邮箱验证", "请点击链接验证: http://localhost:3000/verifyEmail?token=" + token);
        return "验证邮件已发送";
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token) {
        // 查找 token
        EmailChangeToken changeToken = emailChangeTokenService.getByToken(token);
        if (changeToken == null || changeToken.getExpireTime().isBefore(LocalDateTime.now())) {
            return "链接无效或已过期";
        }
        User user = userService.getUser(changeToken.getUserId());
        user.setEmail(changeToken.getNewEmail());
        userService.updateUser(user);
        emailChangeTokenService.deleteByToken(token);
        return "邮箱修改成功";
    }

    @PostMapping("/avatar")
    public ResponseEntity<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

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

            // 获取当前登录用户ID
            long userId = StpUtil.getLoginIdAsLong();
            User user = userService.getUser(userId);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            user.setAvatar(fileUrl);
            userService.updateUser(user);

            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }

    // 手动触发考核接口
    @PostMapping("/assessment")
    public String manualAssessment() {
        userService.assessUsers();
        return "用户考核已执行";
    }

    @GetMapping("/monthly-stats")
    public ResponseEntity<UserMonthlyStats> getMonthlyStats() {
        long userId = StpUtil.getLoginIdAsLong(); // 获取当前登录用户ID
        UserMonthlyStats stats = userService.getCurrentMonthStats(userId);
        if (stats == null) {
            stats = new UserMonthlyStats();
            stats.setUserId(userId);
            stats.setMonth(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")));
            stats.setUploaded(0L);
            stats.setSeedingTime(0L);
        }
        return ResponseEntity.ok(stats);
    }
}
