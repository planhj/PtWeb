package com.example.ptweb.controller.InviteCode;

import cn.dev33.satoken.stp.StpUtil;
import com.example.ptweb.entity.InviteCode;
import com.example.ptweb.entity.User;
import com.example.ptweb.exception.APIErrorCode;
import com.example.ptweb.exception.APIGenericException;
import com.example.ptweb.service.InviteCodeService;
import com.example.ptweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invite-code")
@RequiredArgsConstructor
public class InviteCodeController {

    private final InviteCodeService inviteCodeService;
    private final UserService userService;

    /**
     * 生成邀请码
     */

    @PostMapping("/generate")
    public void generateInviteCode() {
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getUser(userId);

        if (user == null) {
            throw new APIGenericException(APIErrorCode.REQUIRED_AUTHENTICATION, "User not found.");
        }

        // 调用服务生成邀请码
        inviteCodeService.generateInviteCode(userId);
    }

    /**
     * 查看当前用户生成的邀请码（可选功能）
     */
    @GetMapping("/my-codes")
    public List<InviteCode> getMyInviteCodes() {
        Long userId = StpUtil.getLoginIdAsLong();
        return inviteCodeService.getInviteCodesByCreatorId(userId);
    }
}

