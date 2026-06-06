package com.gp.common.mybatisplus.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.core.constant.CommonConstant;
import com.common.core.util.StringUtils;
import com.gp.common.base.constant.DelConstants;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.mybatisplus.entity.Banner;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Locale;

import com.gp.common.mybatisplus.mapper.HomepagePopupMapper;
import com.gp.common.mybatisplus.entity.HomepagePopup;
import com.gp.common.mybatisplus.service.HomepagePopupService;

import javax.annotation.Resource;

/**
 * 首页弹窗Service业务层处理
 *
 * @author axing
 * @date 2024-10-21
 */
@Service
public class HomepagePopupService extends ServiceImpl<HomepagePopupMapper, HomepagePopup>
{
    @Autowired
    private HomepagePopupMapper homepagePopupMapper;

    /**
     * 查询首页弹窗
     *
     * @param id 首页弹窗ID
     * @return 首页弹窗
     */

    public HomepagePopup selectHomepagePopupById(Long id)
    {
        return homepagePopupMapper.selectHomepagePopupById(id);
    }

    /**
     * 查询首页弹窗列表
     *
     * @param param 首页弹窗
     * @return 首页弹窗
     */

    public List<HomepagePopup> selectHomepagePopupList(HomepagePopup param)
    {
        return homepagePopupMapper.selectHomepagePopupList(param);
    }

    /**
     * 新增首页弹窗
     *
     * @param param 首页弹窗
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertHomepagePopup(HomepagePopup param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改首页弹窗
     *
     * @param param 首页弹窗
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateHomepagePopup(HomepagePopup param)
    {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除首页弹窗
     *
     * @param ids 需要删除的首页弹窗ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteHomepagePopupByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除首页弹窗信息
     *
     * @param id 首页弹窗ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteHomepagePopupById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }

}
