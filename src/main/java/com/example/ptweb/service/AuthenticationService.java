package com.example.ptweb.service;

import com.example.ptweb.config.SecurityConfig;
import com.example.ptweb.entity.User;
import com.example.ptweb.exception.APIErrorCode;
import com.example.ptweb.exception.APIGenericException;
import com.example.ptweb.other.RedisLoginAttempt;
import com.example.ptweb.redisrepository.RedisLoginAttemptRepository;
import com.example.ptweb.type.LoginType;
import com.example.ptweb.util.IPUtil;
import com.example.ptweb.util.PasswordHash;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AuthenticationService {
    @Autowired
    private UserService userService;
    @Autowired
    private LoginHistoryService loginHistoryService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private RedisLoginAttemptRepository repository;
    @Autowired
    private SettingService settingService;

    private SecurityConfig getSecurityConfig() {
        return settingService.get(SecurityConfig.getConfigKey(), SecurityConfig.class);
    }

    public boolean authenticate(@NotNull User user, @NotNull String password, @Nullable String ipAddress) {
        checkAccountLoginAttempts(ipAddress);
        boolean verify = PasswordHash.verify(password, user.getPassword());
        if (StringUtils.isEmpty(ipAddress)) {
            ipAddress = IPUtil.getRequestIp(request);
        }
        if (verify) {
            cleanUserLoginFail(ipAddress);
            loginHistoryService.log(user, LoginType.ACCOUNT, ipAddress, request.getHeader("User-Agent"));
        } else {
            markUserLoginFail(ipAddress);
        }
        return verify;
    }

    @Nullable
    public User authenticate(@NotNull String passkey, @Nullable String ipAddress) {
        checkPasskeyLoginAttempts(ipAddress);
        User user = userService.getUserByPasskey(passkey);
        if (StringUtils.isEmpty(ipAddress)) {
            ipAddress = IPUtil.getRequestIp(request);
        }
        if (user != null) {
            cleanUserLoginFail(ipAddress);
            loginHistoryService.log(user, LoginType.PASSKEY, ipAddress, request.getHeader("User-Agent"));
        } else {
            markUserLoginFail(ipAddress);
        }
        return user;
    }

    public void cleanUserLoginFail(@Nullable String ip) {
        if(ip == null) return;
        Optional<RedisLoginAttempt> optional = repository.findByIp(ip);
        optional.ifPresent(redisLoginAttempt -> repository.delete(redisLoginAttempt));
    }

    public long markUserLoginFail(@Nullable String ip) {
        if(ip == null) return 0;
        Optional<RedisLoginAttempt> optional = repository.findByIp(ip);
        RedisLoginAttempt loginAttempt;
        if (optional.isPresent()) {
            loginAttempt = optional.get();
            loginAttempt.setAttempts(loginAttempt.getAttempts() + 1);
        } else {
            loginAttempt = new RedisLoginAttempt();
            loginAttempt.setIp(ip);
        }
        loginAttempt.setLastAttempt(System.currentTimeMillis());
        loginAttempt = repository.save(loginAttempt);
        return loginAttempt.getAttempts();
    }

    public void checkAccountLoginAttempts(@Nullable String ip){
        if(ip == null) return;
        if (getUserFail(ip) > getSecurityConfig().getMaxAuthenticationAttempts()) {
            throw new APIGenericException(APIErrorCode.TOO_MANY_FAILED_AUTHENTICATION_ATTEMPTS, "Too many failed login attempts");
        }
    }
    public void checkPasskeyLoginAttempts(@Nullable String ip){
        if(ip == null) return;
        if (getUserFail(ip) > getSecurityConfig().getMaxPasskeyAuthenticationAttempts()) {
            throw new APIGenericException(APIErrorCode.TOO_MANY_FAILED_AUTHENTICATION_ATTEMPTS, "Too many failed login attempts");
        }
    }

    public long getUserFail(@Nullable String ip) {
        if (ip == null) return 0;
        Optional<RedisLoginAttempt> optional = repository.findByIp(ip);
        long attempts = optional.map(RedisLoginAttempt::getAttempts).orElse(0L);
        return attempts;
    }
}
