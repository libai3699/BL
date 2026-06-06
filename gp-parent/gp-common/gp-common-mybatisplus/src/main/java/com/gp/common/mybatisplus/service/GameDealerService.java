package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.util.StringUtils;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.Game;
import com.gp.common.mybatisplus.entity.GameDealer;
import com.gp.common.mybatisplus.entity.GamePlate;
import com.gp.common.mybatisplus.mapper.GameDealerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 游戏商Service业务层处理
 *
 * @author axing
 * @date 2024-05-07
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class GameDealerService extends ServiceImpl<GameDealerMapper, GameDealer>
{

    @Resource
    private GameService gameService;

    @Resource
    private GamePlateService gamePlateService;



    @Autowired
    private GameDealerMapper gameDealerMapper;

    /**
     * 查询游戏商
     *
     * @param dealerId 游戏商ID
     * @return 游戏商
     */

    public GameDealer selectGameDealerById(Long dealerId)
    {
        return gameDealerMapper.selectGameDealerById(dealerId);
    }

    /**
     * 查询游戏商列表
     *
     * @param param 游戏商
     * @return 游戏商
     */

    public List<GameDealer> selectGameDealerList(GameDealer param)
    {
        return gameDealerMapper.selectGameDealerList(param);
    }

    /**
     * 查询游戏商数量
     *
     * @param param 游戏商
     * @return 游戏商数量
     */
    public Long selectGameDealerCount(GameDealer param)
    {
        return gameDealerMapper.selectGameDealerCount(param);
    }

    /**
     * 新增游戏商
     *
     * @param param 游戏商
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertGameDealer(GameDealer param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改游戏商
     *
     * @param param 游戏商
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateGameDealer(GameDealer param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        if( result ){
            //游戏代理商数据
            GameDealer dealer = this.getById(param.getDealerId());
            if(Objects.nonNull(dealer) && StringUtils.isNotEmpty(dealer.getDealerCode())){
                //修改游戏厂商数据
                LambdaQueryWrapper<GamePlate> gamePlateQuery = Wrappers.lambdaQuery(GamePlate.class);
                gamePlateQuery.eq(GamePlate::getDealerCode, dealer.getDealerCode());
                List<GamePlate> gamePlates = gamePlateService.list(gamePlateQuery);
                gamePlates.stream().forEach(it -> {
                    it.setDealerName(dealer.getDealerName());
                });
                gamePlateService.updateBatchById(gamePlates);

                //修改游戏代理商数据
                LambdaQueryWrapper<Game> gameQuery = Wrappers.lambdaQuery(Game.class);
                gameQuery.eq(Game::getDealerCode, dealer.getDealerCode());
                List<Game> games = gameService.list(gameQuery);
                games.stream().forEach(it -> {
                    it.setDealerName(dealer.getDealerName());
                });
                gameService.updateBatchById(games);
            }
        }
        return result;
    }

    /**
     * 批量删除游戏商
     *
     * @param dealerIds 需要删除的游戏商ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteGameDealerByIds(Long[] dealerIds)
    {
        boolean result = this.removeByIds(Arrays.asList(dealerIds));
        return result;
    }

    /**
     * 删除游戏商信息
     *
     * @param dealerId 游戏商ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteGameDealerById(Long dealerId)
    {
        boolean result = this.removeById(dealerId);
        return result;
    }
}
