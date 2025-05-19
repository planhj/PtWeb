package com.example.ptweb.controller.signin;

import com.example.ptweb.service.SignInService;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sign-in")
public class SignInController {

    private final SignInService signInService;

    public SignInController(SignInService signInService) {
        this.signInService = signInService;
    }

    @PostMapping
    public ResponseEntity<?> signIn() {
        Long userId = StpUtil.getLoginIdAsLong(); // 使用 Sa-Token 获取当前登录用户 ID
        signInService.signIn(userId);
        return ResponseEntity.ok("签到成功");
    }
}

