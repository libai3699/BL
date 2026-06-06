package com.gp.common.mybatisplus.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.util.RedisUtil;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.exception.BusinessException;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.Channel;
import com.gp.common.mybatisplus.entity.Shareholder;
import com.gp.common.mybatisplus.mapper.ShareholderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 股东Service业务层处理
 *
 * @author axing
 * @date 2025-05-20
 */
@Service
public class ShareholderService extends ServiceImpl<ShareholderMapper, Shareholder>
{
    @Autowired
    private ShareholderMapper shareholderMapper;
    @Resource
    private ChannelService channelService;
    @Resource
    private RedisUtil redisUtil;
    /**
     * 查询股东
     *
     * @param id 股东ID
     * @return 股东
     */

    public Shareholder selectShareholderById(Long id)
    {
        return shareholderMapper.selectShareholderById(id);
    }

    public String getShareholderName(Long shareholderId) {
        return shareholderMapper.getShareholderName(shareholderId);
    }
    /**
     * 查询股东列表
     *
     * @param param 股东
     * @return 股东
     */

    public List<Shareholder> selectShareholderList(Shareholder param)
    {
        return shareholderMapper.selectShareholderList(param);
    }
    
    /**
     * 查询股东列表数量
     *
     * @param param 股东
     * @return 股东数量
     */
    public int selectShareholderListCount(Shareholder param)
    {
        return shareholderMapper.selectShareholderListCount(param);
    }
    /**
     * 新增股东
     *
     * @param param 股东
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertShareholder(Shareholder param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改股东
     *
     * @param param 股东
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateShareholder(Shareholder param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除股东
     *
     * @param ids 需要删除的股东ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteShareholderByIds(Long[] ids)
    {
        for (Long id : ids) {
            //查询股东是否在渠道
            int count = channelService.count(new LambdaQueryWrapper<Channel>().eq(Channel::getShareholderId, id));
            if (count > 0) {
                throw new BusinessException("该股东在渠道中被使用，请先解除关联关系");
            }
        }
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除股东信息
     *
     * @param id 股东ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteShareholderById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }

    public List<Shareholder> queryShareholderByUserId(Long userId) {
        LambdaQueryWrapper<Shareholder> q = new LambdaQueryWrapper<>();
        q.eq(Shareholder::getUserId, userId);
        q.last("limit 1");
        return shareholderMapper.selectList(q);
    }
    public Shareholder queryShareholderByUserIdOne(Long userId) {
        LambdaQueryWrapper<Shareholder> q = new LambdaQueryWrapper<>();
        q.eq(Shareholder::getUserId, userId);
        q.last("limit 1");
        return shareholderMapper.selectOne(q);
    }

    public List<Shareholder> queryShareholderByUserIdCache(String dbCode, Long userId) {
        String redisKey = StrUtil.format(RedisKey.shareholder, dbCode, userId);
        // 1. 先查 Redis
        Object o = redisUtil.get(redisKey);
        if (o != null) {
            return (List<Shareholder>) o;
        }
        // 2. Redis 没有，查数据库
        LambdaQueryWrapper<Shareholder> q = new LambdaQueryWrapper<>();
        q.eq(Shareholder::getUserId, userId);
        q.last("limit 1");
        List<Shareholder> result = shareholderMapper.selectList(q);
        // 3. 写入 Redis，设置过期时间（如1小时）
        if (result != null && !result.isEmpty()) {
            redisUtil.set(redisKey, result, 3600);
        }
        return result;
    }
}
