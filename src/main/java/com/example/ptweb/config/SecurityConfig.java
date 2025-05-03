package com.example.ptweb.config;

import com.example.ptweb.type.GuestAccessBlocker;
import com.example.ptweb.type.GuestAccessRequirement;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
@Data
@AllArgsConstructor
public class SecurityConfig {
    private int maxIp;
    private int maxAuthenticationAttempts;
    private int maxPasskeyAuthenticationAttempts;
    private GuestAccessBlocker guestAccessBlocker;
    private boolean guestAccessRequirementAnyMode;
    private List<GuestAccessRequirement> guestAccessRequirement;
    private List<String> guestAccessSecret;
    private List<String> guestAccessReferer;
    private List<String> guestAccessUserAgentKeyword;
    private List<String> guestAccessIp;

    @NotNull
    public static String getConfigKey(){
        return "security";
    }
    @NotNull
    public static SecurityConfig spawnDefault(){
        return new SecurityConfig(10, 5,150, GuestAccessBlocker.NORMAL, false, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

}
