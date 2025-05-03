package com.example.ptweb.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.List;

@Data
@AllArgsConstructor
public class SiteBasicConfig {
    private String siteName;
    private String siteSubName;
    private String siteBaseURL;
    private String siteDescription;
    private List<String> siteKeywords;
    private boolean openRegistration;
    private boolean maintenanceMode;

    @NotNull
    public static String getConfigKey() {
        return "site_basic";
    }

    @NotNull
    public static SiteBasicConfig spawnDefault() {
        String localIp = getLocalIp(); // 获取本机IP地址
        return new SiteBasicConfig(
                "Another Sapling Site",
                "未配置的站点",
                "http://" + localIp + ":8081", // 动态生成 siteBaseURL
                "又一个由 Sapling 驱动的站点！",
                List.of("BitTorrent", "Torrent", "File Sharing", "Private Tracker"),
                false,
                false
        );
    }

    private static String getLocalIp() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress(); // 获取本机的IP地址
        } catch (Exception e) {
            e.printStackTrace();
            return "127.0.0.1"; // 如果无法获取本机IP，返回默认值
        }
    }
}
