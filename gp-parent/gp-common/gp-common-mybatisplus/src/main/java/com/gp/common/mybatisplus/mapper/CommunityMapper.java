package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.Community;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 客服Mapper接口
 *
 * @author axing
 * @date 2024-03-13
 */
public interface CommunityMapper extends BaseMapper<Community>
{
    /**
     * 查询客服
     *
     * @param id 客服ID
     * @return 客服
     */
    public Community selectCommunityById(Integer id);

    /**
     * 查询客服列表
     *
     * @param community 客服
     * @return 客服集合
     */
    public List<Community> selectCommunityList(Community community);

    /**
     * 新增客服
     *
     * @param community 客服
     * @return 结果
     */
    public int insertCommunity(Community community);

    /**
     * 修改客服
     *
     * @param community 客服
     * @return 结果
     */
    public int updateCommunity(Community community);

    /**
     * 删除客服
     *
     * @param id 客服ID
     * @return 结果
     */
    public int deleteCommunityById(Integer id);

    /**
     * 批量删除客服
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteCommunityByIds(Integer[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
