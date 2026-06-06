package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.mybatisplus.domain.PageDTO;
import com.common.mybatisplus.utils.PageUtil;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.Currency;
import com.gp.common.mybatisplus.entity.GamePlate;
import com.gp.common.mybatisplus.entity.GamePlateCountOrder;
import com.gp.common.mybatisplus.entity.OrderTerm;
import com.gp.common.mybatisplus.mapper.GamePlateCountOrderMapper;
import com.gp.common.mybatisplus.param.QueryGamePlateCountOrderParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 游戏厂商注单相关统计Service业务层处理
 *
 * @author axing
 * @date 2024-06-13
 */
@Slf4j
@Service
public class GamePlateCountOrderService extends ServiceImpl<GamePlateCountOrderMapper, GamePlateCountOrder> {
    @Autowired
    private GamePlateCountOrderMapper gamePlateCountOrderMapper;

    @Resource
    private OrderTermService orderTermService;


    @Resource
    private CurrencyService currencyService;

    @Resource
    private GamePlateService gamePlateService;

    public void gamePlateCountOrderDataHandle(String uuid, String day, int type) {
        String startTime = day + " 00:00:00";
        String endTime = day + " 23:59:59";

        List<Currency> currencyList = currencyService.getOpenCurrencys();
        log.info("uuid ={}, gamePlateCountOrderDataHandle->游戏厂商每日注单统计 -> 统计数据, day={},type={},startTime={}, endTime={}", uuid, day, type, startTime, endTime);
        for (Currency currency : currencyList) {
            Integer currencyId = currency.getId();
            log.info("uuid ={}, gamePlateCountOrderDataHandle->游戏厂商每日注单统计 -> 统计数据, currency={}", uuid, currency);
            List<GamePlate> plates = gamePlateService.list();
            for (GamePlate plate : plates) { //游戏厂商
                String plateCode = plate.getPlateCode();
                log.info("uuid ={}, gamePlateCountOrderDataHandle->游戏厂商每日注单统计 -> 统计数据, 游戏厂商编码={}", uuid, plateCode);
                GamePlateCountOrder pojo = new GamePlateCountOrder();
                pojo.setDayStr(day);
                pojo.setCurrencyId(currency.getId());
                pojo.setItemId(currency.getItemId());
                pojo.setChainTag(currency.getChainTag());
                pojo.setPlateCode(plateCode);
                pojo.setPlateNameZh(plate.getPlateNameZh());
                pojo.setPlateNameEn(plate.getPlateNameEn());
                pojo.setPlateNameKo(plate.getPlateNameKo());
                pojo.setPlateNamePt(plate.getPlateNamePt());
                pojo.setPlateNameVi(plate.getPlateNameVi());
                pojo.setPlateNameTr(plate.getPlateNameTr());
                pojo.setPlateNameTw(plate.getPlateNameTw());
                pojo.setPlateNameJa(plate.getPlateNameJa());
                pojo.setPlateNameHi(plate.getPlateNameHi());
                pojo.setPlateNameTh(plate.getPlateNameTh());
                pojo.setPlateNameRu(plate.getPlateNameRu());
                pojo.setPlateNameAr(plate.getPlateNameAr());
                pojo.setCalculationsTime(new Date());
                pojo.setUpdateTime(DateUtils.getNowDate());
                pojo.setCreateTime(DateUtils.getEndTimeByDate(day));
//  ---------------------------------------------------分割线---------------------------------------------------------------------------------------

//                List<OrderTerm> betOrderTermList = fetchOrderTerms(startTime, endTime, plateCode, currencyId, type, false);
                //`bet_amount` decimal(16,4) DEFAULT '0.0000' COMMENT '投注金额',
                //用户游戏投注金额(不分输赢)-当日
//                BigDecimal betAmount = betOrderTermList.stream()
//                        .map(OrderTerm::getBetAmount)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);
//                pojo.setBetAmount(betAmount);
                //游戏投注数量(不分输赢)
//                Integer betNum = betOrderTermList.size();
//                pojo.setBetNum(betNum);
                //游戏投注人数(不分输赢)
//                List<Long> betUserIdList = betOrderTermList.stream()
//                        .map(OrderTerm::getUserId)
//                        .distinct()
//                        .collect(Collectors.toList());
//                pojo.setBetPeopleNum(betUserIdList.size());

//        List<OrderTerm> settleOrderTermList = fetchOrderTerms(startTime, endTime, plateCode, currencyId, type, true);
                //  `win` decimal(16,4) DEFAULT NULL COMMENT '返奖(本金+奖金)',
                //结算金额(返奖金额)
//                BigDecimal settleWinAmount = settleOrderTermList.stream()
//                        .filter(it -> Objects.nonNull(it.getWin()))
//                        .map(OrderTerm::getWin)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);
//                pojo.setSettleAmount(settleWinAmount);

                //`bet_amount` decimal(16,4) DEFAULT '0.0000' COMMENT '投注金额',
                //有效投注金额(分输赢)-当日
//                BigDecimal settleBetAmount = settleOrderTermList.stream()
//                        .map(OrderTerm::getBetAmount)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);
//                pojo.setEfficientBetAmount(settleBetAmount);

                //输赢金额(返奖额减去投注额)-当日
//                pojo.setWinLoseAmount(settleWinAmount.subtract(settleBetAmount));

                //`code_amount` decimal(16,4) DEFAULT NULL COMMENT '打码量(真实投注额)',
                //打码量  code_amount
//                BigDecimal codeAmount = settleOrderTermList.stream()
//                        .filter(it -> Objects.nonNull(it.getCodeAmount()))
//                        .map(OrderTerm::getCodeAmount)
//                        .reduce(BigDecimal.ZERO, BigDecimal::add);
//                pojo.setCodeAmount(codeAmount);

                Map<String, Object> betOrderTermMap;//创建时间
                Map<String, Object> settleTermMap;//结算时间

                    // 查询 OrderTerm 表
                    betOrderTermMap = orderTermService.fetchOrderTermsBycreatTime(startTime, endTime, plateCode, currencyId);
                    settleTermMap = orderTermService.fetchOrderTermsBysettleTime(startTime, endTime, plateCode, currencyId);

                pojo.setBetAmount((BigDecimal) betOrderTermMap.get("betAmount"));
                pojo.setBetNum(longToInt(betOrderTermMap, "counts"));
                pojo.setBetPeopleNum(longToInt(betOrderTermMap, "userCount"));

                //结算金额(返奖金额)
                pojo.setSettleAmount((BigDecimal) settleTermMap.get("win"));
                //有效投注金额(分输赢)-当日
                pojo.setEfficientBetAmount((BigDecimal) settleTermMap.get("betAmount"));
                //输赢金额(返奖额减去投注额)-当日
                pojo.setWinLoseAmount(pojo.getSettleAmount().subtract(pojo.getEfficientBetAmount()));
                //打码量  code_amount
                pojo.setCodeAmount((BigDecimal) settleTermMap.get("codeAmount"));
//  ---------------------------------------------------分割线---------------------------------------------------------------------------------------
                //游戏方费用=输赢绝对值*游戏方的费率
                BigDecimal winLoseAmount = pojo.getWinLoseAmount().abs();
                pojo.setGameCost(winLoseAmount.multiply(plate.getGameRate()));
                //包网方费用=输赢绝对值*包网方的费率
                pojo.setGameCostMerchant(winLoseAmount.multiply(plate.getGameRateMerchant()));

                LambdaUpdateWrapper<GamePlateCountOrder> saveOrUpdateWrapper = Wrappers.lambdaUpdate(GamePlateCountOrder.class);
                saveOrUpdateWrapper.eq(GamePlateCountOrder::getDayStr, day);
                saveOrUpdateWrapper.eq(GamePlateCountOrder::getCurrencyId, currencyId);
                saveOrUpdateWrapper.eq(GamePlateCountOrder::getPlateCode, plateCode);
                boolean result = this.saveOrUpdate(pojo, saveOrUpdateWrapper);
                log.info("uuid={}, GamePlateCountOrderService -> gamePlateCountOrderDataHandle -> 游戏厂商每日注单统计" +
                        ", day={}, currencyId={}, plateCode={}, result={}", uuid, day, currencyId, plateCode, result);

            }
        }
    }

    /**
     * 查询游戏厂商注单相关统计
     *
     * @param id 游戏厂商注单相关统计ID
     * @return 游戏厂商注单相关统计
     */

    public GamePlateCountOrder selectGamePlateCountOrderById(Long id) {
        return gamePlateCountOrderMapper.selectGamePlateCountOrderById(id);
    }

    /**
     * 查询游戏厂商注单相关统计列表
     *
     * @param param 游戏厂商注单相关统计
     * @return 游戏厂商注单相关统计
     */

    public List<GamePlateCountOrder> selectGamePlateCountOrderList(GamePlateCountOrder param) {
        return gamePlateCountOrderMapper.selectGamePlateCountOrderList(param);
    }

    /**
     * 新增游戏厂商注单相关统计
     *
     * @param param 游戏厂商注单相关统计
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertGamePlateCountOrder(GamePlateCountOrder param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改游戏厂商注单相关统计
     *
     * @param param 游戏厂商注单相关统计
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateGamePlateCountOrder(GamePlateCountOrder param) {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除游戏厂商注单相关统计
     *
     * @param ids 需要删除的游戏厂商注单相关统计ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteGamePlateCountOrderByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除游戏厂商注单相关统计信息
     *
     * @param id 游戏厂商注单相关统计ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteGamePlateCountOrderById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    /**
     * 总计
     */
    public GamePlateCountOrder countSum(QueryGamePlateCountOrderParam param) {
        return gamePlateCountOrderMapper.countSum(param);
    }

    public List<GamePlateCountOrder> selectSumByMonth(QueryGamePlateCountOrderParam param) {
        return gamePlateCountOrderMapper.selectSumByMonth(param);
    }

    public GamePlateCountOrder selectSumOneByMonthStr(QueryGamePlateCountOrderParam param) {
        return gamePlateCountOrderMapper.selectSumOneByMonthStr(param);
    }

    /**
     * 条件查询注单数据
     *
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param plateCode  游戏平台编码
     * @param currencyId 币种id
     * @param type       1当天表/2总表
     * @param isSettle   true-结算时间(settleTime) false-创建时间 (createTime)
     * @return
     */
    public List<OrderTerm> fetchOrderTerms(String startTime, String endTime, String plateCode, int currencyId, int type, boolean isSettle) {
        log.info("fetchOrderTerms ->条件查询注单数据, 开始时间={}, 结束时间={},isSettle={}", startTime, endTime, isSettle);

            // 查询 OrderTerm 表
            return queryOrderTermData(OrderTerm.class, startTime, endTime, plateCode, currencyId, isSettle);

    }

    private <T> List<T> queryOrderTermData(Class<T> orderTermClass, String startTime, String endTime, String plateCode, int currencyId,
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
            queryWrapper.eq(OrderTerm::getPlateCode, plateCode)
                    .eq(OrderTerm::getCurrencyId, currencyId)
                    .in(OrderTerm::getOrderType, 0, 1);
        }
        PageDTO<T> page;
        do {
            PageUtil.startPage(pageNum, pageSize);
            List<T> result = null;
            // 根据类型返回查询结果
            if (OrderTerm.class.isAssignableFrom(orderTermClass)) {
                result = (List<T>) orderTermService.list(queryWrapper);
            }
            page = PageUtil.buildPageDto(result);
            list.addAll(result);
            pageNum++;  // 增加页码
        } while (page.getHasNextPage());
        return list;
    }

    private Integer longToInt(Map<String, Object> map, String key) {
        Long aLong = (Long) map.get(key);
        return aLong.intValue();
    }

    public List<GamePlateCountOrder> getByPlateCodeSumList(QueryGamePlateCountOrderParam param) {
        return gamePlateCountOrderMapper.getByPlateCodeSumList(param);
    }

    public List<GamePlateCountOrder> getByPlateCodeMonthList(QueryGamePlateCountOrderParam param) {
        return gamePlateCountOrderMapper.getByPlateCodeMonthList(param);
    }
}
