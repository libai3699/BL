package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.Banner;

import java.util.List;

/**
 * 轮播图Mapper接口
 *
 * @author axing
 * @date 2024-05-02
 */
public interface BannerMapper extends BaseMapper<Banner>
{
    /**
     * 查询轮播图
     *
     * @param id 轮播图ID
     * @return 轮播图
     */
    public Banner selectBannerById(Long id);

    /**
     * 查询轮播图列表
     *
     * @param banner 轮播图
     * @return 轮播图集合
     */
    public List<Banner> selectBannerList(Banner banner);

    /**
     * 新增轮播图
     *
     * @param banner 轮播图
     * @return 结果
     */
    public int insertBanner(Banner banner);

    /**
     * 修改轮播图
     *
     * @param banner 轮播图
     * @return 结果
     */
    public int updateBanner(Banner banner);

    /**
     * 删除轮播图
     *
     * @param id 轮播图ID
     * @return 结果
     */
    public int deleteBannerById(Long id);

    /**
     * 批量删除轮播图
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBannerByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
