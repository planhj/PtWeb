package com.example.ptweb.autoconfig;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;

@EnableMethodCache(basePackages = "com.example.ptweb")
@EnableCreateCacheAnnotation
public class JetcacheConfig {

}
