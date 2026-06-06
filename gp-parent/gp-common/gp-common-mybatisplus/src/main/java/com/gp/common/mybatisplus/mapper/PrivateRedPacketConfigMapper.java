package com.gp.common.mybatisplus.mapper;

import java.math.BigDecimal;
import java.util.List;
import com.gp.common.mybatisplus.entity.PrivateRedPacketConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 私人红包配置Mapper接口
 *
 * @author axing
 * @date 2025-07-25
 */
public interface PrivateRedPacketConfigMapper extends BaseMapper<PrivateRedPacketConfig>
{
    /**
     * 查询私人红包配置
     *
     * @param id 私人红包配置ID
     * @return 私人红包配置
     */
    public PrivateRedPacketConfig selectPrivateRedPacketConfigById(Long id);

    /**
     * 查询私人红包配置列表
     *
     * @param privateRedPacketConfig 私人红包配置
     * @return 私人红包配置集合
     */
    public List<PrivateRedPacketConfig> selectPrivateRedPacketConfigList(PrivateRedPacketConfig privateRedPacketConfig);

    /**
     * 新增私人红包配置
     *
     * @param privateRedPacketConfig 私人红包配置
     * @return 结果
     */
    public int insertPrivateRedPacketConfig(PrivateRedPacketConfig privateRedPacketConfig);

    /**
     * 修改私人红包配置
     *
     * @param privateRedPacketConfig 私人红包配置
     * @return 结果
     */
    public int updatePrivateRedPacketConfig(PrivateRedPacketConfig privateRedPacketConfig);

    /**
     * 删除私人红包配置
     *
     * @param id 私人红包配置ID
     * @return 结果
     */
    public int deletePrivateRedPacketConfigById(Long id);

    /**
     * 批量删除私人红包配置
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deletePrivateRedPacketConfigByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    List<PrivateRedPacketConfig> queryPrivateRedPacketConfigs(@Param("usrId") Long userId, @Param("userLevel") Integer userLevel, @Param("codeAmount") BigDecimal codeAmount, @Param("rechargeAmount") BigDecimal rechargeAmount);

    PrivateRedPacketConfig queryPrivateRedPacketConfig(@Param("usrId") Long userId,  @Param("privateRedId")Long privateRedId,  @Param("userLevel")Integer userLevel, @Param("codeAmount")BigDecimal codeAmount, @Param("rechargeAmount")BigDecimal rechargeAmount);
    /**
     * 查询私人红包列表（不过滤充值条件和游戏类型打码量，用于在Java层动态过滤）
     */
     List<PrivateRedPacketConfig> queryPrivateRedPacketConfigList(@Param("usrId") Long userId,
                                                                 @Param("userLevel") Integer userLevel);
}
