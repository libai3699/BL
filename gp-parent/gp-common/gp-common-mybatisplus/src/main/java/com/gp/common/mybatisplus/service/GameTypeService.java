package com.gp.common.mybatisplus.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.constant.CommonConstant;
import com.common.core.util.StringUtils;
import com.gp.common.base.exception.BusinessException;
import com.gp.common.base.utils.BeanUtils;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.base.utils.Decimal;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.mybatisplus.entity.Channel;
import com.gp.common.mybatisplus.entity.GameType;
import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.entity.UserChannel;
import com.gp.common.mybatisplus.mapper.GameTypeMapper;
import com.gp.common.mybatisplus.param.RebateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 游戏类型Service业务层处理
 *
 * @author axing
 * @date 2024-05-07
 */
@Transactional(rollbackFor = Exception.class)
@Service
@Slf4j
public class GameTypeService extends ServiceImpl<GameTypeMapper, GameType> {
    @Autowired
    private GameTypeMapper gameTypeMapper;
    @Autowired
    private ConfigRiskService configRiskService;
    @Autowired
    private ChannelConfigService channelConfigService;
    @Autowired
    private GameTypeService gameTypeService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private UserChannelService userChannelService;


    /**
     * 查询游戏类型
     *
     * @param id 游戏类型ID
     * @return 游戏类型
     */

    public GameType selectGameTypeById(Long id) {
        return gameTypeMapper.selectGameTypeById(id);
    }

    /**
     * 查询游戏类型列表
     *
     * @param gameType 游戏类型
     * @return 游戏类型
     */

    public List<GameType> selectGameTypeList(GameType gameType) {
        return gameTypeMapper.selectGameTypeList(gameType);
    }

    /**
     * 查询游戏类型数量
     *
     * @param gameType 游戏类型
     * @return 游戏类型数量
     */
    public Long selectGameTypeCount(GameType gameType) {
        return gameTypeMapper.selectGameTypeCount(gameType);
    }

    /**
     * 新增游戏类型
     *
     * @param gameType 游戏类型
     * @return 结果
     */

    public Boolean insertGameType(GameType gameType) {
        boolean result = this.save(gameType);
        return result;
    }

    /**
     * 修改游戏类型
     *
     * @param gameType 游戏类型
     * @return 结果
     */

    public Boolean updateGameType(GameType gameType) {
        GameType pojo = this.selectGameTypeById(gameType.getId());
        if (Objects.isNull(pojo)) {
            throw new BusinessException(
                    StringUtils.format(MessagesUtils.get("common.param.is.error")
                            , "id", gameType.getId())
            );
        }
        gameType.setUpdateTime(DateUtils.getNowDate());
        BeanUtils.copyPropertiesIgnoreNull(gameType, pojo);
        boolean result = this.updateById(pojo);
        return result;
    }

    /**
     * 批量删除游戏类型
     *
     * @param ids 需要删除的游戏类型ID
     * @return 结果
     */

    public Boolean deleteGameTypeByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除游戏类型信息
     *
     * @param id 游戏类型ID
     * @return 结果
     */

    public Boolean deleteGameTypeById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }


    public List<GameType> queryTopTag() {
        String filterGameType = configRiskService.filterGameType();
        LambdaQueryWrapper<GameType> q = new LambdaQueryWrapper<>();
        q.orderByAsc(GameType::getSort);
        q.eq(GameType::getStatus, CommonConstant.OPEN);
        //看看过滤掉 游戏类型
        if(filterGameType!=null){
            String[] split = filterGameType.split(",");
            List<String> arr = Arrays.asList(split);
            q.notIn(GameType::getTypeCode, arr);
        }

        List<GameType> gameTypes = this.baseMapper.selectList(q);

        return gameTypes;
    }

    public GameType getGameTypeRebate(String typeCode) {
        LambdaQueryWrapper<GameType> q = new LambdaQueryWrapper<>();
        q.orderByAsc(GameType::getSort);
        q.in(GameType::getTypeCode, 0,typeCode);
        q.orderByDesc(GameType::getTypeCode);
        q.last("limit 1");
        return this.baseMapper.selectOne(q);
    }

    /**
     * 都是算好的比例
     * @param user
     * @param typeCode
     * @return
     */
    public RebateDto getRebate(TUser user, String typeCode,Integer code, Boolean isCommonModel,Boolean isAgentModel,Boolean isLotteryModel) {
        RebateDto rebateDto = new RebateDto();
        GameType gameType = null;
        Integer level = user.getLevel();
        if(code!=null){
            level = code;
        }
        Long channelId = user.getChannelId();
        //彩票代理模式分开
        if(isLotteryModel){
            rebateDto.setUserRebate(BigDecimal.ZERO);
            rebateDto.setSuperUserRebate(BigDecimal.ZERO);
            rebateDto.setTotalAllocationRebate(BigDecimal.ZERO);

            gameType = channelConfigService.getChannelConfig(channelId, typeCode);
            log.info("gameType:{}", JSONObject.toJSONString(gameType));
            if (level.equals(1)) {
                rebateDto.setUserRebate(gameType.getUserRebateV1()==null?BigDecimal.ZERO :gameType.getUserRebateV1());
            } else if (level.equals(2)) {
                rebateDto.setUserRebate(gameType.getUserRebateV2()==null?BigDecimal.ZERO :gameType.getUserRebateV2());
            } else if (level.equals(3)) {
                rebateDto.setUserRebate(gameType.getUserRebateV3()==null?BigDecimal.ZERO :gameType.getUserRebateV3());
            } else if (level.equals(4)) {
                rebateDto.setUserRebate(gameType.getUserRebateV4()==null?BigDecimal.ZERO :gameType.getUserRebateV4());
            } else if (level.equals(5)) {
                rebateDto.setUserRebate(gameType.getUserRebateV5()==null?BigDecimal.ZERO :gameType.getUserRebateV5());
            }else if (level.equals(6)) {
                rebateDto.setUserRebate(gameType.getUserRebateV6()==null?BigDecimal.ZERO :gameType.getUserRebateV6());
            }else if (level.equals(7)) {
                rebateDto.setUserRebate(gameType.getUserRebateV7()==null?BigDecimal.ZERO :gameType.getUserRebateV7());
            }else if (level.equals(8)) {
                rebateDto.setUserRebate(gameType.getUserRebateV8()==null?BigDecimal.ZERO :gameType.getUserRebateV8());
            }else if (level.equals(9)) {
                rebateDto.setUserRebate(gameType.getUserRebateV9()==null?BigDecimal.ZERO :gameType.getUserRebateV9());
            }else if (level.equals(10)) {
                rebateDto.setUserRebate(gameType.getUserRebateV10()==null?BigDecimal.ZERO :gameType.getUserRebateV10());
            }

            //找下渠道
            if(channelId!=null){
                Channel channel = channelService.getById(channelId);
                if(channel!=null){
                    Long userId = channel.getUserId();
                    //查询源头的
                    UserChannel channelLotteryConfig = userChannelService.getChannelLotteryConfig(userId, channelId);
                    BigDecimal totalReta = BigDecimal.ZERO;
                    if(channelLotteryConfig!=null){
                        totalReta = userChannelService.dealUserChannelRatio(channelLotteryConfig, typeCode);
                    }
                    if(totalReta.compareTo(BigDecimal.ZERO)<0){
                        totalReta = BigDecimal.ZERO;
                    }
                    //自己
                    UserChannel selfConfig = userChannelService.getChannelLotteryConfig(user.getUserId(), channelId);
                    BigDecimal selfReta = BigDecimal.ZERO;
                    if(selfConfig!=null){
                        selfReta=userChannelService.dealUserChannelRatio(selfConfig, typeCode);
                    }
                    if(selfReta.compareTo(BigDecimal.ZERO)<0){
                        selfReta = BigDecimal.ZERO;
                    }
                    //自己的外水
                    rebateDto.setSuperUserRebate(selfReta);
                    //剩余的总外水
                    if (Decimal.of(totalReta).ge(selfReta)) {
                        rebateDto.setTotalAllocationRebate(totalReta.subtract(selfReta));
                    }
                }
            }

            return rebateDto;
        }


        //代理模式
        if (isCommonModel) {
            log.info("<1>");
            gameType = channelConfigService.getChannelConfig(channelId, typeCode);
            log.info("gameType:{}", JSONObject.toJSONString(gameType));
            if (level.equals(1)) {
                rebateDto.setUserRebate(gameType.getUserRebateV1()==null?BigDecimal.ZERO :gameType.getUserRebateV1());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV1()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV1());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV1()==null?BigDecimal.ZERO :gameType.getTotalAllocationV1());
            } else if (level.equals(2)) {
                rebateDto.setUserRebate(gameType.getUserRebateV2()==null?BigDecimal.ZERO :gameType.getUserRebateV2());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV2()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV2());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV2()==null?BigDecimal.ZERO :gameType.getTotalAllocationV2());
            } else if (level.equals(3)) {
                rebateDto.setUserRebate(gameType.getUserRebateV3()==null?BigDecimal.ZERO :gameType.getUserRebateV3());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV3()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV3());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV3()==null?BigDecimal.ZERO :gameType.getTotalAllocationV3());
            } else if (level.equals(4)) {
                rebateDto.setUserRebate(gameType.getUserRebateV4()==null?BigDecimal.ZERO :gameType.getUserRebateV4());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV4()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV4());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV4()==null?BigDecimal.ZERO :gameType.getTotalAllocationV4());
            } else if (level.equals(5)) {
                rebateDto.setUserRebate(gameType.getUserRebateV5()==null?BigDecimal.ZERO :gameType.getUserRebateV5());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV5()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV5());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV5()==null?BigDecimal.ZERO :gameType.getTotalAllocationV5());
            }else if (level.equals(6)) {
                rebateDto.setUserRebate(gameType.getUserRebateV6()==null?BigDecimal.ZERO :gameType.getUserRebateV6());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV6()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV6());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV6()==null?BigDecimal.ZERO :gameType.getTotalAllocationV6());
            }else if (level.equals(7)) {
                rebateDto.setUserRebate(gameType.getUserRebateV7()==null?BigDecimal.ZERO :gameType.getUserRebateV7());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV7()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV7());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV7()==null?BigDecimal.ZERO :gameType.getTotalAllocationV7());
            }else if (level.equals(8)) {
                rebateDto.setUserRebate(gameType.getUserRebateV8()==null?BigDecimal.ZERO :gameType.getUserRebateV8());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV8()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV8());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV8()==null?BigDecimal.ZERO :gameType.getTotalAllocationV8());
            }else if (level.equals(9)) {
                rebateDto.setUserRebate(gameType.getUserRebateV9()==null?BigDecimal.ZERO :gameType.getUserRebateV9());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV9()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV9());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV9()==null?BigDecimal.ZERO :gameType.getTotalAllocationV9());
            }else if (level.equals(10)) {
                rebateDto.setUserRebate(gameType.getUserRebateV10()==null?BigDecimal.ZERO :gameType.getUserRebateV10());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV10()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV10());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV10()==null?BigDecimal.ZERO :gameType.getTotalAllocationV10());
            }
            return rebateDto;
        }
        //极差模式
        if(isAgentModel){
            gameType = gameTypeService.getGameTypeRebate(typeCode);
            dealRenate(gameType);
            log.info("gameType:{}", JSONObject.toJSONString(gameType));
            if (level.equals(1)) {
                rebateDto.setUserRebate(gameType.getUserRebateV1()==null?BigDecimal.ZERO :gameType.getUserRebateV1());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV1()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV1());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV1()==null?BigDecimal.ZERO :gameType.getTotalAllocationV1());
            } else if (level.equals(2)) {
                rebateDto.setUserRebate(gameType.getUserRebateV2()==null?BigDecimal.ZERO :gameType.getUserRebateV2());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV2()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV2());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV2()==null?BigDecimal.ZERO :gameType.getTotalAllocationV2());
            } else if (level.equals(3)) {
                rebateDto.setUserRebate(gameType.getUserRebateV3()==null?BigDecimal.ZERO :gameType.getUserRebateV3());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV3()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV3());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV3()==null?BigDecimal.ZERO :gameType.getTotalAllocationV3());
            } else if (level.equals(4)) {
                rebateDto.setUserRebate(gameType.getUserRebateV4()==null?BigDecimal.ZERO :gameType.getUserRebateV4());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV4()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV4());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV4()==null?BigDecimal.ZERO :gameType.getTotalAllocationV4());
            } else if (level.equals(5)) {
                rebateDto.setUserRebate(gameType.getUserRebateV5()==null?BigDecimal.ZERO :gameType.getUserRebateV5());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV5()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV5());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV5()==null?BigDecimal.ZERO :gameType.getTotalAllocationV5());
            }else if (level.equals(6)) {
                rebateDto.setUserRebate(gameType.getUserRebateV6()==null?BigDecimal.ZERO :gameType.getUserRebateV6());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV6()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV6());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV6()==null?BigDecimal.ZERO :gameType.getTotalAllocationV6());
            }else if (level.equals(7)) {
                rebateDto.setUserRebate(gameType.getUserRebateV7()==null?BigDecimal.ZERO :gameType.getUserRebateV7());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV7()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV7());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV7()==null?BigDecimal.ZERO :gameType.getTotalAllocationV7());
            }else if (level.equals(8)) {
                rebateDto.setUserRebate(gameType.getUserRebateV8()==null?BigDecimal.ZERO :gameType.getUserRebateV8());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV8()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV8());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV8()==null?BigDecimal.ZERO :gameType.getTotalAllocationV8());
            }else if (level.equals(9)) {
                rebateDto.setUserRebate(gameType.getUserRebateV9()==null?BigDecimal.ZERO :gameType.getUserRebateV9());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV9()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV9());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV9()==null?BigDecimal.ZERO :gameType.getTotalAllocationV9());
            }else if (level.equals(10)) {
                rebateDto.setUserRebate(gameType.getUserRebateV10()==null?BigDecimal.ZERO :gameType.getUserRebateV10());
                rebateDto.setSuperUserRebate(gameType.getSuperUserRebateV10()==null?BigDecimal.ZERO :gameType.getSuperUserRebateV10());
                rebateDto.setTotalAllocationRebate(gameType.getTotalAllocationV10()==null?BigDecimal.ZERO :gameType.getTotalAllocationV10());
            }
            return rebateDto;
        }
        return null;
    }

    private void dealRenate(GameType gameType) {
        gameType.setUserRebateV1(gameType.getTotalAllocationV1()==null?BigDecimal.ZERO:gameType.getTotalAllocationV1().multiply(gameType.getUserRebateV1()));
        gameType.setSuperUserRebateV1(gameType.getTotalAllocationV1()==null?BigDecimal.ZERO:gameType.getTotalAllocationV1().multiply(gameType.getSuperUserRebateV1()));
        gameType.setTotalAllocationV1(gameType.getTotalAllocationV1());

        gameType.setUserRebateV2(gameType.getTotalAllocationV2()==null?BigDecimal.ZERO:gameType.getTotalAllocationV2().multiply(gameType.getUserRebateV2()));
        gameType.setSuperUserRebateV2(gameType.getTotalAllocationV2()==null?BigDecimal.ZERO:gameType.getTotalAllocationV2().multiply(gameType.getSuperUserRebateV2()));
        gameType.setTotalAllocationV2(gameType.getTotalAllocationV2());

        gameType.setUserRebateV3(gameType.getTotalAllocationV3()==null?BigDecimal.ZERO:gameType.getTotalAllocationV3().multiply(gameType.getUserRebateV3()));
        gameType.setSuperUserRebateV3(gameType.getTotalAllocationV3()==null?BigDecimal.ZERO:gameType.getTotalAllocationV3().multiply(gameType.getSuperUserRebateV3()));
        gameType.setTotalAllocationV3(gameType.getTotalAllocationV3());

        gameType.setUserRebateV4(gameType.getTotalAllocationV4()==null?BigDecimal.ZERO:gameType.getTotalAllocationV4().multiply(gameType.getUserRebateV4()));
        gameType.setSuperUserRebateV4(gameType.getTotalAllocationV4()==null?BigDecimal.ZERO:gameType.getTotalAllocationV4().multiply(gameType.getSuperUserRebateV4()));
        gameType.setTotalAllocationV4(gameType.getTotalAllocationV4());


        gameType.setUserRebateV5(gameType.getTotalAllocationV5()==null?BigDecimal.ZERO:gameType.getTotalAllocationV5().multiply(gameType.getUserRebateV5()));
        gameType.setSuperUserRebateV5(gameType.getTotalAllocationV5()==null?BigDecimal.ZERO:gameType.getTotalAllocationV5().multiply(gameType.getSuperUserRebateV5()));
        gameType.setTotalAllocationV5(gameType.getTotalAllocationV5());

        gameType.setUserRebateV6(gameType.getTotalAllocationV6()==null?BigDecimal.ZERO:gameType.getTotalAllocationV6().multiply(gameType.getUserRebateV6()));
        gameType.setSuperUserRebateV6(gameType.getTotalAllocationV6()==null?BigDecimal.ZERO:gameType.getTotalAllocationV6().multiply(gameType.getSuperUserRebateV6()));
        gameType.setTotalAllocationV6(gameType.getTotalAllocationV6());

        gameType.setUserRebateV7(gameType.getTotalAllocationV7()==null?BigDecimal.ZERO:gameType.getTotalAllocationV7().multiply(gameType.getUserRebateV7()));
        gameType.setSuperUserRebateV7(gameType.getTotalAllocationV7()==null?BigDecimal.ZERO:gameType.getTotalAllocationV7().multiply(gameType.getSuperUserRebateV7()));
        gameType.setTotalAllocationV7(gameType.getTotalAllocationV7());

        gameType.setUserRebateV8(gameType.getTotalAllocationV8()==null?BigDecimal.ZERO:gameType.getTotalAllocationV8().multiply(gameType.getUserRebateV8()));
        gameType.setSuperUserRebateV8(gameType.getTotalAllocationV8()==null?BigDecimal.ZERO:gameType.getTotalAllocationV8().multiply(gameType.getSuperUserRebateV8()));
        gameType.setTotalAllocationV8(gameType.getTotalAllocationV8());

        gameType.setUserRebateV9(gameType.getTotalAllocationV9()==null?BigDecimal.ZERO:gameType.getTotalAllocationV9().multiply(gameType.getUserRebateV9()));
        gameType.setSuperUserRebateV9(gameType.getTotalAllocationV9()==null?BigDecimal.ZERO:gameType.getTotalAllocationV9().multiply(gameType.getSuperUserRebateV9()));
        gameType.setTotalAllocationV9(gameType.getTotalAllocationV9());


        gameType.setUserRebateV10(gameType.getTotalAllocationV10()==null?BigDecimal.ZERO:gameType.getTotalAllocationV10().multiply(gameType.getUserRebateV10()));
        gameType.setSuperUserRebateV10(gameType.getTotalAllocationV10()==null?BigDecimal.ZERO:gameType.getTotalAllocationV10().multiply(gameType.getSuperUserRebateV10()));
        gameType.setTotalAllocationV10(gameType.getTotalAllocationV10());


    }


    public List<GameType> queryAllGameType() {
        LambdaQueryWrapper<GameType> q = new LambdaQueryWrapper<>();
        q.orderByAsc(GameType::getSort);
        q.eq(GameType::getStatus, CommonConstant.OPEN);
        List<GameType> gameTypes = this.list(q);
        return gameTypes;
    }

    public GameType getbyTypeCode(String typeCode) {
        LambdaQueryWrapper<GameType> q = new LambdaQueryWrapper<>();
        q.orderByAsc(GameType::getSort);
        q.eq(GameType::getStatus, CommonConstant.OPEN);
        q.eq(GameType::getTypeCode, typeCode);
        GameType one = this.getOne(q);
        return one;
    }

    public List<GameType> listByTypeCodesWithoutOpen(String gameTypeCode) {
        if (StrUtil.isEmpty(gameTypeCode)) return Collections.emptyList();
        List<String> codes = Arrays.stream(gameTypeCode.split(","))
                .map(String::trim).filter(StrUtil::isNotEmpty)
                .collect(java.util.stream.Collectors.toList());
        if (codes.isEmpty()) return Collections.emptyList();
        LambdaQueryWrapper<GameType> q = new LambdaQueryWrapper<>();
        q.in(GameType::getTypeCode, codes);
        q.orderByAsc(GameType::getSort);
        return this.list(q);
    }
}
