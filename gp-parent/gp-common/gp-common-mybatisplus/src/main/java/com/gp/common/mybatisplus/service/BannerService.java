package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.constant.CommonConstant;
import com.common.core.util.StringUtils;
import com.gp.common.base.constant.DelConstants;
import com.gp.common.base.utils.AdminUtil;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.Banner;
import com.gp.common.mybatisplus.mapper.BannerMapper;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 轮播图Service业务层处理
 *
 * @author axing
 * @date 2024-05-02
 */
@Transactional(rollbackFor = Exception.class)
@Service
@Slf4j
public class BannerService extends ServiceImpl<BannerMapper, Banner> {
    @Autowired
    private BannerMapper bannerMapper;
    @Resource
    private MNacosParam mNacosParam;

    /**
     * 查询轮播图
     *
     * @param id 轮播图ID
     * @return 轮播图
     */

    public Banner selectBannerById(Long id) {
        return bannerMapper.selectBannerById(id);
    }

    /**
     * 查询轮播图列表
     *
     * @param banner 轮播图
     * @return 轮播图
     */

    public List<Banner> selectBannerList(Banner banner) {
        return bannerMapper.selectBannerList(banner);
    }

    /**
     * 新增轮播图
     *
     * @param banner 轮播图
     * @return 结果
     */

    public Boolean insertBanner(Banner banner) {
        banner.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(banner);
        return result;
    }

    /**
     * 修改轮播图
     *
     * @param banner 轮播图
     * @return 结果
     */

    public Boolean updateBanner(Banner banner) {
        boolean result = this.updateById(banner);
        return result;
    }

    /**
     * 批量删除轮播图
     *
     * @param ids 需要删除的轮播图ID
     * @return 结果
     */

    public Boolean deleteBannerByIds(Long[] ids) {
        List<Banner> list = new ArrayList<>();
        for (Long id : ids) {
            Banner banner = new Banner();
            banner.setHasDel(DelConstants.del);
            banner.setId(id);
            list.add(banner);
        }
        boolean result = this.updateBatchById(list);
        return result;
    }

    /**
     * 删除轮播图信息
     *
     * @param id 轮播图ID
     * @return 结果
     */

    public Boolean deleteBannerById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    @Cacheable(value = "queryBannerByType", key = "#dbCode+'-'+#lanKey + '-' + #type")
    public List<Banner> queryBannerByType(String dbCode, String lanKey, Integer type) {
        LambdaQueryWrapper<Banner> q = new LambdaQueryWrapper<>();
        q.eq(Banner::getHasDel, DelConstants.noDel);
        q.eq(Banner::getStatus, CommonConstant.OPEN);
        q.eq(Banner::getType, type);
        String language = LocaleContextHolder.getLocale().toString();
        ;
        q.eq(Banner::getLanKey, language);
        q.orderByAsc(Banner::getSort);
        List<Banner> banners = this.bannerMapper.selectList(q);
        if (banners == null || banners.isEmpty()) {
            q.clear();
            q.eq(Banner::getHasDel, DelConstants.noDel);
            q.eq(Banner::getStatus, CommonConstant.OPEN);
            q.eq(Banner::getType, type);
            q.eq(Banner::getLanKey, Locale.US.toString());
            q.orderByAsc(Banner::getSort);
            banners = this.bannerMapper.selectList(q);
        }
        banners.stream().forEach(e -> {
            e.setBannerUrl(StringUtils.isEmpty(e.getBannerUrl()) ? "" : AdminUtil.perfectAdmin(mNacosParam.getFileAdmin(), e.getBannerUrl()));
        });
        return banners;
    }

}
