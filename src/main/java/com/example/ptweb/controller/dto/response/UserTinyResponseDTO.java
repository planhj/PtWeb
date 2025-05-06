package com.example.ptweb.controller.dto.response;

import com.example.ptweb.entity.User;
import com.example.ptweb.other.ResponsePojo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.annotation.Validated;

@EqualsAndHashCode(callSuper = true)
@Data
@Validated
public class UserTinyResponseDTO extends ResponsePojo {
    private long id;
    private String username;
    private String avatar;

    public UserTinyResponseDTO(@NotNull User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.avatar = user.getAvatar();
    }
}
