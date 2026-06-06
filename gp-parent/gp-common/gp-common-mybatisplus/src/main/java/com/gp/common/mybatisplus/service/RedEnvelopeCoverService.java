package com.gp.common.mybatisplus.service;

import java.util.List;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.RedEnvelopeCoverMapper;
import com.gp.common.mybatisplus.entity.RedEnvelopeCover;
import com.gp.common.mybatisplus.service.RedEnvelopeCoverService;

/**
 * 红包封面Service业务层处理
 *
 * @author axing
 * @date 2024-12-25
 */
@Service
public class RedEnvelopeCoverService extends ServiceImpl<RedEnvelopeCoverMapper, RedEnvelopeCover>
{
    @Autowired
    private RedEnvelopeCoverMapper redEnvelopeCoverMapper;

    /**
     * 查询红包封面
     *
     * @param id 红包封面ID
     * @return 红包封面
     */

    public RedEnvelopeCover selectRedEnvelopeCoverById(Integer id)
    {
        return redEnvelopeCoverMapper.selectRedEnvelopeCoverById(id);
    }

    /**
     * 查询红包封面列表
     *
     * @param param 红包封面
     * @return 红包封面
     */

    public List<RedEnvelopeCover> selectRedEnvelopeCoverList(RedEnvelopeCover param)
    {
        return redEnvelopeCoverMapper.selectRedEnvelopeCoverList(param);
    }

    /**
     * 查询红包封面列表数量
     *
     * @param param 红包封面
     * @return 红包封面数量
     */
    public int selectRedEnvelopeCoverListCount(RedEnvelopeCover param) {
        return redEnvelopeCoverMapper.selectRedEnvelopeCoverListCount(param);
    }

    /**
     * 新增红包封面
     *
     * @param param 红包封面
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertRedEnvelopeCover(RedEnvelopeCover param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改红包封面
     *
     * @param param 红包封面
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateRedEnvelopeCover(RedEnvelopeCover param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除红包封面
     *
     * @param ids 需要删除的红包封面ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteRedEnvelopeCoverByIds(Integer[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除红包封面信息
     *
     * @param id 红包封面ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteRedEnvelopeCoverById(Integer id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
