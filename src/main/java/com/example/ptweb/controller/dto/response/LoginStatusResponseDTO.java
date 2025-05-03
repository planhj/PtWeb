package com.example.ptweb.controller.dto.response;

import com.example.ptweb.other.ResponsePojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
@Validated
public class LoginStatusResponseDTO extends ResponsePojo {
    private boolean isLoggedIn;
    private boolean isSafe;
    private boolean isSwitch;
    private UserSessionResponseDTO user;

}
