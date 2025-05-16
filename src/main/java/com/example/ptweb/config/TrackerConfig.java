package com.example.ptweb.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.util.List;

@Data
@AllArgsConstructor
public class TrackerConfig {
    private List<String> trackerURL;
    private long maxTorrentSize;
    private int torrentIntervalMin;
    private int torrentIntervalMax;
    private boolean ipAddressWhitelistMode;
    private List<String> controlIps;
    private boolean portWhiteListMode;
    private List<Integer> controlPorts;
    private String torrentPrefix;

    @NotNull
    public static String getConfigKey() {
        return "tracker";
    }

    @NotNull
    public static TrackerConfig spawnDefault() {
        String localIp = getLocalIp(); // 获取本机IP地址
        return new TrackerConfig(
                List.of("http://" + localIp + ":8081/api/announce"), // 动态生成 URL
                -1,
                60 * 60 * 15,
                60 * 60 * 45,
                false,
                List.of(),
                false,
                List.of(20, 21, 22, 23, 25, 80, 110, 119, 161, 162, 443, 445, 1433, 1521, 2049, 3306, 3389, 8080, 8081),
                "PtWeb"
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
