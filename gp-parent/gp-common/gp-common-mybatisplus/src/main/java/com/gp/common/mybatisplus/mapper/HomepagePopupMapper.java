package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.HomepagePopup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 首页弹窗Mapper接口
 *
 * @author axing
 * @date 2024-10-21
 */
public interface HomepagePopupMapper extends BaseMapper<HomepagePopup>
{
    /**
     * 查询首页弹窗
     *
     * @param id 首页弹窗ID
     * @return 首页弹窗
     */
    public HomepagePopup selectHomepagePopupById(Long id);

    /**
     * 查询首页弹窗列表
     *
     * @param homepagePopup 首页弹窗
     * @return 首页弹窗集合
     */
    public List<HomepagePopup> selectHomepagePopupList(HomepagePopup homepagePopup);

    /**
     * 新增首页弹窗
     *
     * @param homepagePopup 首页弹窗
     * @return 结果
     */
    public int insertHomepagePopup(HomepagePopup homepagePopup);

    /**
     * 修改首页弹窗
     *
     * @param homepagePopup 首页弹窗
     * @return 结果
     */
    public int updateHomepagePopup(HomepagePopup homepagePopup);

    /**
     * 删除首页弹窗
     *
     * @param id 首页弹窗ID
     * @return 结果
     */
    public int deleteHomepagePopupById(Long id);

    /**
     * 批量删除首页弹窗
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteHomepagePopupByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
