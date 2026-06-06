package com.gp.common.mybatisplus.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.constant.S3DirConstant;
import com.common.core.prop.AmazonProp;
import com.common.core.util.RedisUtil;
import com.common.core.util.StringUtils;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.enums.CodeEnum;
import com.gp.common.base.exception.BusinessException;
import com.gp.common.base.utils.BeanUtils;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.mybatisplus.entity.Game;
import com.gp.common.mybatisplus.entity.GamePlate;
import com.gp.common.mybatisplus.entity.GameType;
import com.gp.common.mybatisplus.mapper.GameMapper;
import com.gp.common.mybatisplus.param.ImportGameExcelParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 游戏Service业务层处理
 *
 * @author axing
 * @date 2024-05-07
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class GameService extends ServiceImpl<GameMapper, Game> {

    @Resource
    private GamePlateService gamePlateService;

    @Resource
    private GameTypeService gameTypeService;
    @Resource
    private AmazonProp amazonProp;

    @Autowired
    private GameMapper gameMapper;

    @Resource
    private RedisUtil redisUtil;

    /**
     * 查询游戏
     *
     * @param id 游戏ID
     * @return 游戏
     */

    public Game selectGameById(Long id) {
        return gameMapper.selectGameById(id);
    }

    /**
     * 查询游戏列表
     *
     * @param game 游戏
     * @return 游戏
     */

    public List<Game> selectGameList(Game game) {
        return gameMapper.selectGameList(game);
    }

    /**
     * 查询游戏数量
     *
     * @param game 游戏
     * @return 游戏数量
     */
    public Long selectGameCount(Game game) {
        return gameMapper.selectGameCount(game);
    }

    /**
     * 新增游戏
     *
     * @param param 游戏
     * @return 结果
     */

    public Boolean insertGame(Game param) {

        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        if (result) {
            String fileName = "/" + amazonProp.getDirname() + S3DirConstant.gameUrlDir + param.getId();
            //更新游戏图片
            param.setGamePicEn(fileName + "-us-F.webp");
            param.setGamePicVerticalZh(fileName + "-zh-S.webp");
            param.setGamePicVerticalEn(fileName + "-us-S.webp");
            param.setGamePicVerticalKo(fileName + "-kr-S.webp");
            param.setGamePicVerticalBr(fileName + "-br-S.webp");
            param.setGamePicVerticalVi(fileName + "-vn-S.webp");
            param.setGamePicVerticalTw(fileName + "-tw-S.webp");
            param.setGamePicVerticalJa(fileName + "-jp-S.webp");
            param.setGamePicVerticalHi(fileName + "-hi-S.webp");
            param.setGamePicVerticalTh(fileName + "-th-S.webp");
            param.setGamePicVerticalRu(fileName + "-ru-S.webp");
            param.setGamePicVerticalAr(fileName + "-ar-S.webp");
            result = this.updateById(param);
        }
        return result;
    }

    /**
     * 修改游戏
     *
     * @param param 游戏
     * @return 结果
     */

    public Boolean updateGame(Game param) {
        Game pojo = this.selectGameById(param.getId());
        if (Objects.isNull(pojo)) {
            throw new BusinessException(
                    StringUtils.format(MessagesUtils.get("common.param.is.error")
                            , "id", param.getId())
            );
        }

        //清空所有图片路径
//        param.setGamePicZh(null);
//        param.setGamePicEn(null);
//        param.setGamePicVerticalZh(null);
//        param.setGamePicVerticalEn(null);
//        param.setGamePicVerticalKo(null);
//        param.setGamePicVerticalBr(null);
//        param.setGamePicVerticalVi(null);
//        param.setGamePicVerticalTw(null);
//        param.setGamePicVerticalJa(null);
//        param.setGamePicVerticalHi(null);
//        param.setGamePicVerticalTh(null);

//        param.setGameTypeCode(gamePlate.getGameTypeCode());
        param.setUpdateTime(DateUtils.getNowDate());
        BeanUtils.copyPropertiesIgnoreNull(param, pojo);
        boolean result = this.updateById(pojo);
        return result;
    }

    /**
     * 批量删除游戏
     *
     * @param ids 需要删除的游戏ID
     * @return 结果
     */

    public Boolean deleteGameByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除游戏信息
     *
     * @param id 游戏ID
     * @return 结果
     */

    public Boolean deleteGameById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    public List<Game> queryGameByTag(String typeCode, String plateName, String address) {
        return this.baseMapper.queryGameByTag(typeCode, plateName, address);
    }

    /**
     * 导入游戏列表
     *
     * @param userList 导入结果
     * @param username
     */
    @Transactional(rollbackFor = Exception.class)
    public String importData(List<ImportGameExcelParam> userList, String username) {
        if (StringUtils.isNull(userList) || userList.size() == 0) {
            throw new BusinessException(CodeEnum.Error.getCode(), "导入数据不能为空");
        }
        Map<Long, GamePlate> gamePlateMap;
        //查询所有游戏厂商
        List<Long> collect = userList.stream().map(ImportGameExcelParam::getGamePlateId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(collect)) {
            LambdaQueryWrapper<GamePlate> plateQuery = Wrappers.lambdaQuery(GamePlate.class);
            plateQuery.in(GamePlate::getId, collect);
            List<GamePlate> gamePlateList = gamePlateService.list(plateQuery);
            gamePlateMap = gamePlateList.stream().collect(Collectors.toMap(GamePlate::getId, Function.identity()));
        } else {
            throw new BusinessException(CodeEnum.Error.getCode(), "游戏厂商未查询到");
        }
        //查询所有游戏类型
        Map<String, GameType> gameTypeMap;
        List<String> typeNameList = userList.stream().map(ImportGameExcelParam::getTypeNameZh).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(typeNameList)) {
            LambdaQueryWrapper<GameType> gameTypeLambdaQueryWrapper = Wrappers.lambdaQuery(GameType.class);
            gameTypeLambdaQueryWrapper.in(GameType::getTypeNameZh, typeNameList);
            List<GameType> gameTypeList = gameTypeService.list(gameTypeLambdaQueryWrapper);
            gameTypeMap = gameTypeList.stream().collect(Collectors.toMap(GameType::getTypeNameZh, Function.identity()));
        } else {
            throw new BusinessException(CodeEnum.Error.getCode(), "游戏类型未查询到");
        }
        AtomicInteger i = new AtomicInteger(2);
        List<Game> gameList = CollUtil.newArrayList();
        userList.forEach(x -> {
            if (null == x.getGamePlateId()) {
                throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：游戏厂商ID不能为空");
            }
            if (StrUtil.isBlank(x.getTypeNameZh())) {
                throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：游戏类型名称不能为空");
            }
            if (StrUtil.isBlank(x.getGameNameZh())) {
                throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：游戏名称-中文不能为空");
            }
            if (StrUtil.isBlank(x.getGameNameEn())) {
                throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：游戏名称-英语不能为空");
            }
            if (StrUtil.isBlank(x.getGameCode())) {
                throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：游戏编码不能为空");
            }
            if (null == x.getIsHot()) {
                throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：是否热门不能为空");
            } else {
                if (x.getIsHot() != 0 && x.getIsHot() != 1) {
                    throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：是否热门只能为0或1");
                }
            }
            if (null == x.getStatus()) {
                throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：状态不能为空");
            } else {
                if (x.getStatus() != 0 && x.getStatus() != 1) {
                    throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：状态只能为0或1");
                }
            }
            if (null == x.getAndroidIsOpenH5()) {
                throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：安卓是否打开h5不能为空");
            } else {
                if (x.getAndroidIsOpenH5() != 0 && x.getAndroidIsOpenH5() != 1) {
                    throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：安卓是否打开h5只能为0或1");
                }
            }
            if (null == x.getIosIsOpenH5()) {
                throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：ios是否打开h5不能为空");
            } else {
                if (x.getIosIsOpenH5() != 0 && x.getIosIsOpenH5() != 1) {
                    throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：ios是否打开h5只能为0或1");
                }
            }
            if (null == x.getPcIsOpenH5()) {
                throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：pc是否打开h5不能为空");
            } else {
                if (x.getPcIsOpenH5() != 0 && x.getPcIsOpenH5() != 1) {
                    throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：pc是否打开h5只能为0或1");
                }
            }
            if (null == x.getMacIsOpenH5()) {
                throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：mac是否打开h5不能为空");
            } else {
                if (x.getMacIsOpenH5() != 0 && x.getMacIsOpenH5() != 1) {
                    throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：mac是否打开h5只能为0或1");
                }
            }
            GamePlate gamePlate = gamePlateMap.get(x.getGamePlateId());
            if (null == gamePlate) {
                throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：游戏厂商不存在");
            }
            //同一个游戏厂商和游戏编码不能重复导入
            Game game = null;
            try {
                game = this.getOne(Wrappers.lambdaQuery(Game.class).eq(Game::getPlateCode, gamePlate.getPlateCode())
                        .eq(Game::getDealerCode, gamePlate.getDealerCode()).eq(Game::getGameCode, x.getGameCode()));
            } catch (Exception e) {
                throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：游戏厂商和游戏编码重复");
            }
            if (null == game) {
                Game param = new Game();
                GameType gameType = gameTypeMap.get(x.getTypeNameZh());
                //判断游戏类型是否属于该游戏厂商
                if (gamePlate.getGameTypeCode().contains(gameType.getTypeCode())) {
                    param.setGameTypeCode(gameType.getTypeCode());
                } else {
                    throw new BusinessException(CodeEnum.Error.getCode(), "第" + i.get() + "行，" + x.getGameNameZh() + "：游戏类型与游戏厂商不匹配");
                }

                param.setGameNameZh(x.getGameNameZh());
                param.setGameNameEn(x.getGameNameEn());
                param.setGameNameKo(x.getGameNameKo());
                param.setGameNamePt(x.getGameNamePt());
                param.setGameNameVi(x.getGameNameVi());
                param.setGameNameTr(x.getGameNameTr());
                param.setGameNameTw(x.getGameNameTw());
                param.setGameNameJa(x.getGameNameJa());
                param.setGameNameHi(x.getGameNameHi());
                param.setGameNameTh(x.getGameNameTh());
                param.setGameNameRu(x.getGameNameRu());
                param.setGameInfoAr(x.getGameInfoAr());

                param.setGameCode(x.getGameCode());
                param.setGameCode2(x.getGameCode2());
                param.setGameInfoZh(x.getGameNameZh());
                param.setGameInfoEn(x.getGameNameEn());
                param.setGameInfoKo(x.getGameInfoKo());
                param.setGameInfoPt(x.getGameInfoPt());
                param.setGameInfoVi(x.getGameInfoVi());
                param.setGameInfoTr(x.getGameInfoTr());
                param.setGameInfoTw(x.getGameInfoTw());
                param.setGameInfoJa(x.getGameInfoJa());
                param.setGameInfoHi(x.getGameInfoHi());
                param.setGameInfoTh(x.getGameInfoTh());
                param.setGameInfoRu(x.getGameInfoRu());
                param.setGameInfoAr(x.getGameInfoAr());

                param.setPlateNameZh(gamePlate.getPlateNameZh());
                param.setPlateNameEn(gamePlate.getPlateNameEn());
                param.setPlateNameKo(gamePlate.getPlateNameKo());
                param.setPlateNamePt(gamePlate.getPlateNamePt());
                param.setPlateNameVi(gamePlate.getPlateNameVi());
                param.setPlateNameTr(gamePlate.getPlateNameTr());
                param.setPlateNameTw(gamePlate.getPlateNameTw());
                param.setPlateNameJa(gamePlate.getPlateNameJa());
                param.setPlateNameHi(gamePlate.getPlateNameHi());
                param.setPlateNameTh(gamePlate.getPlateNameTh());
                param.setPlateNameRu(gamePlate.getPlateNameRu());
                param.setPlateNameAr(gamePlate.getPlateNameAr());

                param.setPlateCode(gamePlate.getPlateCode());
                param.setDealerCode(gamePlate.getDealerCode());
                param.setDealerName(gamePlate.getDealerName());

                param.setCategory(gamePlate.getCategory());
                param.setIsHot(x.getIsHot());
                param.setStatus(x.getStatus());
                param.setAndroidIsOpenH5(x.getAndroidIsOpenH5());
                param.setIosIsOpenH5(x.getIosIsOpenH5());
                param.setPcIsOpenH5(x.getPcIsOpenH5());
                param.setMacIsOpenH5(x.getMacIsOpenH5());
                param.setIsNewGame(x.getIsNewGame());
                param.setSort(x.getSort());
                param.setHotSort(x.getHotSort());
                param.setPlateSort(x.getPlateSort());

                param.setCreateBy(username);
                param.setCreateTime(DateUtils.getNowDate());
                gameList.add(param);
            } else {
                //更新游戏-只更新游戏名称和简介
                game.setGameNameZh(x.getGameNameZh());
                game.setGameNameEn(x.getGameNameEn());
                game.setGameNameKo(x.getGameNameKo());
                game.setGameNamePt(x.getGameNamePt());
                game.setGameNameVi(x.getGameNameVi());
                game.setGameNameTr(x.getGameNameTr());
                game.setGameNameTw(x.getGameNameTw());
                game.setGameNameJa(x.getGameNameJa());
                game.setGameNameHi(x.getGameNameHi());
                game.setGameNameTh(x.getGameNameTh());
                game.setGameNameRu(x.getGameNameRu());
                game.setGameNameAr(x.getGameNameAr());

                game.setGameInfoZh(x.getGameNameZh());
                game.setGameInfoEn(x.getGameNameEn());
                game.setGameInfoKo(x.getGameInfoKo());
                game.setGameInfoPt(x.getGameInfoPt());
                game.setGameInfoVi(x.getGameInfoVi());
                game.setGameInfoTr(x.getGameInfoTr());
                game.setGameInfoTw(x.getGameInfoTw());
                game.setGameInfoJa(x.getGameInfoJa());
                game.setGameInfoHi(x.getGameInfoHi());
                game.setGameInfoTh(x.getGameInfoTh());
                game.setGameInfoRu(x.getGameInfoRu());
                game.setGameInfoAr(x.getGameInfoAr());

                game.setSort(x.getSort());
                game.setHotSort(x.getHotSort());
                game.setPlateSort(x.getPlateSort());
                game.setIsNewGame(x.getIsNewGame());
                game.setUpdateBy(username);
                game.setUpdateTime(DateUtils.getNowDate());
                gameList.add(game);
            }
            i.getAndIncrement();
        });
        this.saveOrUpdateBatch(gameList);
        //更新游戏图片
        LambdaQueryWrapper<Game> lambdaQuery = Wrappers.lambdaQuery(Game.class);
        lambdaQuery
                .or(wrapper -> wrapper.isNull(Game::getGamePicEn).or().eq(Game::getGamePicEn, ""))
                .or(wrapper -> wrapper.isNull(Game::getGamePicVerticalZh).or().eq(Game::getGamePicVerticalZh, ""))
                .or(wrapper -> wrapper.isNull(Game::getGamePicVerticalEn).or().eq(Game::getGamePicVerticalEn, ""))
                .or(wrapper -> wrapper.isNull(Game::getGamePicVerticalKo).or().eq(Game::getGamePicVerticalKo, ""))
                .or(wrapper -> wrapper.isNull(Game::getGamePicVerticalBr).or().eq(Game::getGamePicVerticalBr, ""))
                .or(wrapper -> wrapper.isNull(Game::getGamePicVerticalVi).or().eq(Game::getGamePicVerticalVi, ""))
                .or(wrapper -> wrapper.isNull(Game::getGamePicVerticalTw).or().eq(Game::getGamePicVerticalTw, ""))
                .or(wrapper -> wrapper.isNull(Game::getGamePicVerticalJa).or().eq(Game::getGamePicVerticalJa, ""))
                .or(wrapper -> wrapper.isNull(Game::getGamePicVerticalHi).or().eq(Game::getGamePicVerticalHi, ""))
                .or(wrapper -> wrapper.isNull(Game::getGamePicVerticalTh).or().eq(Game::getGamePicVerticalTh, ""))
                .or(wrapper -> wrapper.isNull(Game::getGamePicVerticalRu).or().eq(Game::getGamePicVerticalRu, ""))
                .or(wrapper -> wrapper.isNull(Game::getGamePicVerticalAr).or().eq(Game::getGamePicVerticalAr, ""));
        List<Game> list = this.list(lambdaQuery);
        String fileName = "/" + amazonProp.getDirname() + S3DirConstant.gameUrlDir;
        list.forEach(x -> {
            if (StringUtils.isBlank(x.getGamePicEn())) {
                x.setGamePicEn(fileName + x.getId() + "-us-F.webp");
            }
            if (StringUtils.isBlank(x.getGamePicVerticalZh())) {
                x.setGamePicVerticalZh(fileName + x.getId() + "-zh-S.webp");
            }
            if (StringUtils.isBlank(x.getGamePicVerticalEn())) {
                x.setGamePicVerticalEn(fileName + x.getId() + "-us-S.webp");
            }
            if (StringUtils.isBlank(x.getGamePicVerticalKo())) {
                x.setGamePicVerticalKo(fileName + x.getId() + "-kr-S.webp");
            }
            if (StringUtils.isBlank(x.getGamePicVerticalBr())) {
                x.setGamePicVerticalBr(fileName + x.getId() + "-br-S.webp");
            }
            if (StringUtils.isBlank(x.getGamePicVerticalVi())) {
                x.setGamePicVerticalVi(fileName + x.getId() + "-vn-S.webp");
            }
            if (StringUtils.isBlank(x.getGamePicVerticalTw())) {
                x.setGamePicVerticalTw(fileName + x.getId() + "-tw-S.webp");
            }
            if (StringUtils.isBlank(x.getGamePicVerticalJa())) {
                x.setGamePicVerticalJa(fileName + x.getId() + "-jp-S.webp");
            }
            if (StringUtils.isBlank(x.getGamePicVerticalHi())) {
                x.setGamePicVerticalHi(fileName + x.getId() + "-hi-S.webp");
            }
            if (StringUtils.isBlank(x.getGamePicVerticalTh())) {
                x.setGamePicVerticalTh(fileName + x.getId() + "-th-S.webp");
            }
            if (StringUtils.isBlank(x.getGamePicVerticalRu())) {
                x.setGamePicVerticalRu(fileName + x.getId() + "-ru-S.webp");
            }
            if (StringUtils.isBlank(x.getGamePicVerticalAr())) {
                x.setGamePicVerticalAr(fileName + x.getId() + "-ar-S.webp");
            }

        });
        this.saveOrUpdateBatch(list);
        return "导入成功";

    }

    public List<Game> queryHot(String address) {
        return this.baseMapper.queryHot(address);
    }

    public List<Game> queryGameCondition(String typeCode, String plateName, String keyWord, Integer rank, String address) {
        return this.baseMapper.queryGameCondition(typeCode, plateName, keyWord, rank, null, address);
    }

    public List<Game> queryNewGameOrHot(String typeCode, String plateName, String keyWord, Integer rank, String address, Integer type) {
        return this.baseMapper.queryNewGameOrHot(typeCode, plateName, keyWord, rank, null, address,type);
    }
    public List<Game> queryGameConditionRecent(Long userId, String plateName, String keyWord, Integer rank, String address) {
        String key = StrUtil.format(RedisKey.recentGameKey, CecuUtil.getDbCode(), userId);
        //这里准备从缓存去取
        List<String> recentGameId = redisUtil.getRecentGame(key);
        //从空
        if (CollUtil.isNotEmpty(recentGameId)) {
            return this.baseMapper.queryGameCondition("0", null, keyWord, rank, recentGameId, address);
        }
        return CollUtil.newArrayList();
    }

    public List<Game> randomGame(Integer i) {
        return this.baseMapper.randomGame(i);
    }

    public Game getGameByCodeAndPlateCode(String gameCode, String plateCode) {
        LambdaQueryWrapper<Game> q = new LambdaQueryWrapper<>();
        q.eq(Game::getGameCode, gameCode).eq(Game::getPlateCode, plateCode);
        q.last("limit 1");
        return this.getOne(q);
    }



    public List<String> queryNewOrHotGamePlate(Integer type) {
        return this.baseMapper.queryNewOrHotGamePlate(type);
    }


}
