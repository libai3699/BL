package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.WhiteIp;

import java.util.List;

/**
 * ip白名单Mapper接口
 *
 * @author axing
 * @date 2023-12-27
 */
public interface WhiteIpMapper extends BaseMapper<WhiteIp>
{
    /**
     * 查询ip白名单
     *
     * @param id ip白名单ID
     * @return ip白名单
     */
    public WhiteIp selectWhiteIpById(Long id);

    /**
     * 查询ip白名单列表
     *
     * @param tWhiteIp ip白名单
     * @return ip白名单集合
     */
    public List<WhiteIp> selectWhiteIpList(WhiteIp tWhiteIp);

    /**
     * 新增ip白名单
     *
     * @param tWhiteIp ip白名单
     * @return 结果
     */
    public int insertWhiteIp(WhiteIp tWhiteIp);

    /**
     * 修改ip白名单
     *
     * @param tWhiteIp ip白名单
     * @return 结果
     */
    public int updateWhiteIp(WhiteIp tWhiteIp);

    /**
     * 删除ip白名单
     *
     * @param id ip白名单ID
     * @return 结果
     */
    public int deleteWhiteIpById(Long id);

    /**
     * 批量删除ip白名单
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteWhiteIpByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
