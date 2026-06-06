package com.gp.common.mybatisplus.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.gp.common.mybatisplus.dto.UserExtDTO;
import com.gp.common.mybatisplus.entity.UserExt;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.vo.HomeWaitRebateAmountExportVO;
import com.gp.common.mybatisplus.vo.HomeWaitReturnCommissionAmountExportVO;
import org.apache.ibatis.annotations.Param;

/**
 * 用户额外数据Mapper接口
 *
 * @author axing
 * @date 2024-05-13
 */
public interface UserExtMapper extends BaseMapper<UserExt>
{
    /**
     * 查询用户额外数据
     *
     * @param id 用户额外数据ID
     * @return 用户额外数据
     */
    public UserExt selectUserExtById(Long id);

    /**
     * 查询用户额外数据列表
     *
     * @param userExt 用户额外数据
     * @return 用户额外数据集合
     */
    public List<UserExt> selectUserExtList(UserExt userExt);

    /**
     * 新增用户额外数据
     *
     * @param userExt 用户额外数据
     * @return 结果
     */
    public int insertUserExt(UserExt userExt);

    /**
     * 修改用户额外数据
     *
     * @param userExt 用户额外数据
     * @return 结果
     */
    public int updateUserExt(UserExt userExt);

    /**
     * 删除用户额外数据
     *
     * @param id 用户额外数据ID
     * @return 结果
     */
    public int deleteUserExtById(Long id);

    /**
     * 批量删除用户额外数据
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserExtByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    Integer reduceUserExt(@Param("userId") Long userId, @Param("type") Integer type, @Param("num") BigDecimal num, @Param("signSecretKey") String signSecretKey, @Param("signTime") Long signTime);

    Map<String, Object> userExtCountEndMap(@Param("userIdList") List<Long> userIdList);

    Map<String, Object> userExtCountEndMapByChannelId(@Param("channelId") Long channelId);

    List<UserExtDTO> selectUserExtBatch(@Param("userIds") List<Long> userIds);

    /**
     * 待返水明细（每用户 type=4 汇总），为 0 的不返回，排序在 SQL 中完成
     */
    List<HomeWaitRebateAmountExportVO> queryWaitRebateAmountDetailByUser();

    /**
     * 待返佣明细（每用户 type=5 汇总），为 0 的不返回，排序在 SQL 中完成
     */
    List<HomeWaitReturnCommissionAmountExportVO> queryWaitReturnCommissionAmountDetailByUser();
}
