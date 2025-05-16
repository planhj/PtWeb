package com.example.ptweb.controller.auth;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.example.ptweb.controller.auth.dto.request.LoginRequestDTO;
import com.example.ptweb.controller.auth.dto.request.RegisterRequestDTO;
import com.example.ptweb.controller.dto.response.LoginStatusResponseDTO;
import com.example.ptweb.controller.dto.response.UserResponseDTO;
import com.example.ptweb.controller.dto.response.UserSessionResponseDTO;
import com.example.ptweb.entity.User;
import com.example.ptweb.exception.APIErrorCode;
import com.example.ptweb.exception.APIGenericException;
import com.example.ptweb.service.AuthenticationService;
import com.example.ptweb.service.UserService;
import com.example.ptweb.type.PrivacyLevel;
import com.example.ptweb.util.IPUtil;
import com.example.ptweb.util.PasswordHash;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.ptweb.exception.APIErrorCode.AUTHENTICATION_FAILED;
import static com.example.ptweb.exception.APIErrorCode.MISSING_PARAMETERS;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public UserSessionResponseDTO login(@RequestBody LoginRequestDTO login) {
        String ip = IPUtil.getRequestIp(request);
        if (StringUtils.isEmpty(login.getUser())) {
            throw new APIGenericException(MISSING_PARAMETERS, "User parameter is required");
        }
        if (StringUtils.isEmpty(login.getPassword())) {
            throw new APIGenericException(MISSING_PARAMETERS, "Password parameter is required");
        }
        User user = userService.getUserByUsername(login.getUser());
        if (user == null) user = userService.getUserByEmail(login.getUser());
        if (user == null) {
            log.info("IP {} tried to login with not exists username {}.",ip, login.getUser());
            authenticationService.markUserLoginFail(ip); // Mark fail because it not use authenticate
            authenticationService.checkAccountLoginAttempts(ip);
            throw new APIGenericException(AUTHENTICATION_FAILED);
        }

        if(!authenticationService.authenticate(user,login.getPassword(),ip)){
            log.info("IP {} tried to login to user {} with bad password.",ip, login.getUser());
            throw new APIGenericException(AUTHENTICATION_FAILED);
        }

        StpUtil.login(user.getId());
        return getUserBasicInformation(user);
    }

    @PostMapping("/logout")
    public Map<String, Object> logout() {
        if (StpUtil.isLogin()) {
            StpUtil.logout();
            Map<String, Object> logoutResponse = new LinkedHashMap<>();
            logoutResponse.put("status", "ok");
            return logoutResponse;
        } else {
            throw new APIGenericException(APIErrorCode.REQUIRED_AUTHENTICATION);
        }
    }

    @GetMapping("/status")
    public LoginStatusResponseDTO status() {
        try {
            User user = userService.getUser(StpUtil.getLoginIdAsLong());
            if (user == null) {
                return new LoginStatusResponseDTO(false, false, false, null);
            } else {
                return new LoginStatusResponseDTO(true, true, false, getUserBasicInformation(user));
            }
        } catch (NotLoginException e) {
            return new LoginStatusResponseDTO(false, false, false, null);
        }
    }

    @PostMapping("/register")
    @Transactional
    public UserSessionResponseDTO register(@RequestBody RegisterRequestDTO register) {
        log.info("Received register request: {}", register.getEmail());
        if (StringUtils.isEmpty(register.getEmail())) {
            throw new APIGenericException(MISSING_PARAMETERS, "Email parameter is required");
        }
        if (StringUtils.isEmpty(register.getUsername())) {
            throw new APIGenericException(MISSING_PARAMETERS, "Username parameter is required");
        }
        if (StringUtils.isEmpty(register.getPassword())) {
            throw new APIGenericException(MISSING_PARAMETERS, "Password parameter is required");
        }
        User user = userService.getUserByUsername(register.getUsername());
        if (user != null) {
            throw new APIGenericException(APIErrorCode.USERNAME_ALREADY_IN_USAGE);
        }
        user = userService.getUserByEmail(register.getEmail());
        if (user != null) {
            throw new APIGenericException(APIErrorCode.EMAIL_ALREADY_IN_USAGE);
        }
        user = userService.save(new User(
                0,
                register.getEmail(),
                PasswordHash.hash(register.getPassword()),
                register.getUsername(),
                UUID.randomUUID().toString(),
                Timestamp.from(Instant.now()),
                "https://www.baidu.com/facivon.ico",
                "测试用户",
                "这个用户很懒，还没有个性签名",
                "100mbps",
                "100mbps",
                0L, 0L, 0L, 0L,
                BigDecimal.ZERO,
                0,
                0L,
                UUID.randomUUID().toString(), PrivacyLevel.LOW));
        StpUtil.login(user.getId());
        return getUserBasicInformation(user);
    }

    @NotNull
    private UserSessionResponseDTO getUserBasicInformation(User user) {
        SaTokenInfo tokenInfo = null;
        try {
            tokenInfo = StpUtil.getTokenInfo();
        } catch (NotLoginException ignored) {

        }
        return new UserSessionResponseDTO(tokenInfo, new UserResponseDTO(user));
    }

}
