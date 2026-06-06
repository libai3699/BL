package com.gp.common.mybatisplus.service;

import java.util.List;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.AActivityShowMapper;
import com.gp.common.mybatisplus.entity.AActivityShow;
import com.gp.common.mybatisplus.service.AActivityShowService;

/**
 * 活动总揽Service业务层处理
 *
 * @author axing
 * @date 2024-08-08
 */
@Service
public class AActivityShowService extends ServiceImpl<AActivityShowMapper, AActivityShow>
{
    @Autowired
    private AActivityShowMapper aActivityShowMapper;

    /**
     * 查询活动总揽
     *
     * @param id 活动总揽ID
     * @return 活动总揽
     */

    public AActivityShow selectAActivityShowById(Long id)
    {
        return aActivityShowMapper.selectAActivityShowById(id);
    }

    /**
     * 查询活动总揽列表
     *
     * @param param 活动总揽
     * @return 活动总揽
     */

    public List<AActivityShow> selectAActivityShowList(AActivityShow param)
    {
        return aActivityShowMapper.selectAActivityShowList(param);
    }

    /**
     * 新增活动总揽
     *
     * @param param 活动总揽
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertAActivityShow(AActivityShow param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改活动总揽
     *
     * @param param 活动总揽
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAActivityShow(AActivityShow param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除活动总揽
     *
     * @param ids 需要删除的活动总揽ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAActivityShowByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除活动总揽信息
     *
     * @param id 活动总揽ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAActivityShowById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
