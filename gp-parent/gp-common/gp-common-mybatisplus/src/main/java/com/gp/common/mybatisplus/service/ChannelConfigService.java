package com.gp.common.mybatisplus.service;

import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.mybatisplus.entity.GameType;
import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.vo.ChannelLotteryConfigVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.ChannelConfigMapper;
import com.gp.common.mybatisplus.entity.ChannelConfig;

/**
 * 渠道配置Service业务层处理
 *
 * @author axing
 * @date 2025-05-23
 */
@Service
@Slf4j
public class ChannelConfigService extends ServiceImpl<ChannelConfigMapper, ChannelConfig>
{
    @Autowired
    private ChannelConfigMapper channelConfigMapper;
    @Autowired
    private GameTypeService gameTypeService;
    @Autowired
    private ConfigRiskService configRiskService;

    /**
     * 查询渠道配置
     *
     * @param id 渠道配置ID
     * @return 渠道配置
     */

    public ChannelConfig selectChannelConfigById(Long id)
    {
        return channelConfigMapper.selectChannelConfigById(id);
    }

    /**
     * 查询渠道配置列表
     *
     * @param param 渠道配置
     * @return 渠道配置
     */

    public List<ChannelConfig> selectChannelConfigList(ChannelConfig param)
    {
        return channelConfigMapper.selectChannelConfigList(param);
    }
    
    /**
     * 查询渠道配置列表数量
     *
     * @param param 渠道配置
     * @return 渠道配置数量
     */
    public int selectChannelConfigListCount(ChannelConfig param)
    {
        return channelConfigMapper.selectChannelConfigListCount(param);
    }

    /**
     * 新增渠道配置
     *
     * @param param 渠道配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertChannelConfig(ChannelConfig param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改渠道配置
     *
     * @param param 渠道配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateChannelConfig(ChannelConfig param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除渠道配置
     *
     * @param ids 需要删除的渠道配置ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteChannelConfigByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除渠道配置信息
     *
     * @param id 渠道配置ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteChannelConfigById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }

    public GameType getChannelConfig(Long channelId, String typeCode) {
        //先查询通用的
        if(channelId == null) {
            channelId = 0L;
        }
        //首先要找游戏类型 找到总比例
        GameType gameType = gameTypeService.getGameTypeRebate(typeCode);
        //都去找这个总比例
        //找到总配置
        String userRebate = configRiskService.userRebate();
        String superUserRebate = configRiskService.superUserRebate();
        if(channelId== 0L &&userRebate!=null&&superUserRebate!=null){
            BigDecimal userRebateBig = new BigDecimal(userRebate).multiply(gameType.getTotalAllocationV1());
            BigDecimal superUserRebateBig = new BigDecimal(superUserRebate).multiply(gameType.getTotalAllocationV1());
            gameType.setUserRebateV1(userRebateBig);
            gameType.setUserRebateV2(userRebateBig);
            gameType.setUserRebateV3(userRebateBig);
            gameType.setUserRebateV4(userRebateBig);
            gameType.setUserRebateV5(userRebateBig);
            gameType.setUserRebateV6(userRebateBig);
            gameType.setUserRebateV7(userRebateBig);
            gameType.setUserRebateV8(userRebateBig);
            gameType.setUserRebateV9(userRebateBig);
            gameType.setUserRebateV10(userRebateBig);
            gameType.setSuperUserRebateV1(superUserRebateBig);
            gameType.setSuperUserRebateV2(superUserRebateBig);
            gameType.setSuperUserRebateV3(superUserRebateBig);
            gameType.setSuperUserRebateV4(superUserRebateBig);
            gameType.setSuperUserRebateV5(superUserRebateBig);
            gameType.setSuperUserRebateV6(superUserRebateBig);
            gameType.setSuperUserRebateV7(superUserRebateBig);
            gameType.setSuperUserRebateV8(superUserRebateBig);
            gameType.setSuperUserRebateV9(superUserRebateBig);
            gameType.setSuperUserRebateV10(superUserRebateBig);
            gameType.setTotalAllocationV1(gameType.getTotalAllocationV1());
            gameType.setTotalAllocationV2(gameType.getTotalAllocationV2());
            gameType.setTotalAllocationV3(gameType.getTotalAllocationV3());
            gameType.setTotalAllocationV4(gameType.getTotalAllocationV4());
            gameType.setTotalAllocationV5(gameType.getTotalAllocationV5());
            gameType.setTotalAllocationV6(gameType.getTotalAllocationV6());
            gameType.setTotalAllocationV7(gameType.getTotalAllocationV7());
            gameType.setTotalAllocationV8(gameType.getTotalAllocationV8());
            gameType.setTotalAllocationV9(gameType.getTotalAllocationV9());
            gameType.setTotalAllocationV10(gameType.getTotalAllocationV10());
            return gameType;
        }
        if(channelId== 0L){
            //不走下面的了
            GameType gameTypeRebate = gameTypeService.getGameTypeRebate(typeCode);
            return dealGameType(gameTypeRebate);
        }

        LambdaQueryWrapper<ChannelConfig> q = new LambdaQueryWrapper<>();
        q.eq(ChannelConfig::getChannelId, channelId);
        q.in(ChannelConfig::getGameType, 0, typeCode);
        //如果配置了通用的,那就只取通用的其他类型作废,防止因为前台配置比例,但是对不上找过来的原因
        q.orderByAsc(ChannelConfig::getGameType);
        q.last("limit 1");
        ChannelConfig one = this.getOne(q);
        if(one==null){
            GameType gameTypeRebate = gameTypeService.getGameTypeRebate(typeCode);
            return dealGameType(gameTypeRebate);
        }
        dealGameType(gameType, one);
        return gameType;
    }
    public GameType getAgencyChannelConfig(Long channelId, String typeCode) {
        //先查询通用的
        if(channelId == null) {
            channelId = 0L;
        }
        GameType gameType = new GameType();
        LambdaQueryWrapper<ChannelConfig> q = new LambdaQueryWrapper<>();
        q.eq(ChannelConfig::getChannelId, channelId);
        q.in(ChannelConfig::getGameType, 0, typeCode);
        q.orderByDesc(ChannelConfig::getGameType);
        q.last("limit 1");
        ChannelConfig one = this.getOne(q);
        if(one==null){
            return null;
        }
        dealGameTypeOut(gameType, one);
        return gameType;
    }
    public ChannelConfig getChannelConfigVO(Long channelId, Integer typeCode) {
        //先查询通用的
        LambdaQueryWrapper<ChannelConfig> q = new LambdaQueryWrapper<>();
        q.eq(ChannelConfig::getChannelId, channelId);
        q.in(ChannelConfig::getGameType, 0, typeCode);
        q.orderByDesc(ChannelConfig::getGameType);
        q.last("limit 1");
        ChannelConfig one = this.getOne(q);
        return one;
    }


    public List<GameType> getChannelConfigAll(Long channelId,List<GameType> gameTypes) {
        if(channelId==null){
            channelId=0L;
        }
        LambdaQueryWrapper<ChannelConfig> q = new LambdaQueryWrapper<>();
        q.eq(ChannelConfig::getChannelId, channelId);
        q.orderByDesc(ChannelConfig::getGameType);
        List<ChannelConfig> channelConfigs = this.list(q);
        for (GameType gameType : gameTypes) {
            for (ChannelConfig channelConfig : channelConfigs) {
                if (gameType.getTypeCode().equals(channelConfig.getGameType()+"")||"0".equals(channelConfig.getGameType()+"")) {
                    dealGameTypeOut(gameType, channelConfig);
                    break;
                }
            }
            String userRebate = configRiskService.userRebate();
            String superUserRebate = configRiskService.superUserRebate();
            if(userRebate!=null&&superUserRebate!=null){
                BigDecimal userRebateBig = new BigDecimal(userRebate);
                BigDecimal superUserRebateBig = new BigDecimal(superUserRebate);
                gameType.setUserRebateV1(userRebateBig);
                gameType.setUserRebateV2(userRebateBig);
                gameType.setUserRebateV3(userRebateBig);
                gameType.setUserRebateV4(userRebateBig);
                gameType.setUserRebateV5(userRebateBig);
                gameType.setUserRebateV6(userRebateBig);
                gameType.setUserRebateV7(userRebateBig);
                gameType.setUserRebateV8(userRebateBig);
                gameType.setUserRebateV9(userRebateBig);
                gameType.setUserRebateV10(userRebateBig);
                gameType.setSuperUserRebateV1(superUserRebateBig);
                gameType.setSuperUserRebateV2(superUserRebateBig);
                gameType.setSuperUserRebateV3(superUserRebateBig);
                gameType.setSuperUserRebateV4(superUserRebateBig);
                gameType.setSuperUserRebateV5(superUserRebateBig);
                gameType.setSuperUserRebateV6(superUserRebateBig);
                gameType.setSuperUserRebateV7(superUserRebateBig);
                gameType.setSuperUserRebateV8(superUserRebateBig);
                gameType.setSuperUserRebateV9(superUserRebateBig);
                gameType.setSuperUserRebateV10(superUserRebateBig);
                gameType.setTotalAllocationV1(gameType.getTotalAllocationV1());
                gameType.setTotalAllocationV2(gameType.getTotalAllocationV2());
                gameType.setTotalAllocationV3(gameType.getTotalAllocationV3());
                gameType.setTotalAllocationV4(gameType.getTotalAllocationV4());
                gameType.setTotalAllocationV5(gameType.getTotalAllocationV5());
                gameType.setTotalAllocationV6(gameType.getTotalAllocationV6());
                gameType.setTotalAllocationV7(gameType.getTotalAllocationV7());
                gameType.setTotalAllocationV8(gameType.getTotalAllocationV8());
                gameType.setTotalAllocationV9(gameType.getTotalAllocationV9());
                gameType.setTotalAllocationV10(gameType.getTotalAllocationV10());

            }
        }
        //如果配置了反水比例
        return gameTypes;
    }
    private void dealGameTypeOut(GameType gameType, ChannelConfig channelConfig) {
        gameType.setUserRebateV1(channelConfig.getUserRebateV1() == null ? BigDecimal.ZERO :
            (channelConfig.getUserRebateV1().compareTo(new BigDecimal(-1)) == 0 ? gameType.getUserRebateV1() : channelConfig.getUserRebateV1()));
        gameType.setUserRebateV2(channelConfig.getUserRebateV2() == null ? BigDecimal.ZERO :
            (channelConfig.getUserRebateV2().compareTo(new BigDecimal(-1)) == 0 ? gameType.getUserRebateV2() : channelConfig.getUserRebateV2()));
        gameType.setUserRebateV3(channelConfig.getUserRebateV3() == null ? BigDecimal.ZERO :
            (channelConfig.getUserRebateV3().compareTo(new BigDecimal(-1)) == 0 ? gameType.getUserRebateV3() : channelConfig.getUserRebateV3()));
        gameType.setUserRebateV4(channelConfig.getUserRebateV4() == null ? BigDecimal.ZERO :
            (channelConfig.getUserRebateV4().compareTo(new BigDecimal(-1)) == 0 ? gameType.getUserRebateV4() : channelConfig.getUserRebateV4()));
        gameType.setUserRebateV5(channelConfig.getUserRebateV5() == null ? BigDecimal.ZERO :
            (channelConfig.getUserRebateV5().compareTo(new BigDecimal(-1)) == 0 ? gameType.getUserRebateV5() : channelConfig.getUserRebateV5()));

        gameType.setUserRebateV6(channelConfig.getUserRebateV6() == null ? BigDecimal.ZERO :
            (channelConfig.getUserRebateV6().compareTo(new BigDecimal(-1)) == 0 ? gameType.getUserRebateV6() : channelConfig.getUserRebateV6()));
        gameType.setUserRebateV7(channelConfig.getUserRebateV7() == null ? BigDecimal.ZERO :
            (channelConfig.getUserRebateV7().compareTo(new BigDecimal(-1)) == 0 ? gameType.getUserRebateV7() : channelConfig.getUserRebateV7()));
        gameType.setUserRebateV8(channelConfig.getUserRebateV8() == null ? BigDecimal.ZERO :
            (channelConfig.getUserRebateV8().compareTo(new BigDecimal(-1)) == 0 ? gameType.getUserRebateV8() : channelConfig.getUserRebateV8()));
        gameType.setUserRebateV9(channelConfig.getUserRebateV9() == null ? BigDecimal.ZERO :
            (channelConfig.getUserRebateV9().compareTo(new BigDecimal(-1)) == 0 ? gameType.getUserRebateV9() : channelConfig.getUserRebateV9()));
        gameType.setUserRebateV10(channelConfig.getUserRebateV10() == null ? BigDecimal.ZERO :
            (channelConfig.getUserRebateV10().compareTo(new BigDecimal(-1)) == 0 ? gameType.getUserRebateV10() : channelConfig.getUserRebateV10()));
        gameType.setSuperUserRebateV1(channelConfig.getSuperUserRebateV1() == null ? BigDecimal.ZERO :
            (channelConfig.getSuperUserRebateV1().compareTo(new BigDecimal(-1)) == 0 ? gameType.getSuperUserRebateV1() : channelConfig.getSuperUserRebateV1()));
        gameType.setSuperUserRebateV2(channelConfig.getSuperUserRebateV2() == null ? BigDecimal.ZERO :
            (channelConfig.getSuperUserRebateV2().compareTo(new BigDecimal(-1)) == 0 ? gameType.getSuperUserRebateV2() : channelConfig.getSuperUserRebateV2()));
        gameType.setSuperUserRebateV3(channelConfig.getSuperUserRebateV3() == null ? BigDecimal.ZERO :
            (channelConfig.getSuperUserRebateV3().compareTo(new BigDecimal(-1)) == 0 ? gameType.getSuperUserRebateV3() : channelConfig.getSuperUserRebateV3()));
        gameType.setSuperUserRebateV4(channelConfig.getSuperUserRebateV4() == null ? BigDecimal.ZERO :
            (channelConfig.getSuperUserRebateV4().compareTo(new BigDecimal(-1)) == 0 ? gameType.getSuperUserRebateV4() : channelConfig.getSuperUserRebateV4()));
        gameType.setSuperUserRebateV5(channelConfig.getSuperUserRebateV5() == null ? BigDecimal.ZERO :
            (channelConfig.getSuperUserRebateV5().compareTo(new BigDecimal(-1)) == 0 ? gameType.getSuperUserRebateV5() : channelConfig.getSuperUserRebateV5()));

        gameType.setSuperUserRebateV6(channelConfig.getSuperUserRebateV6() == null ? BigDecimal.ZERO :
            (channelConfig.getSuperUserRebateV6().compareTo(new BigDecimal(-1)) == 0 ? gameType.getSuperUserRebateV6() : channelConfig.getSuperUserRebateV6()));
        gameType.setSuperUserRebateV7(channelConfig.getSuperUserRebateV7() == null ? BigDecimal.ZERO :
            (channelConfig.getSuperUserRebateV7().compareTo(new BigDecimal(-1)) == 0 ? gameType.getSuperUserRebateV7() : channelConfig.getSuperUserRebateV7()));
        gameType.setSuperUserRebateV8(channelConfig.getSuperUserRebateV8() == null ? BigDecimal.ZERO :
            (channelConfig.getSuperUserRebateV8().compareTo(new BigDecimal(-1)) == 0 ? gameType.getSuperUserRebateV8() : channelConfig.getSuperUserRebateV8()));
        gameType.setSuperUserRebateV9(channelConfig.getSuperUserRebateV9() == null ? BigDecimal.ZERO :
            (channelConfig.getSuperUserRebateV9().compareTo(new BigDecimal(-1)) == 0 ? gameType.getSuperUserRebateV9() : channelConfig.getSuperUserRebateV9()));
        gameType.setSuperUserRebateV10(channelConfig.getSuperUserRebateV10() == null ? BigDecimal.ZERO :
            (channelConfig.getSuperUserRebateV10().compareTo(new BigDecimal(-1)) == 0 ? gameType.getSuperUserRebateV10() : channelConfig.getSuperUserRebateV10()));
        log.info("gameType.getTotalAllocationV1()={}",gameType.getTotalAllocationV1());
        BigDecimal totalAllocationV1 = calculateTotalAllocation(gameType.getTotalAllocationV1(), channelConfig.getTotalAllocationV1());
        log.info("gameType.getTotalAllocationV1()={}",totalAllocationV1);
        BigDecimal totalAllocationV2 = calculateTotalAllocation(gameType.getTotalAllocationV2(), channelConfig.getTotalAllocationV2());
        BigDecimal totalAllocationV3 = calculateTotalAllocation(gameType.getTotalAllocationV3(), channelConfig.getTotalAllocationV3());
        BigDecimal totalAllocationV4 = calculateTotalAllocation(gameType.getTotalAllocationV4(), channelConfig.getTotalAllocationV4());
        BigDecimal totalAllocationV5 = calculateTotalAllocation(gameType.getTotalAllocationV5(), channelConfig.getTotalAllocationV5());

        BigDecimal totalAllocationV6 = calculateTotalAllocation(gameType.getTotalAllocationV6(), channelConfig.getTotalAllocationV6());
        BigDecimal totalAllocationV7 = calculateTotalAllocation(gameType.getTotalAllocationV7(), channelConfig.getTotalAllocationV7());
        BigDecimal totalAllocationV8 = calculateTotalAllocation(gameType.getTotalAllocationV8(), channelConfig.getTotalAllocationV8());
        BigDecimal totalAllocationV9 = calculateTotalAllocation(gameType.getTotalAllocationV9(), channelConfig.getTotalAllocationV9());
        BigDecimal totalAllocationV10 = calculateTotalAllocation(gameType.getTotalAllocationV10(), channelConfig.getTotalAllocationV10());
        gameType.setTotalAllocationV1(totalAllocationV1);
        gameType.setTotalAllocationV2(totalAllocationV2);
        gameType.setTotalAllocationV3(totalAllocationV3);
        gameType.setTotalAllocationV4(totalAllocationV4);
        gameType.setTotalAllocationV5(totalAllocationV5);

        gameType.setTotalAllocationV6(totalAllocationV6);
        gameType.setTotalAllocationV7(totalAllocationV7);
        gameType.setTotalAllocationV8(totalAllocationV8);
        gameType.setTotalAllocationV9(totalAllocationV9);
        gameType.setTotalAllocationV10(totalAllocationV10);


    }

    private void dealGameType(GameType gameType, ChannelConfig channelConfig) {
        // 计算总分配比例
        BigDecimal totalAllocationV1 = calculateTotalAllocation(gameType.getTotalAllocationV1(), channelConfig.getTotalAllocationV1());
        BigDecimal totalAllocationV2 = calculateTotalAllocation(gameType.getTotalAllocationV2(), channelConfig.getTotalAllocationV2());
        BigDecimal totalAllocationV3 = calculateTotalAllocation(gameType.getTotalAllocationV3(), channelConfig.getTotalAllocationV3());
        BigDecimal totalAllocationV4 = calculateTotalAllocation(gameType.getTotalAllocationV4(), channelConfig.getTotalAllocationV4());
        BigDecimal totalAllocationV5 = calculateTotalAllocation(gameType.getTotalAllocationV5(), channelConfig.getTotalAllocationV5());


        // 计算总分配比例
        BigDecimal totalAllocationV6 = calculateTotalAllocation(gameType.getTotalAllocationV6(), channelConfig.getTotalAllocationV6());
        BigDecimal totalAllocationV7 = calculateTotalAllocation(gameType.getTotalAllocationV7(), channelConfig.getTotalAllocationV7());
        BigDecimal totalAllocationV8 = calculateTotalAllocation(gameType.getTotalAllocationV8(), channelConfig.getTotalAllocationV8());
        BigDecimal totalAllocationV9 = calculateTotalAllocation(gameType.getTotalAllocationV9(), channelConfig.getTotalAllocationV9());
        BigDecimal totalAllocationV10 = calculateTotalAllocation(gameType.getTotalAllocationV10(), channelConfig.getTotalAllocationV10());


        // 设置用户返点
        setRebate(gameType::setUserRebateV1, channelConfig.getUserRebateV1(), gameType.getUserRebateV1(), totalAllocationV1);
        setRebate(gameType::setUserRebateV2, channelConfig.getUserRebateV2(), gameType.getUserRebateV2(), totalAllocationV2);
        setRebate(gameType::setUserRebateV3, channelConfig.getUserRebateV3(), gameType.getUserRebateV3(), totalAllocationV3);
        setRebate(gameType::setUserRebateV4, channelConfig.getUserRebateV4(), gameType.getUserRebateV4(), totalAllocationV4);
        setRebate(gameType::setUserRebateV5, channelConfig.getUserRebateV5(), gameType.getUserRebateV5(), totalAllocationV5);

        // 设置用户返点
        setRebate(gameType::setUserRebateV6, channelConfig.getUserRebateV6(), gameType.getUserRebateV6(), totalAllocationV6);
        setRebate(gameType::setUserRebateV7, channelConfig.getUserRebateV7(), gameType.getUserRebateV7(), totalAllocationV7);
        setRebate(gameType::setUserRebateV8, channelConfig.getUserRebateV8(), gameType.getUserRebateV8(), totalAllocationV8);
        setRebate(gameType::setUserRebateV9, channelConfig.getUserRebateV9(), gameType.getUserRebateV9(), totalAllocationV9);
        setRebate(gameType::setUserRebateV10, channelConfig.getUserRebateV10(), gameType.getUserRebateV10(), totalAllocationV10);

        // 设置上级用户返点
        setRebate(gameType::setSuperUserRebateV1, channelConfig.getSuperUserRebateV1(), gameType.getSuperUserRebateV1(), totalAllocationV1);
        setRebate(gameType::setSuperUserRebateV2, channelConfig.getSuperUserRebateV2(), gameType.getSuperUserRebateV2(), totalAllocationV2);
        setRebate(gameType::setSuperUserRebateV3, channelConfig.getSuperUserRebateV3(), gameType.getSuperUserRebateV3(), totalAllocationV3);
        setRebate(gameType::setSuperUserRebateV4, channelConfig.getSuperUserRebateV4(), gameType.getSuperUserRebateV4(), totalAllocationV4);
        setRebate(gameType::setSuperUserRebateV5, channelConfig.getSuperUserRebateV5(), gameType.getSuperUserRebateV5(), totalAllocationV5);

        // 设置上级用户返点
        setRebate(gameType::setSuperUserRebateV6, channelConfig.getSuperUserRebateV6(), gameType.getSuperUserRebateV6(), totalAllocationV6);
        setRebate(gameType::setSuperUserRebateV7, channelConfig.getSuperUserRebateV7(), gameType.getSuperUserRebateV7(), totalAllocationV7);
        setRebate(gameType::setSuperUserRebateV8, channelConfig.getSuperUserRebateV8(), gameType.getSuperUserRebateV8(), totalAllocationV8);
        setRebate(gameType::setSuperUserRebateV9, channelConfig.getSuperUserRebateV9(), gameType.getSuperUserRebateV9(), totalAllocationV9);
        setRebate(gameType::setSuperUserRebateV10, channelConfig.getSuperUserRebateV10(), gameType.getSuperUserRebateV10(), totalAllocationV10);

        // 设置超级用户返点
        gameType.setTotalAllocationV1(totalAllocationV1);
        gameType.setTotalAllocationV2(totalAllocationV2);
        gameType.setTotalAllocationV3(totalAllocationV3);
        gameType.setTotalAllocationV4(totalAllocationV4);
        gameType.setTotalAllocationV5(totalAllocationV5);

        // 设置超级用户返点
        gameType.setTotalAllocationV6(totalAllocationV6);
        gameType.setTotalAllocationV7(totalAllocationV8);
        gameType.setTotalAllocationV8(totalAllocationV8);
        gameType.setTotalAllocationV9(totalAllocationV9);
        gameType.setTotalAllocationV10(totalAllocationV10);
    }


    private GameType dealGameType(GameType gameType) {
        // 计算总分配比例
        BigDecimal totalAllocationV1 = getTotalAllocation(gameType.getTotalAllocationV1());
        BigDecimal totalAllocationV2 = getTotalAllocation(gameType.getTotalAllocationV2());
        BigDecimal totalAllocationV3 = getTotalAllocation(gameType.getTotalAllocationV3());
        BigDecimal totalAllocationV4 = getTotalAllocation(gameType.getTotalAllocationV4());
        BigDecimal totalAllocationV5 = getTotalAllocation(gameType.getTotalAllocationV5());


        BigDecimal totalAllocationV6 = getTotalAllocation(gameType.getTotalAllocationV6());
        BigDecimal totalAllocationV7 = getTotalAllocation(gameType.getTotalAllocationV7());
        BigDecimal totalAllocationV8 = getTotalAllocation(gameType.getTotalAllocationV8());
        BigDecimal totalAllocationV9 = getTotalAllocation(gameType.getTotalAllocationV9());
        BigDecimal totalAllocationV10 = getTotalAllocation(gameType.getTotalAllocationV10());
        // 设置用户返点
        setRebate(gameType::setUserRebateV1, null, gameType.getUserRebateV1(), totalAllocationV1);
        setRebate(gameType::setUserRebateV2, null, gameType.getUserRebateV2(), totalAllocationV2);
        setRebate(gameType::setUserRebateV3, null, gameType.getUserRebateV3(), totalAllocationV3);
        setRebate(gameType::setUserRebateV4, null, gameType.getUserRebateV4(), totalAllocationV4);
        setRebate(gameType::setUserRebateV5, null, gameType.getUserRebateV5(), totalAllocationV5);


        // 设置用户返点
        setRebate(gameType::setUserRebateV6, null, gameType.getUserRebateV6(), totalAllocationV6);
        setRebate(gameType::setUserRebateV7, null, gameType.getUserRebateV7(), totalAllocationV7);
        setRebate(gameType::setUserRebateV8, null, gameType.getUserRebateV8(), totalAllocationV8);
        setRebate(gameType::setUserRebateV9, null, gameType.getUserRebateV9(), totalAllocationV9);
        setRebate(gameType::setUserRebateV10, null, gameType.getUserRebateV10(), totalAllocationV10);
        // 设置超级用户返点
        setRebate(gameType::setSuperUserRebateV1, null, gameType.getSuperUserRebateV1(), totalAllocationV1);
        setRebate(gameType::setSuperUserRebateV2, null, gameType.getSuperUserRebateV2(), totalAllocationV2);
        setRebate(gameType::setSuperUserRebateV3, null, gameType.getSuperUserRebateV3(), totalAllocationV3);
        setRebate(gameType::setSuperUserRebateV4, null, gameType.getSuperUserRebateV4(), totalAllocationV4);
        setRebate(gameType::setSuperUserRebateV5, null, gameType.getSuperUserRebateV5(), totalAllocationV5);

        // 设置超级用户返点
        setRebate(gameType::setSuperUserRebateV6, null, gameType.getSuperUserRebateV6(), totalAllocationV6);
        setRebate(gameType::setSuperUserRebateV7, null, gameType.getSuperUserRebateV7(), totalAllocationV7);
        setRebate(gameType::setSuperUserRebateV8, null, gameType.getSuperUserRebateV8(), totalAllocationV8);
        setRebate(gameType::setSuperUserRebateV9, null, gameType.getSuperUserRebateV9(), totalAllocationV9);
        setRebate(gameType::setSuperUserRebateV10, null, gameType.getSuperUserRebateV10(), totalAllocationV10);
        // 设置超级用户返点
        gameType.setTotalAllocationV1(totalAllocationV1);
        gameType.setTotalAllocationV2(totalAllocationV2);
        gameType.setTotalAllocationV3(totalAllocationV3);
        gameType.setTotalAllocationV4(totalAllocationV4);
        gameType.setTotalAllocationV5(totalAllocationV5);
        // 设置超级用户返点
        gameType.setTotalAllocationV6(totalAllocationV6);
        gameType.setTotalAllocationV7(totalAllocationV7);
        gameType.setTotalAllocationV8(totalAllocationV8);
        gameType.setTotalAllocationV9(totalAllocationV9);
        gameType.setTotalAllocationV10(totalAllocationV10);
        return gameType;
    }

    private BigDecimal getTotalAllocation(BigDecimal allocation) {
        return allocation == null ? BigDecimal.ZERO : allocation;
    }
    private BigDecimal calculateTotalAllocation(BigDecimal gameTypeAllocation, BigDecimal channelRebate) {
        return channelRebate == null || channelRebate.compareTo(BigDecimal.valueOf(-1L)) == 0 ?
                (gameTypeAllocation == null ? BigDecimal.ZERO : gameTypeAllocation) :
                channelRebate;
    }

    private void setRebate(java.util.function.Consumer<BigDecimal> setter, BigDecimal channelRebate, BigDecimal gameTypeRebate, BigDecimal totalAllocation) {
        if(channelRebate==null||channelRebate.compareTo(BigDecimal.valueOf(-1L))==0){
            setter.accept(gameTypeRebate.multiply(totalAllocation));
        }else {
            setter.accept(channelRebate.multiply(totalAllocation));
        }
    }





}
