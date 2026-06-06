package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.PayChannel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author axing
 * @date 2026-01-09
 */
public interface PayChannelMapper extends BaseMapper<PayChannel> {
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public PayChannel selectPayChannelById(Integer id);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param payChannel 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<PayChannel> selectPayChannelList(PayChannel payChannel);

    /**
     * 获取提现启用的通道(包含商户启用,并且支持提现)
     */
    List<PayChannel> listWithMerchant(@Param("payTypeId") Long payTypeId);

    /**
     * 新增【请填写功能名称】
     *
     * @param payChannel 【请填写功能名称】
     * @return 结果
     */
    public int insertPayChannel(PayChannel payChannel);

    /**
     * 修改【请填写功能名称】
     *
     * @param payChannel 【请填写功能名称】
     * @return 结果
     */
    public int updatePayChannel(PayChannel payChannel);

    /**
     * 删除【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deletePayChannelById(Integer id);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deletePayChannelByIds(Integer[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
