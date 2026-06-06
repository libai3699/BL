package com.common.core.util;


import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

@Component
public class Ip2RegionUtil {

    private volatile Searcher searcher;

    @Value("classpath:ip2region.xdb")
    private Resource dbResource;

    @PostConstruct
    public void init() throws IOException {
        // 以 URLConnection 方式禁用 jar 缓存，并确保关闭流
        URLConnection conn = dbResource.getURL().openConnection();
        conn.setUseCaches(false);
        byte[] cBuff;
        try (InputStream in = conn.getInputStream()) {
            cBuff = FileCopyUtils.copyToByteArray(in);
        }
        this.searcher = Searcher.newWithBuffer(cBuff);
    }

    public String getRegion(String ipAddr) {
        try {
            return this.searcher.search(ipAddr);
        } catch (Exception e) {
            return "无法获取地理位置信息"; // 也可以抛业务异常
        }
    }

    @PreDestroy
    public void destroy() {
        if (this.searcher != null) {
            try { this.searcher.close(); } catch (Exception ignore) {}
        }
    }
}
