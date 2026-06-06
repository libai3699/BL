package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.RedEnvelopeCover;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 红包封面Mapper接口
 *
 * @author axing
 * @date 2024-12-25
 */
public interface RedEnvelopeCoverMapper extends BaseMapper<RedEnvelopeCover>
{
    /**
     * 查询红包封面
     *
     * @param id 红包封面ID
     * @return 红包封面
     */
    public RedEnvelopeCover selectRedEnvelopeCoverById(Integer id);

    /**
     * 查询红包封面列表
     *
     * @param redEnvelopeCover 红包封面
     * @return 红包封面集合
     */
    public List<RedEnvelopeCover> selectRedEnvelopeCoverList(RedEnvelopeCover redEnvelopeCover);

    /**
     * 查询红包封面列表数量
     *
     * @param redEnvelopeCover 红包封面
     * @return 红包封面集合数量
     */
    public int selectRedEnvelopeCoverListCount(RedEnvelopeCover redEnvelopeCover);

    /**
     * 新增红包封面
     *
     * @param redEnvelopeCover 红包封面
     * @return 结果
     */
    public int insertRedEnvelopeCover(RedEnvelopeCover redEnvelopeCover);

    /**
     * 修改红包封面
     *
     * @param redEnvelopeCover 红包封面
     * @return 结果
     */
    public int updateRedEnvelopeCover(RedEnvelopeCover redEnvelopeCover);

    /**
     * 删除红包封面
     *
     * @param id 红包封面ID
     * @return 结果
     */
    public int deleteRedEnvelopeCoverById(Integer id);

    /**
     * 批量删除红包封面
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteRedEnvelopeCoverByIds(Integer[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
