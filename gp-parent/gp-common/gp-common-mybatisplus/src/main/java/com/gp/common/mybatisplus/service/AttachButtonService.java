package com.gp.common.mybatisplus.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.AttachButtonMapper;
import com.gp.common.mybatisplus.entity.AttachButton;
import com.gp.common.mybatisplus.service.AttachButtonService;

/**
 * 附加按钮Service业务层处理
 *
 * @author axing
 * @date 2025-06-09
 */
@Service
public class AttachButtonService extends ServiceImpl<AttachButtonMapper, AttachButton>
{
    @Autowired
    private AttachButtonMapper attachButtonMapper;

    /**
     * 查询附加按钮
     *
     * @param id 附加按钮ID
     * @return 附加按钮
     */

    public AttachButton selectAttachButtonById(Integer id)
    {
        return attachButtonMapper.selectAttachButtonById(id);
    }

    /**
     * 查询附加按钮列表
     *
     * @param param 附加按钮
     * @return 附加按钮
     */

    public List<AttachButton> selectAttachButtonList(AttachButton param)
    {
        return attachButtonMapper.selectAttachButtonList(param);
    }

    /**
     * 新增附加按钮
     *
     * @param param 附加按钮
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertAttachButton(AttachButton param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改附加按钮
     *
     * @param param 附加按钮
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAttachButton(AttachButton param)
    {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除附加按钮
     *
     * @param ids 需要删除的附加按钮ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAttachButtonByIds(Integer[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除附加按钮信息
     *
     * @param id 附加按钮ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAttachButtonById(Integer id)
    {
        boolean result = this.removeById(id);
        return result;
    }

    public List<AttachButton> queryAttachButton(String language) {
        LambdaQueryWrapper<AttachButton> q = new LambdaQueryWrapper<>();
        q.eq(AttachButton::getLanKey, language);
        q.orderByAsc(AttachButton::getSort);
        return attachButtonMapper.selectList(q);
    }
}
