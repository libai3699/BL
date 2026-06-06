package com.gp.common.mybatisplus.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.mybatisplus.domain.PageDTO;
import com.common.mybatisplus.utils.PageUtil;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.*;
import com.gp.common.mybatisplus.entity.Currency;
import com.gp.common.mybatisplus.mapper.GameTypeCountOrderMapper;
import com.gp.common.mybatisplus.mapper.OrderTermMapper;
import com.gp.common.mybatisplus.param.QueryGameTypeCountOrderParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 游戏类型注单相关统计Service业务层处理
 *
 * @author axing
 * @date 2025-06-13
 */
@Slf4j
@Service
public class GameTypeCountOrderService extends ServiceImpl<GameTypeCountOrderMapper, GameTypeCountOrder> {
    @Autowired
    private GameTypeCountOrderMapper gameTypeCountOrderMapper;
    @Autowired
    private GameTypeService gameTypeService;
    @Resource
    private CurrencyService currencyService;
    @Resource
    private OrderTermService orderTermService;

    @Resource
    private OrderTermMapper orderTermMapper;

    public void gameTypeCountOrderDataHandle(String uuid, String day, int type) {
        String startTime = day + " 00:00:00";
        String endTime = day + " 23:59:59";

        List<Currency> currencyList = currencyService.getOpenCurrencys();

        log.info("uuid ={}, gameTypeCountOrderDataHandle ->统计数据, 开始时间={}, 结束时间={},type={}", uuid, startTime, endTime, type);
        for (Currency currency : currencyList) {
            Integer currencyId = currency.getId();

            LambdaQueryWrapper<GameType> plateLambdaQuery = Wrappers.lambdaQuery(GameType.class);
            //状态(0 关闭, 1 开启)
//            plateLambdaQuery.eq(GameType::getStatus, 1);
            List<GameType> typesList = gameTypeService.list(plateLambdaQuery);
            for (GameType types : typesList) { //游戏厂商
                String typeCode = types.getTypeCode();

                GameTypeCountOrder pojo = new GameTypeCountOrder();
                pojo.setDayStr(day);
                pojo.setUpdateTime(DateUtils.getNowDate());
                pojo.setCreateTime(DateUtils.getEndTimeByDate(day));
                pojo.setCurrencyId(currency.getId());
                pojo.setItemId(currency.getItemId());
                pojo.setChainTag(currency.getChainTag());
                pojo.setTypeCode(typeCode);
                pojo.setTypeNameZh(types.getTypeNameZh());
                pojo.setTypeNameEn(types.getTypeNameEn());
                pojo.setTypeNameKo(types.getTypeNameKo());
                pojo.setTypeNamePt(types.getTypeNamePt());
                pojo.setTypeNameVi(types.getTypeNameVi());
                pojo.setTypeNameTr(types.getTypeNameTr());
                pojo.setTypeNameTw(types.getTypeNameTw());
                pojo.setTypeNameJa(types.getTypeNameJa());
                pojo.setTypeNameHi(types.getTypeNameHi());
                pojo.setTypeNameTh(types.getTypeNameTh());
                pojo.setTypeNameRu(types.getTypeNameRu());
                pojo.setTypeNameAr(types.getTypeNameAr());

                BigDecimal betAmount;
                int betNum;
                int betPeopleNum;
                //当天表
                Map<String, Object> gameTypeCodeMap = orderTermMapper.gameTypeByCreateTimeCountMap(startTime, endTime, currency.getId(), typeCode);
                betAmount = (BigDecimal) gameTypeCodeMap.get("betAmount");
                betNum = longToInt(gameTypeCodeMap, "betNum");
                betPeopleNum = longToInt(gameTypeCodeMap, "betPeopleNum");

                pojo.setBetAmount(betAmount);//用户游戏投注金额(不分输赢)-当日
                pojo.setBetNum(betNum);//游戏投注数量(不分输赢)
                pojo.setBetPeopleNum(betPeopleNum);//游戏投注人数(不分输赢)

//                //true-结算时间(settleTime) false-创建时间 (createTime)
//                List<OrderTerm> betOrderTermList = fetchOrderTerms(startTime, endTime, typeCode, currencyId, type, false);
//
//                //`bet_amount` decimal(16,4) DEFAULT '0.0000' COMMENT '投注金额',
//                //用户游戏投注金额(不分输赢)-当日
//                BigDecimal betAmount = betOrderTermList.stream()
//                        .map(OrderTerm::getBetAmount)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);
//                pojo.setBetAmount(betAmount);
//                //游戏投注数量(不分输赢)
//                Integer betNum = betOrderTermList.size();
//                pojo.setBetNum(betNum);
//                //游戏投注人数(不分输赢)
//                List<Long> betUserIdList = betOrderTermList.stream()
//                        .map(OrderTerm::getUserId)
//                        .distinct()
//                        .collect(Collectors.toList());
//                pojo.setBetPeopleNum(betUserIdList.size());
//-----------------------------------------------------------------------------------------------------------------------------
                BigDecimal settleWinAmount;
                BigDecimal settleBetAmount;
                BigDecimal codeAmount;

                //当天表
                Map<String, Object> gameTypeBySettleTimeCountMap = orderTermMapper.gameTypeBySettleTimeCountMap(startTime, endTime, currency.getId(), typeCode);
                settleWinAmount = (BigDecimal) gameTypeBySettleTimeCountMap.get("settleWinAmount");
                settleBetAmount = (BigDecimal) gameTypeBySettleTimeCountMap.get("settleBetAmount");
                codeAmount = (BigDecimal) gameTypeBySettleTimeCountMap.get("codeAmount");

                pojo.setSettleAmount(settleWinAmount);//结算金额(返奖金额)
                pojo.setEfficientBetAmount(settleBetAmount);//有效投注金额(分输赢)-当日
                pojo.setCodeAmount(codeAmount);//打码量
                pojo.setWinLoseAmount(settleWinAmount.subtract(settleBetAmount)); //输赢金额(返奖额减去投注额)-当日

//                List<OrderTerm> settleOrderTermList = fetchOrderTerms(startTime, endTime, typeCode, currencyId, type, true);
//
//                //  `win` decimal(16,4) DEFAULT NULL COMMENT '返奖(本金+奖金)',
//                //结算金额(返奖金额)
//                BigDecimal settleWinAmount = settleOrderTermList.stream()
//                        .filter(it -> Objects.nonNull(it.getWin()))
//                        .map(OrderTerm::getWin)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);
//                pojo.setSettleAmount(settleWinAmount);
//
//                //`bet_amount` decimal(16,4) DEFAULT '0.0000' COMMENT '投注金额',
//                //有效投注金额(分输赢)-当日
//                BigDecimal settleBetAmount = settleOrderTermList.stream()
//                        .map(OrderTerm::getBetAmount)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);
//                pojo.setEfficientBetAmount(settleBetAmount);
//
//                //输赢金额(返奖额减去投注额)-当日
//                pojo.setWinLoseAmount(settleWinAmount.subtract(settleBetAmount));
//
//                //`code_amount` decimal(16,4) DEFAULT NULL COMMENT '打码量(真实投注额)',
//                //打码量  code_amount
//                BigDecimal codeAmount = settleOrderTermList.stream()
//                        .filter(it -> Objects.nonNull(it.getCodeAmount()))
//                        .map(OrderTerm::getCodeAmount)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);
//                pojo.setCodeAmount(codeAmount);

                pojo.setCalculationsTime(new Date());
                LambdaUpdateWrapper<GameTypeCountOrder> saveOrUpdateWrapper = Wrappers.lambdaUpdate(GameTypeCountOrder.class);
                saveOrUpdateWrapper.eq(GameTypeCountOrder::getDayStr, day);
                saveOrUpdateWrapper.eq(GameTypeCountOrder::getCurrencyId, currencyId);
                saveOrUpdateWrapper.eq(GameTypeCountOrder::getTypeCode, typeCode);
                boolean result = this.saveOrUpdate(pojo, saveOrUpdateWrapper);
                log.info("uuid={}, GameTypeCountOrderService -> gamePlateCountOrderDataHandle -> saveOrUpdate" +
                                ", day={}, currencyId={}, plateCode={}, result={}"
                        , uuid, day, currencyId, typeCode, result
                );

            }
        }
    }

    private static Integer longToInt(Map<String, Object> map, String key) {
        Long aLong = (Long) map.get(key);
        return aLong.intValue();
    }

    /**
     * 条件查询注单数据
     *
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param typeCode   游戏类型编码
     * @param currencyId 币种id
     * @param type       1当天表/2总表
     * @param isSettle   true-结算时间(settleTime) false-创建时间 (createTime)
     * @return
     */
    public List<OrderTerm> fetchOrderTerms(String startTime, String endTime, String typeCode, int currencyId, int type, boolean isSettle) {
        log.info("fetchOrderTerms ->条件查询注单数据, 开始时间={}, 结束时间={},isSettle={}", startTime, endTime, isSettle);

        // 查询 OrderTerm 表
        return queryOrderTermData(OrderTerm.class, startTime, endTime, typeCode, currencyId, isSettle);

    }

    private <T> List<T> queryOrderTermData(Class<T> orderTermClass, String startTime, String endTime, String typeCode, int currencyId,
                                           boolean isSettle) {
        List<T> list = new ArrayList<>();
        int pageNum = 1;
        int pageSize = 10000;

        LambdaQueryWrapper<OrderTerm> queryWrapper = Wrappers.lambdaQuery(OrderTerm.class);
        log.info("queryOrderTermData ->条件查询注单数据, orderTermClass={}", orderTermClass);

        // 根据类型使用不同的时间范围：下单时间或结算时间
        if (isSettle) {
            if (OrderTerm.class.isAssignableFrom(orderTermClass)) {
                queryWrapper.between(OrderTerm::getSettleTime, startTime, endTime);
            }
        } else {
            if (OrderTerm.class.isAssignableFrom(orderTermClass)) {
                queryWrapper.between(OrderTerm::getCreateTime, startTime, endTime);
            }
        }

        // 设置游戏平台编码和币种ID等查询条件
        if (OrderTerm.class.isAssignableFrom(orderTermClass)) {
            queryWrapper.eq(OrderTerm::getGameTypeCode, typeCode)
                    .eq(OrderTerm::getCurrencyId, currencyId)
                    .in(OrderTerm::getOrderType, 0, 1);
        }
        PageDTO<T> page;
        do {
            PageUtil.startPage(pageNum, pageSize);
            List<T> result;
            // 根据类型返回查询结果

                result = (List<T>) orderTermService.list(queryWrapper);

            page = PageUtil.buildPageDto(result);
            list.addAll(result);
            pageNum++;  // 增加页码
        } while (page.getHasNextPage());

        return list;
    }

    public List<GameTypeCountOrder> selectSumByMonth(QueryGameTypeCountOrderParam param) {
        return gameTypeCountOrderMapper.selectSumByMonth(param);
    }

    public GameTypeCountOrder selectSumOneByMonthStr(QueryGameTypeCountOrderParam param) {
        return gameTypeCountOrderMapper.selectSumOneByMonthStr(param);
    }

    /**
     * 总计
     */
    public GameTypeCountOrder countSum(QueryGameTypeCountOrderParam param) {
        return gameTypeCountOrderMapper.countSum(param);
    }

    /**
     * 查询游戏类型注单相关统计
     *
     * @param id 游戏类型注单相关统计ID
     * @return 游戏类型注单相关统计
     */

    public GameTypeCountOrder selectGameTypeCountOrderById(Long id) {
        return gameTypeCountOrderMapper.selectGameTypeCountOrderById(id);
    }

    /**
     * 查询游戏类型注单相关统计列表
     *
     * @param param 游戏类型注单相关统计
     * @return 游戏类型注单相关统计
     */

    public List<GameTypeCountOrder> selectGameTypeCountOrderList(GameTypeCountOrder param) {
        return gameTypeCountOrderMapper.selectGameTypeCountOrderList(param);
    }

    public List<GameTypeCountOrder> getByGameTypeSumList(QueryGameTypeCountOrderParam param) {
        return gameTypeCountOrderMapper.getByGameTypeSumList(param);
    }

    public List<GameTypeCountOrder> getByTypeCodeMonthList(QueryGameTypeCountOrderParam param) {
        return gameTypeCountOrderMapper.getByTypeCodeMonthList(param);
    }
}
