package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.AActivityShow;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 活动总揽Mapper接口
 *
 * @author axing
 * @date 2024-08-08
 */
public interface AActivityShowMapper extends BaseMapper<AActivityShow>
{
    /**
     * 查询活动总揽
     *
     * @param id 活动总揽ID
     * @return 活动总揽
     */
    public AActivityShow selectAActivityShowById(Long id);

    /**
     * 查询活动总揽列表
     *
     * @param aActivityShow 活动总揽
     * @return 活动总揽集合
     */
    public List<AActivityShow> selectAActivityShowList(AActivityShow aActivityShow);

    /**
     * 新增活动总揽
     *
     * @param aActivityShow 活动总揽
     * @return 结果
     */
    public int insertAActivityShow(AActivityShow aActivityShow);

    /**
     * 修改活动总揽
     *
     * @param aActivityShow 活动总揽
     * @return 结果
     */
    public int updateAActivityShow(AActivityShow aActivityShow);

    /**
     * 删除活动总揽
     *
     * @param id 活动总揽ID
     * @return 结果
     */
    public int deleteAActivityShowById(Long id);

    /**
     * 批量删除活动总揽
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteAActivityShowByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
