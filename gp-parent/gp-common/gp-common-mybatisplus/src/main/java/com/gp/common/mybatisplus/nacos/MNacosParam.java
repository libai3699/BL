package com.gp.common.mybatisplus.nacos;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.common.core.util.Ip2RegionUtil;
import com.common.core.util.RedisUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.utils.IpUtil;
import com.gp.common.mybatisplus.entity.DomainConfig;
import com.gp.common.mybatisplus.service.DomainConfigService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author axing
 * @version 1.0
 * @date 2022/11/12 16:24
 */
@RefreshScope
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class MNacosParam {

    @Value("${walletKeySecretKey:dsdasdawwedwds}")
    public String walletKeySecretKey;
    @Value("${signSecretKey:dsdasdawwedwds}")
    public String signSecretKey;
    @Value("${fileAdmin}")
    public String fileAdmin;

    @Resource
    private Ip2RegionUtil ip2RegionUtil;

    @Resource
    private DomainConfigService domainConfigService;

    @Resource
    private RedisUtil redisUtil;

    // 缓存过期时间配置（单位：秒）
    private static final int CACHE_EXPIRE_TIME = 60 * 60;

    /**
     * 动态获取图片域名
     *
     * @return 域名
     */
    public String getFileAdmin() {
        //如果是总库直接返回配置文件
        String dbCode = CecuUtil.getDbCode();
        String dbName = CecuUtil.getDbName();
        if ((StringUtils.isNotBlank(dbName) && dbName.equals("yh_ft_control")) ||
                (StringUtils.isBlank(dbName) && StringUtils.isBlank(dbCode))) {
            return fileAdmin;
        }

        try {
            // 获取省份信息
            String ipRegion = ip2RegionUtil.getRegion(IpUtil.getRealIpAddr());
            if ("无法获取地理位置信息".equals(ipRegion)) {
                log.warn("无法获取IP地理位置信息，使用默认配置");
                return getDefaultFileAdmin(1);
            }

            String[] regionParts = ipRegion.split("\\|");
            if (regionParts.length < 3) {
                log.warn("IP地理位置信息格式不正确，使用默认配置");
                return getDefaultFileAdmin(1);
            }

            String province = regionParts[2];
            log.info("解析到省份信息：{}", province);

            // 查询缓存或数据库
            return getDomainFromCacheOrDb(province, 1);

        } catch (Exception e) {
            log.error("获取文件管理域名失败，IP地址：{}，异常信息：{}", IpUtil.getRealIpAddr(), e.getMessage(), e);
            return getDefaultFileAdmin(1);
        }
    }

    /**
     * 动态获取web网页域名
     *
     * @return 域名
     */
    public String getWebUrl() {
        //如果是总库直接返回配置文件
        String dbCode = CecuUtil.getDbCode();
        String dbName = CecuUtil.getDbName();
        if ((StringUtils.isNotBlank(dbName) && dbName.equals("yh_ft_control")) ||
                (StringUtils.isBlank(dbName) && StringUtils.isBlank(dbCode))) {
            return fileAdmin;
        }
        try {
            // 获取省份信息
            String ipRegion = ip2RegionUtil.getRegion(IpUtil.getRealIpAddr());
            if ("无法获取地理位置信息".equals(ipRegion)) {
                log.warn("无法获取IP地理位置信息，使用默认配置");
                return getDefaultFileAdmin(2);
            }

            String[] regionParts = ipRegion.split("\\|");
            if (regionParts.length < 3) {
                log.warn("IP地理位置信息格式不正确，使用默认配置");
                return getDefaultFileAdmin(2);
            }

            String province = regionParts[2];
            log.info("解析到省份信息：{}", province);

            // 查询缓存或数据库
            return getDomainFromCacheOrDb(province, 2);

        } catch (Exception e) {
            log.error("获取文件管理域名失败，IP地址：{}，异常信息：{}", IpUtil.getRealIpAddr(), e.getMessage(), e);
            return getDefaultFileAdmin(2);
        }
    }

    /**
     * 获取默认域名
     *
     * @param domainType 域名类型 1 图片域名 2 网站域名
     * @return 域名
     */
    private String getDefaultFileAdmin(Integer domainType) {
        String province = "其他";
        log.info("使用默认省份配置：{}", province);
        return getDomainFromCacheOrDb(province, domainType);
    }

    /**
     * 从缓存或数据库获取域名配置
     *
     * @param province   省份信息
     * @param domainType 1 图片域名 2 网站域名
     * @return 域名配置
     */
    private String getDomainFromCacheOrDb(String province, Integer domainType) {
        String redisKey = RedisKey.imageDomainKey;
        if (domainType == 2) {
            redisKey = RedisKey.webDomainKey;
        }
        String key = StrUtil.format(redisKey, CecuUtil.getDbCode(), province);

        // 查询缓存
        if (redisUtil.exists(key)) {
            log.debug("从缓存中获取域名配置，key：{}", key);
            return redisUtil.get(key).toString();
        }

        // 查询数据库
        DomainConfig domainConfig = domainConfigService.getOne(
                Wrappers.lambdaQuery(DomainConfig.class)
                        .eq(DomainConfig::getDomainType, domainType)
                        .eq(DomainConfig::getRegionCode, province)
                        .eq(DomainConfig::getStatus, 1));

        if (domainConfig != null && org.apache.commons.lang3.StringUtils.isNotBlank(domainConfig.getDomainName())) {
            // 缓存有效数据
            redisUtil.set(key, domainConfig.getDomainName(), CACHE_EXPIRE_TIME);
            log.debug("从数据库中获取域名配置并缓存，key：{}，value：{}", key, domainConfig.getDomainName());
            return domainConfig.getDomainName();
        } else {
            if (!province.equals("其他")) {
                String other = StrUtil.format(redisKey, CecuUtil.getDbCode(), "其他");
                if (redisUtil.exists(other)) {
                    log.debug("从缓存中获取域名配置，key：{}", key);
                    String domainName = redisUtil.get(other).toString();
                    redisUtil.set(key, domainName, 120);
                    return domainName;
                }
                DomainConfig domainConfig1 = domainConfigService.getOne(
                        Wrappers.lambdaQuery(DomainConfig.class)
                                .eq(DomainConfig::getDomainType, domainType)
                                .eq(DomainConfig::getRegionCode, "其他"));
                if (domainConfig1 != null && org.apache.commons.lang3.StringUtils.isNotBlank(domainConfig1.getDomainName())) {
                    // 缓存有效数据
                    redisUtil.set(key, domainConfig1.getDomainName(), 120);
                    redisUtil.set(StrUtil.format(redisKey, CecuUtil.getDbCode(), "其他"), domainConfig1.getDomainName(), CACHE_EXPIRE_TIME);
                    log.debug("从数据库中获取域名配置并缓存，key：{}，value：{}", key, domainConfig1.getDomainName());
                    return domainConfig1.getDomainName();
                }
            }
        }
        return fileAdmin;
    }
}
