package com.gp.common.mybatisplus.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.RedPacketRainConfig;
import org.apache.ibatis.annotations.Param;

/**
 * 红包雨活动配置Mapper接口
 *
 * @author axing
 * @date 2025-07-24
 */
public interface RedPacketRainConfigMapper extends BaseMapper<RedPacketRainConfig>
{
    /**
     * 查询红包雨活动配置
     *
     * @param id 红包雨活动配置ID
     * @return 红包雨活动配置
     */
    public RedPacketRainConfig selectRedPacketRainConfigById(Long id);

    /**
     * 查询红包雨活动配置列表
     *
     * @param redPacketRainConfig 红包雨活动配置
     * @return 红包雨活动配置集合
     */
    public List<RedPacketRainConfig> selectRedPacketRainConfigList(RedPacketRainConfig redPacketRainConfig);

    /**
     * 新增红包雨活动配置
     *
     * @param redPacketRainConfig 红包雨活动配置
     * @return 结果
     */
    public int insertRedPacketRainConfig(RedPacketRainConfig redPacketRainConfig);

    /**
     * 修改红包雨活动配置
     *
     * @param redPacketRainConfig 红包雨活动配置
     * @return 结果
     */
    public int updateRedPacketRainConfig(RedPacketRainConfig redPacketRainConfig);

    /**
     * 删除红包雨活动配置
     *
     * @param id 红包雨活动配置ID
     * @return 结果
     */
    public int deleteRedPacketRainConfigById(Long id);

    /**
     * 批量删除红包雨活动配置
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteRedPacketRainConfigByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    RedPacketRainConfig queryRedPacketRainConfig(@Param("userLevel")Integer userLevel, @Param("codeAmount") BigDecimal codeAmount, @Param("rechargeAmount")BigDecimal rechargeAmount);

    RedPacketRainConfig queryRedPacketRainToday(@Param("userLevel")Integer userLevel, @Param("codeAmount") BigDecimal codeAmount, @Param("rechargeAmount")BigDecimal rechargeAmount,@Param("startTime") Date startTimeByDate);

        /**
         * 查询红包雨列表（不过滤充值条件和游戏类型打码量，用于在Java层动态过滤）
         */
        List<RedPacketRainConfig> queryRedPacketRainTodayList(@Param("userLevel") Integer userLevel,
                        @Param("codeAmount") BigDecimal codeAmount);
}
