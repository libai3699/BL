package com.common.core.id.service.impl;


import com.common.core.id.common.IdNoConstants;
import com.common.core.id.common.IdNoTypeEnum;
import com.common.core.id.service.IdNoGenerateService;
import com.common.core.id.util.IdNoSerialUtil;
import lombok.Data;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 单号生成接口实现
 *
 * @author mengqiang
 * @version FormNoGenerateServiceImpl.java, v 1.0 2019-01-01 18:10
 */
@Data
public class IdNoGenerateServiceImpl implements IdNoGenerateService {
    /**
     * redis 服务
     * demo 项目没有加redis相关，若有需要请参考，redis的博客
     */
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 根据单据编号类型 生成单据编号
     *
     * @param idNoTypeEnum 单据编号类型
     * @author mengqiang
     * @date 2019/1/1
     */
    @Override
    public String generateFormNo(IdNoTypeEnum idNoTypeEnum) {
        //获得单号前缀
        //格式 固定前缀 +时间前缀 示例 ：YF20190101
        String formNoPrefix = IdNoSerialUtil.getFormNoPrefix(idNoTypeEnum);
        //获得缓存key
        String cacheKey = IdNoSerialUtil.getCacheKey(formNoPrefix);
        //获得当日自增数
        Long incrementalSerial = stringRedisTemplate.opsForValue().increment(cacheKey);
        //设置失效时间 7天
        stringRedisTemplate.expire(cacheKey, IdNoConstants.DEFAULT_CACHE_DAYS, TimeUnit.DAYS);
        //组合单号并补全流水号
        String serialWithPrefix = IdNoSerialUtil
                .completionSerial(formNoPrefix, incrementalSerial, idNoTypeEnum);
        //补全随机数
        return IdNoSerialUtil.completionRandom(serialWithPrefix, idNoTypeEnum);
    }

    /**
     * 根据单据编号类型 生成单据编号
     *
     * @author mengqiang
     * @date 2019/1/1
     */
    @Override
    public String generatePayNo() {
        return generateFormNo(IdNoTypeEnum.NYP_ORDER);
    }
    /**
     * 根据单据编号类型 生成单据编号
     *
     * @author mengqiang
     * @date 2019/1/1
     */
    @Override
    public String generateWithdrowNo() {
        return generateFormNo(IdNoTypeEnum.NYW_ORDER);
    }
}
