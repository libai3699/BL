package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.UserChannel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserChannelMapper extends BaseMapper<UserChannel> {


    /**
     * 查询渠道配置
     *
     * @param param 渠道配置ID
     * @return 渠道配置
     */
    public UserChannel selectTUserChannelById(UserChannel param);

    /**
     * 插入渠道配置（如果不存在）
     * 使用 INSERT IGNORE 语法，如果user_id和channel_id组合已存在则忽略
     *
     * @param userChannel 渠道配置
     * @return 影响的行数
     */
    @Insert("INSERT IGNORE INTO t_user_channel (user_id, channel_id, pid, p_path, " +
            "super_user_rebate_1, super_user_rebate_2, super_user_rebate_3, super_user_rebate_4, " +
            "super_user_rebate_5, super_user_rebate_6, super_user_rebate_7, super_user_rebate_8, " +
            "super_user_rebate_9, dividend_status, dividend_rebate, create_by, create_time, update_by, update_time) " +
            "VALUES (#{userId}, #{channelId}, #{pid}, #{pPath}, " +
            "#{superUserRebate1}, #{superUserRebate2}, #{superUserRebate3}, #{superUserRebate4}, " +
            "#{superUserRebate5}, #{superUserRebate6}, #{superUserRebate7}, #{superUserRebate8}, " +
            "#{superUserRebate9}, #{dividendStatus}, #{dividendRebate}, #{createBy}, #{createTime}, #{updateBy}, #{updateTime})")
    int insertIgnore(UserChannel userChannel);

}