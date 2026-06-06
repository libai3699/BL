package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.util.StringUtils;
import com.gp.common.base.exception.BusinessException;
import com.gp.common.base.utils.BeanUtils;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.mybatisplus.entity.Game;
import com.gp.common.mybatisplus.entity.GameDealer;
import com.gp.common.mybatisplus.entity.GamePlate;
import com.gp.common.mybatisplus.mapper.GamePlateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 游戏厂商Service业务层处理
 *
 * @author axing
 * @date 2024-05-07
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class GamePlateService extends ServiceImpl<GamePlateMapper, GamePlate> {

    @Resource
    private GameDealerService gameDealerService;

    @Resource
    private GameService gameService;

    @Autowired
    private GamePlateMapper gamePlateMapper;

    /**
     * 查询游戏厂商
     *
     * @param id 游戏厂商ID
     * @return 游戏厂商
     */

    public GamePlate selectGamePlateById(Long id) {
        return gamePlateMapper.selectGamePlateById(id);
    }

    /**
     * 查询游戏厂商列表
     *
     * @param gamePlate 游戏厂商
     * @return 游戏厂商
     */

    public List<GamePlate> selectGamePlateList(GamePlate gamePlate) {
        return gamePlateMapper.selectGamePlateList(gamePlate);
    }

    /**
     * 查询游戏厂商数量
     *
     * @param gamePlate 游戏厂商
     * @return 游戏厂商数量
     */
    public Long selectGamePlateCount(GamePlate gamePlate) {
        return gamePlateMapper.selectGamePlateCount(gamePlate);
    }

    /**
     * 新增游戏厂商
     *
     * @param param 游戏厂商
     * @return 结果
     */

    public Boolean insertGamePlate(GamePlate param) {
        LambdaQueryWrapper<GameDealer> dealerQuery = Wrappers.lambdaQuery(GameDealer.class);
        dealerQuery.eq(GameDealer::getDealerCode, param.getDealerCode());
        dealerQuery.last("limit 1");
        GameDealer gameDealer = gameDealerService.getOne(dealerQuery);
        if (Objects.isNull(gameDealer)) {
            throw new BusinessException(StringUtils.format(MessagesUtils.get("common.param.is.error"), "dealerCode"));
        }
        param.setDealerName(gameDealer.getDealerName());
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改游戏厂商
     *
     * @param param 游戏厂商
     * @return 结果
     */

    public Boolean updateGamePlate(GamePlate param) {
        GamePlate pojo = this.selectGamePlateById(param.getId());
        if (Objects.isNull(pojo)) {
            throw new BusinessException(
                    StringUtils.format(MessagesUtils.get("common.param.is.error")
                            , "id", param.getId())
            );
        }
        LambdaQueryWrapper<GameDealer> dealerQuery = Wrappers.lambdaQuery(GameDealer.class);
        dealerQuery.eq(GameDealer::getDealerCode, param.getDealerCode());
        dealerQuery.last("limit 1");
        GameDealer gameDealer = gameDealerService.getOne(dealerQuery);
        if (Objects.isNull(gameDealer)) {
            throw new BusinessException(StringUtils.format(MessagesUtils.get("common.param.is.error"), "dealerCode"));
        }
        param.setUpdateTime(DateUtils.getNowDate());
        BeanUtils.copyPropertiesIgnoreNull(param, pojo);
        pojo.setDefaultGameId(param.getDefaultGameId());
        boolean result = this.updateById(pojo);
        if (result) {
            LambdaQueryWrapper<Game> gameQuery = Wrappers.lambdaQuery(Game.class);
            gameQuery.eq(Game::getPlateCode, pojo.getPlateCode());
            List<Game> games = gameService.list(gameQuery);
            games.stream().forEach(it -> {
                it.setPlateNameZh(pojo.getPlateNameZh());
                it.setPlateNameEn(pojo.getPlateNameEn());
            });
            gameService.updateBatchById(games);
        }
        return result;
    }

    /**
     * 批量删除游戏厂商
     *
     * @param ids 需要删除的游戏厂商ID
     * @return 结果
     */

    public Boolean deleteGamePlateByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除游戏厂商信息
     *
     * @param id 游戏厂商ID
     * @return 结果
     */

    public Boolean deleteGamePlateById(Integer id) {
        boolean result = this.removeById(id);
        return result;
    }

    public List<GamePlate> queryPlateByType(String gameTypeCode) {
        //主要获取 category_zh  category_en
        List<GamePlate> gamePlates = this.baseMapper.queryPlateByType(gameTypeCode);
        return gamePlates;
    }

    public GamePlate getPlateByCode(String plateCode) {
        LambdaQueryWrapper<GamePlate> q = new LambdaQueryWrapper<>();
        q.eq(GamePlate::getPlateCode, plateCode);
        q.last("limit 1");
        GamePlate one = this.getOne(q);
        return one;
    }

    public List<GamePlate> queryPlateListByCode(List<String> collect) {
        LambdaQueryWrapper<GamePlate> q = new LambdaQueryWrapper<>();
        q.in(GamePlate::getPlateCode, collect);
        q.eq(GamePlate::getIsShow, 1);
        return this.list(q);
    }
}