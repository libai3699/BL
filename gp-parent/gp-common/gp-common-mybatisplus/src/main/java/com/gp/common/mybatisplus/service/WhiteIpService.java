package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.WhiteIp;
import com.gp.common.mybatisplus.mapper.WhiteIpMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ip白名单Service业务层处理
 *
 * @author axing
 * @date 2023-12-27
 */
@Service
public class WhiteIpService extends ServiceImpl<WhiteIpMapper, WhiteIp>
{
    @Autowired
    private  WhiteIpMapper whiteIpMapper;

    /**
     * 查询ip白名单
     *
     * @param id ip白名单ID
     * @return ip白名单
     */

    public  WhiteIp selectWhiteIpById(Long id)
    {
        return whiteIpMapper.selectWhiteIpById(id);
    }

    /**
     * 查询ip白名单列表
     *
     * @param tWhiteIp ip白名单
     * @return ip白名单
     */

    public List<WhiteIp> selectWhiteIpList(WhiteIp tWhiteIp)
    {
        return whiteIpMapper.selectWhiteIpList(tWhiteIp);
    }

    /**
     * 新增ip白名单
     *
     * @param tWhiteIp ip白名单
     * @return 结果
     */

    public Boolean insertWhiteIp(WhiteIp tWhiteIp)
    {
        tWhiteIp.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(tWhiteIp);
        return result;
    }

    /**
     * 修改ip白名单
     *
     * @param tWhiteIp ip白名单
     * @return 结果
     */

    public Boolean updateWhiteIp(WhiteIp tWhiteIp)
    {
        tWhiteIp.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(tWhiteIp);
        return result;
    }

    /**
     * 批量删除ip白名单
     *
     * @param ids 需要删除的ip白名单ID
     * @return 结果
     */

    public Boolean deleteWhiteIpByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除ip白名单信息
     *
     * @param id ip白名单ID
     * @return 结果
     */

    public Boolean deleteWhiteIpById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }


    public Boolean checkIp(String ip) {
        LambdaQueryWrapper<WhiteIp> queryWrapper = Wrappers.lambdaQuery(WhiteIp.class);
        List<WhiteIp> tWhiteIps = this.baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(tWhiteIps) || tWhiteIps.size()>0){
            List<String> ipList = tWhiteIps.stream().map(WhiteIp::getIp).collect(Collectors.toList());
            if (ipList.contains(ip)){
                return true;
            }else {
                return false;
            }
        }else {
            return true;
        }

    }
}
