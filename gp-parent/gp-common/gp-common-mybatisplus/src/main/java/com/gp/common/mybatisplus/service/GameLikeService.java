package com.gp.common.mybatisplus.service;

import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.common.core.util.RedisUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.DelConstants;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.mybatisplus.entity.Game;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.GameLikeMapper;
import com.gp.common.mybatisplus.entity.GameLike;
import com.gp.common.mybatisplus.service.GameLikeService;

/**
 * 游戏收藏Service业务层处理
 *
 * @author axing
 * @date 2024-05-09
 */
@Service
public class GameLikeService extends ServiceImpl<GameLikeMapper, GameLike>
{
    @Autowired
    private GameLikeMapper gameLikeMapper;
    @Autowired
    private RedisUtil redisUtil;
    /**
     * 查询游戏收藏
     *
     * @param id 游戏收藏ID
     * @return 游戏收藏
     */

    public GameLike selectGameLikeById(Long id)
    {
        return gameLikeMapper.selectGameLikeById(id);
    }

    /**
     * 查询游戏收藏列表
     *
     * @param param 游戏收藏
     * @return 游戏收藏
     */

    public List<GameLike> selectGameLikeList(GameLike param)
    {
        return gameLikeMapper.selectGameLikeList(param);
    }

    /**
     * 新增游戏收藏
     *
     * @param param 游戏收藏
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertGameLike(GameLike param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改游戏收藏
     *
     * @param param 游戏收藏
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateGameLike(GameLike param)
    {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除游戏收藏
     *
     * @param ids 需要删除的游戏收藏ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteGameLikeByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除游戏收藏信息
     *
     * @param id 游戏收藏ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteGameLikeById(Long id)
    {
        GameLike gameLike = new GameLike();
        gameLike.setHasDel(DelConstants.del);
        gameLike.setId(id);
        boolean result = this.updateById(gameLike);
        return result;
    }

    public void handleLike(GameLike gameLike, Integer type) {
        //  0 取消收藏 1 收藏
        Integer hasDel = gameLike.getHasDel();
        gameLike.setCreateTime(new Date());
        if(type == 0){
            // 取消收藏
            if(hasDel==0){
                //已经删除就不用了
            }
            if(hasDel==1){
                gameLike.setHasDel(DelConstants.del);
                this.updateById(gameLike);
            }
        }

        if(type == 1){
            // 收藏
            if(hasDel==0){
                gameLike.setHasDel(DelConstants.noDel);
                this.updateById(gameLike);
            }
            if(hasDel==1){
            }
        }
    }

    public List<Game> queryMyLike(Long userId,String plateName,String keyWord,Integer rank,String address) {
       return this.baseMapper.queryMyLike(userId,plateName,keyWord,rank,address);
    }

    public List<GameLike> queryLike(Long userId, List<Long> collect) {
        return this.baseMapper.queryLove(userId,collect);
    }

}
