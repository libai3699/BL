package com.common.elasticsearch.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.common.elasticsearch.entity.ESPage;
import com.common.elasticsearch.entity.EsOrderTermEntity;
import com.common.elasticsearch.entity.EsUserAmountSummary;
import com.common.elasticsearch.rule.ESIndexRule;
import com.common.elasticsearch.search.SearchEsTermChange;
import com.common.elasticsearch.template.ESRestTemplate;
import com.common.elasticsearch.util.EsDateUtil;
import com.gp.common.base.utils.DateUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedCardinality;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author axing
 * @version 1.0
 * @date 2023/10/16/016 15:43
 */
@Data
@Slf4j
public class EsOrderTermService implements Serializable {

    private static final long serialVersionUID = 1L;

    private ESRestTemplate esRestTemplate;

    public void save(EsOrderTermEntity esOrderTermEntity, String productId) {
        esOrderTermEntity.setEsId(esOrderTermEntity.getId());
        esOrderTermEntity.setVersion(esOrderTermEntity.getUpdateVersion());
        esRestTemplate.Builder()
                .indexName(ESIndexRule.orderTermIndex(productId))
                .mapping(EsOrderTermEntity.class)
                .bulkOps().save(esOrderTermEntity);
    }

    public void saveAll(List<EsOrderTermEntity> esOrderTermEntities, String productId) {
        for (EsOrderTermEntity esOrderTermEntity : esOrderTermEntities) {
            esOrderTermEntity.setEsId(esOrderTermEntity.getId());
            esOrderTermEntity.setVersion(esOrderTermEntity.getUpdateVersion());
        }
        esRestTemplate.Builder()
                .indexName(ESIndexRule.orderTermIndex(productId))
                .mapping(EsOrderTermEntity.class)
                .bulkOps().saveAll(esOrderTermEntities);
    }

    public void updateById(EsOrderTermEntity esOrderTermEntity, String productId) {
        esOrderTermEntity.setEsId(esOrderTermEntity.getId());
        esOrderTermEntity.setVersion(esOrderTermEntity.getUpdateVersion());
        esRestTemplate.Builder()
                .indexName(ESIndexRule.orderTermIndex(productId))
                .mapping(EsOrderTermEntity.class)
                .bulkOps().update(esOrderTermEntity);
    }

    public void updateAll(List<EsOrderTermEntity> esOrderTermEntities, String productId) {
        for (EsOrderTermEntity esOrderTermEntity : esOrderTermEntities) {
            esOrderTermEntity.setEsId(esOrderTermEntity.getId());
            esOrderTermEntity.setVersion(esOrderTermEntity.getUpdateVersion());
        }
        esRestTemplate.Builder()
                .indexName(ESIndexRule.orderTermIndex(productId))
                .mapping(EsOrderTermEntity.class)
                .bulkOps().batchUpdate(esOrderTermEntities);
    }

    public void delById(String id, String productId) {
        esRestTemplate.Builder()
                .indexName(ESIndexRule.orderTermIndex(productId))
                .mapping(EsOrderTermEntity.class)
                .bulkOps().deleteById(id);
    }

    public ESPage<EsOrderTermEntity> search(SearchEsTermChange searchEsTermChange) {
        ESRestTemplate.ESRestTemplateFactory esRestTemplateFactory = esRestTemplate.Builder()
                .indexName(ESIndexRule.orderTermIndex(searchEsTermChange.getProductId()))
                .mapping(EsOrderTermEntity.class)
                .current(searchEsTermChange.getPage() == null ? 1 : searchEsTermChange.getPage())
                .size(searchEsTermChange.getSize() == null ? 10 : searchEsTermChange.getSize())
                .sortField(searchEsTermChange.getSortField())
                .sortOrder(searchEsTermChange.getSortOrder());
        //设置查询条件,
        this.buildFilterQuery(esRestTemplateFactory, searchEsTermChange);
//        //设置查询条件,
        ESPage<EsOrderTermEntity> esPage = esRestTemplateFactory.bulkOps().searchForPage();
        return esPage;
    }

    /**
     * 首页订单统计查询
     *
     * @param searchEsTermChange
     * @return
     */
    public Map<String, BigDecimal> orderStatSearch(SearchEsTermChange searchEsTermChange) {
        ESRestTemplate.ESRestTemplateFactory esRestTemplateFactory = esRestTemplate.Builder()
                .indexName(ESIndexRule.orderTermIndex(searchEsTermChange.getProductId()))
                .mapping(EsOrderTermEntity.class);

        //设置查询条件,
        this.buildOrderStatFilterQuery(esRestTemplateFactory, searchEsTermChange);
//        //设置查询条件,
        List<EsOrderTermEntity> esPage = esRestTemplateFactory.bulkOps().searchList();

        BigDecimal betAmount = esPage.stream().map(EsOrderTermEntity::getBetAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, BigDecimal> result = new HashMap<>();
        result.put("count", BigDecimal.valueOf(esPage == null ? 0 : esPage.size())); // 防止 esPage 为 null
        result.put("betAmount", betAmount == null ? BigDecimal.ZERO : betAmount); // 防止 betAmount 为 null
        log.info("查询es注单数据--->count:{},betAmount:{}", esPage.size(), betAmount);
        return result;
    }

    public ESPage<EsOrderTermEntity> searchList(SearchEsTermChange searchEsTermChange) {
        ESRestTemplate.ESRestTemplateFactory esRestTemplateFactory = esRestTemplate.Builder()
                .indexName(ESIndexRule.orderTermIndex(searchEsTermChange.getProductId()))
                .mapping(EsOrderTermEntity.class)
                .current(searchEsTermChange.getPage() == null ? 1 : searchEsTermChange.getPage())
                .size(searchEsTermChange.getSize() == null ? 10 : searchEsTermChange.getSize())
                .sortField(searchEsTermChange.getSortField())
                .sortOrder(searchEsTermChange.getSortOrder());

        //设置查询条件,
        this.buildOrderTermListQuery(esRestTemplateFactory, searchEsTermChange);
//        //设置查询条件,
        String string = esRestTemplateFactory.toString();
        log.info("search EsOrderTermEntity, params: {}", string);
        ESPage<EsOrderTermEntity> esPage = esRestTemplateFactory.bulkOps().searchForPage();

        return esPage;

    }

    public String exportEs(SearchEsTermChange searchEsTermChange, String filePrefix) {
        ESRestTemplate.ESRestTemplateFactory esRestTemplateFactory = esRestTemplate.Builder()
                .indexName(ESIndexRule.orderTermIndex(searchEsTermChange.getProductId()))
                .mapping(EsOrderTermEntity.class)
                .sortField(searchEsTermChange.getSortField())
                .sortOrder(searchEsTermChange.getSortOrder());

        //设置查询条件,
        this.buildOrderTermListQuery(esRestTemplateFactory, searchEsTermChange);
//        //设置查询条件,
        String string = esRestTemplateFactory.toString();
        log.info("search EsOrderTermEntity, params: {}", string);
        return esRestTemplateFactory.bulkOps().scrollExport(filePrefix);

    }

    /**
     * count
     *
     * @param searchEsTermChange 查询条件
     * @return
     */
    public long countEs(SearchEsTermChange searchEsTermChange) {
        ESRestTemplate.ESRestTemplateFactory esRestTemplateFactory = esRestTemplate.Builder()
                .indexName(ESIndexRule.orderTermIndex(searchEsTermChange.getProductId()))
                .mapping(EsOrderTermEntity.class)
                .sortField(searchEsTermChange.getSortField())
                .sortOrder(searchEsTermChange.getSortOrder());

        //设置查询条件,
        this.buildOrderTermListQuery(esRestTemplateFactory, searchEsTermChange);
//        //设置查询条件,
        String string = esRestTemplateFactory.toString();
        log.info("search EsOrderTermEntity, params: {}", string);
        return esRestTemplateFactory.bulkOps().count();

    }

    /**
     * 单字段sum
     *
     * @param searchEsTermChange 查询条件
     * @param fieldName          字段名
     * @return
     */
    public BigDecimal sumFieldEs(SearchEsTermChange searchEsTermChange, String fieldName) {
        ESRestTemplate.ESRestTemplateFactory esRestTemplateFactory = esRestTemplate.Builder()
                .indexName(ESIndexRule.orderTermIndex(searchEsTermChange.getProductId()))
                .mapping(EsOrderTermEntity.class)
                .sortField(searchEsTermChange.getSortField())
                .sortOrder(searchEsTermChange.getSortOrder());

        //设置查询条件,
        this.buildOrderTermListQuery(esRestTemplateFactory, searchEsTermChange);
//        //设置查询条件,
        String string = esRestTemplateFactory.toString();
        log.info("search EsOrderTermEntity, params: {}", string);
        return esRestTemplateFactory.bulkOps().sumFieldAggregation(fieldName);

    }

    /**
     * 多字段sum
     *
     * @param searchEsTermChange 查询条件
     * @param fieldName          字段名
     * @return
     */
    public Map<String, BigDecimal> sumFieldsEs(SearchEsTermChange searchEsTermChange, String... fieldName) {
        ESRestTemplate.ESRestTemplateFactory esRestTemplateFactory = esRestTemplate.Builder()
                .indexName(ESIndexRule.orderTermIndex(searchEsTermChange.getProductId()))
                .mapping(EsOrderTermEntity.class)
                .sortField(searchEsTermChange.getSortField())
                .sortOrder(searchEsTermChange.getSortOrder());

        //设置查询条件,
        this.buildOrderTermListQuery(esRestTemplateFactory, searchEsTermChange);

//        //设置查询条件,
        String string = esRestTemplateFactory.toString();
        log.info("search EsOrderTermEntity, params: {}", string);
        return (Map<String, BigDecimal>) esRestTemplateFactory.bulkOps().sumFieldsAggregation(fieldName);

    }

    private void buildOrderTermListQuery(ESRestTemplate.ESRestTemplateFactory esRestTemplateFactory, SearchEsTermChange searchEsTermChange) {

        // 字符串类型动态拼接
        String plateNameZh = searchEsTermChange.getPlateNameZh();
        if (StringUtils.isNotBlank(plateNameZh)) {
            esRestTemplateFactory.filterTermsQuery("plateNameZh", plateNameZh);
        }

        String gameCode = searchEsTermChange.getGameCode();
        if (StringUtils.isNotBlank(gameCode)) {
            esRestTemplateFactory.filterTermsQuery("gameCode", gameCode);
        }

        String gameNameZh = searchEsTermChange.getGameNameZh();
        if (StringUtils.isNotBlank(gameNameZh)) {
            esRestTemplateFactory.filterTermsQuery("gameNameZh", gameNameZh);
        }

        String gameTypeCode = searchEsTermChange.getGameTypeCode();
        if (StringUtils.isNotBlank(gameTypeCode)) {
            esRestTemplateFactory.filterTermsQuery("gameTypeCode", gameTypeCode);
        }

        String orderNo = searchEsTermChange.getOrderNo();
        if (StringUtils.isNotBlank(orderNo)) {
            esRestTemplateFactory.filterTermsQuery("orderNo", orderNo);
        }

        String upOrderNo = searchEsTermChange.getUpOrderNo();
        if (StringUtils.isNotBlank(upOrderNo)) {
            esRestTemplateFactory.filterTermsQuery("upOrderNo", upOrderNo);
        }

        String upPreOrderNo = searchEsTermChange.getUpPreOrderNo();
        if (StringUtils.isNotBlank(upPreOrderNo)) {
            esRestTemplateFactory.filterTermsQuery("upPreOrderNo", upPreOrderNo);
        }

        Long channelId = searchEsTermChange.getChannelId();
        if (channelId != null) {
            esRestTemplateFactory.filterTermsQuery("channelId", channelId);
        }

        Long userId = searchEsTermChange.getUserId();
        if (userId != null) {
            esRestTemplateFactory.filterTermsQuery("userId", userId);
        }

        Long tgUserId = searchEsTermChange.getTgUserId();
        if (tgUserId != null) {
            esRestTemplateFactory.filterTermsQuery("tgUserId", tgUserId);
        }

        Long superUserId = searchEsTermChange.getSuperUserId();
        if (superUserId != null) {
            esRestTemplateFactory.filterTermsQuery("superUserId", superUserId);
        }

        Long superUserTgId = searchEsTermChange.getSuperUserTgId();
        if (superUserTgId != null) {
            esRestTemplateFactory.filterTermsQuery("superUserTgId", superUserTgId);
        }

        Integer currencyId = searchEsTermChange.getCurrencyId();
        if (currencyId != null) {
            esRestTemplateFactory.filterTermsQuery("currencyId", currencyId);
        }

        Integer orderStatus = searchEsTermChange.getOrderStatus();
        if (orderStatus != null) {
            esRestTemplateFactory.filterTermsQuery("orderStatus", orderStatus);
        } else {
            List<Integer> orderStatusList = searchEsTermChange.getOrderStatusList();
            if (CollectionUtil.isNotEmpty(orderStatusList)) {
                esRestTemplateFactory.filterTermsQuery("orderStatus", orderStatusList.toArray());
            }
        }

        Integer settleStatus = searchEsTermChange.getSettleStatus();
        if (settleStatus != null) {
            esRestTemplateFactory.filterTermsQuery("settleStatus", settleStatus);
        }

        List<Integer> orderTypes = searchEsTermChange.getOrderTypes();
        if (orderTypes != null && !orderTypes.isEmpty()) {
            esRestTemplateFactory.filterTermsQuery("orderType", orderTypes.toArray());
        } else {
            Integer orderType = searchEsTermChange.getOrderType();
            if (orderType != null) {
                esRestTemplateFactory.filterTermsQuery("orderType", orderType);
            }
        }

        String[] createTimes = searchEsTermChange.getCreateTimes();
        if (createTimes != null && createTimes.length == 2) {
            Date startTime = DateUtils.parseDate(createTimes[0]);
            Date endTime = DateUtils.parseDate(createTimes[1]);
            esRestTemplateFactory.startTime("createTime", EsDateUtil.getUtcDateStr(startTime)); // startTime, null, 是设置 `createTime` 大于 startTime
            esRestTemplateFactory.endTime("createTime", EsDateUtil.getUtcDateStr(endTime));

        }

        String[] settleTimes = searchEsTermChange.getSettleTimes();
        if (settleTimes != null && settleTimes.length == 2) {
            Date startTime = DateUtils.parseDate(settleTimes[0]);
            Date endTime = DateUtils.parseDate(settleTimes[1]);
            esRestTemplateFactory.startTime("settleTime", EsDateUtil.getUtcDateStr(startTime)); // startTime, null, 是设置 `createTime` 大于 startTime
            esRestTemplateFactory.endTime("settleTime", EsDateUtil.getUtcDateStr(endTime));
        }

        String sortField = searchEsTermChange.getSortField();
        if (sortField != null) {
            esRestTemplateFactory.sortField(sortField);
        }
        SortOrder sortOrder = searchEsTermChange.getSortOrder();
        if (sortOrder != null) {
            esRestTemplateFactory.sortOrder(sortOrder);
        }

    }

    private void buildOrderTermDetailQuery(ESRestTemplate.ESRestTemplateFactory esRestTemplateFactory, SearchEsTermChange searchEsTermChange) {
        String plateCode = searchEsTermChange.getPlateCode();
        if (StringUtils.isNotBlank(plateCode)) {
            esRestTemplateFactory.filterTermsQuery("plateCode", plateCode);
        }
        String gameTypeCode = searchEsTermChange.getGameTypeCode();
        if (StringUtils.isNotBlank(gameTypeCode)) {
            esRestTemplateFactory.filterTermsQuery("gameTypeCode", gameTypeCode);
        }
        Integer currencyId = searchEsTermChange.getCurrencyId();
        if (currencyId != null) {
            esRestTemplateFactory.filterTermsQuery("currencyId", currencyId);
        }
        Long userId = searchEsTermChange.getUserId();
        if (userId != null) {
            esRestTemplateFactory.filterTermsQuery("userId", userId);
        }
        List<Integer> orderTypes = searchEsTermChange.getOrderTypes();
        if (orderTypes != null && !orderTypes.isEmpty()) {
            esRestTemplateFactory.filterTermsQuery("orderType", orderTypes.toArray());
        } else {
            Integer orderType = searchEsTermChange.getOrderType();
            if (orderType != null) {
                esRestTemplateFactory.filterTermsQuery("orderType", orderType);
            }
        }
        String[] createTimes = searchEsTermChange.getCreateTimes();
        if (createTimes != null && createTimes.length == 2) {
            Date startTime = DateUtils.parseDate(createTimes[0]);
            Date endTime = DateUtils.parseDate(createTimes[1]);
            esRestTemplateFactory.startTime("createTime", EsDateUtil.getUtcDateStr(startTime)); // startTime, null, 是设置 `createTime` 大于 startTime
            esRestTemplateFactory.endTime("createTime", EsDateUtil.getUtcDateStr(endTime));

        }

        String[] settleTimes = searchEsTermChange.getSettleTimes();
        if (settleTimes != null && settleTimes.length == 2) {
            Date startTime = DateUtils.parseDate(settleTimes[0]);
            Date endTime = DateUtils.parseDate(settleTimes[1]);
            esRestTemplateFactory.startTime("settleTime", EsDateUtil.getUtcDateStr(startTime)); // startTime, null, 是设置 `createTime` 大于 startTime
            esRestTemplateFactory.endTime("settleTime", EsDateUtil.getUtcDateStr(endTime));
        }

    }

    private void buildFilterQuery(ESRestTemplate.ESRestTemplateFactory esRestTemplateFactory, SearchEsTermChange searchEsTermChange) {
        //前台只展示单一注单,复合注单,从注单可以通过后台查询
        esRestTemplateFactory.filterTermsQuery("orderType", 0, 1);

        Long userId = searchEsTermChange.getUserId();
        if (userId != null) {
            esRestTemplateFactory.filterTermsQuery("userId", userId);
        }


        Date startTime = searchEsTermChange.getStartTime();
        if (startTime != null) {
            // 设置时间大于startTime的条件
            esRestTemplateFactory.startTime("createTime", EsDateUtil.getUtcDateStr(startTime)); // startTime, null, 是设置 `createTime` 大于 startTime
        }

        Date endTime = searchEsTermChange.getEndTime();
        if (endTime != null) {
            esRestTemplateFactory.endTime("createTime", EsDateUtil.getUtcDateStr(endTime)); // null, endTime, 是设置 `createTime` 小于 endTime
        }
        String sortField = searchEsTermChange.getSortField();
        if (sortField != null) {
            esRestTemplateFactory.sortField(sortField);
        }
        SortOrder sortOrder = searchEsTermChange.getSortOrder();
        if (sortOrder != null) {
            esRestTemplateFactory.sortOrder(sortOrder);
        }
        List<Integer> orderTypes = searchEsTermChange.getOrderTypes();
        if (orderTypes != null && !orderTypes.isEmpty()) {
            esRestTemplateFactory.filterTermsQuery("orderType", orderTypes.toArray());
        } else {
            Integer orderType = searchEsTermChange.getOrderType();
            if (orderType != null) {
                esRestTemplateFactory.filterTermsQuery("orderType", orderType);
            }
        }

        List<Integer> gameTypeCodeArr = searchEsTermChange.getGameTypeCodeArr();
        if (gameTypeCodeArr != null && !gameTypeCodeArr.isEmpty()) {
            Integer[] typeArray = gameTypeCodeArr.toArray(new Integer[0]);
            esRestTemplateFactory.filterTermsQuery("gameTypeCode", typeArray);
        }
        Integer orderStatus = searchEsTermChange.getOrderStatus();
        if (orderStatus != null) {
            esRestTemplateFactory.filterTermsQuery("orderStatus", orderStatus);
        }
    }

    private void buildOrderStatFilterQuery(ESRestTemplate.ESRestTemplateFactory esRestTemplateFactory, SearchEsTermChange searchEsTermChange) {
        //查询单一注单,复合注单,结算状态未结算的
        esRestTemplateFactory.filterTermsQuery("orderType", 0, 1);
        esRestTemplateFactory.filterTermsQuery("settleStatus", 0);

        Date startTime = searchEsTermChange.getStartTime();
        if (startTime != null) {
            // 设置时间大于startTime的条件
            esRestTemplateFactory.startTime("createTime", EsDateUtil.getUtcDateStr(startTime)); // startTime, null, 是设置 `createTime` 大于 startTime
        }

        Date endTime = searchEsTermChange.getEndTime();
        if (endTime != null) {
            esRestTemplateFactory.endTime("createTime", EsDateUtil.getUtcDateStr(endTime)); // null, endTime, 是设置 `createTime` 小于 endTime
        }

    }

    public List<EsUserAmountSummary> getDetailedSummary(SearchEsTermChange searchEsTermChange) {
        try {
            ESRestTemplate.ESRestTemplateFactory esRestTemplateFactory = esRestTemplate.Builder()
                    .indexName(ESIndexRule.orderTermIndex(searchEsTermChange.getProductId()))
                    .mapping(EsOrderTermEntity.class);

            // 设置查询条件
            this.buildOrderTermDetailQuery(esRestTemplateFactory, searchEsTermChange);

            // 添加按用户ID分组的聚合操作，包含金额字段
            TermsAggregationBuilder groupByUserId = AggregationBuilders.terms("groupByUserId")
                    .field("userId")
                    .subAggregation(AggregationBuilders.sum("betAmount").field("betAmount"))
                    .subAggregation(AggregationBuilders.sum("winLoss").field("winLoss"))
                    .subAggregation(AggregationBuilders.sum("codeAmount").field("codeAmount"))
                    .subAggregation(AggregationBuilders.sum("win").field("win"))
                    .subAggregation(AggregationBuilders.count("betNum").field("id"))
                    .subAggregation(AggregationBuilders.topHits("userInfo")
                            .fetchSource(new String[]{"plateCode", "gameTypeCode"}, null)
                            .size(1)  // 只取 1 条
                    );
            ESRestTemplate.ESRestTemplateOps esRestTemplateOps = esRestTemplateFactory.bulkOps();
            esRestTemplateOps.addAggregation(groupByUserId);

            log.info("search getDetailedSummary, params: {}", esRestTemplateFactory.toString());

            // 调用已经写好的方法解析聚合结果
            List<EsUserAmountSummary> result = esRestTemplateOps.searchGroupSummary();
            // 增加空值检查
            return result != null ? result : new ArrayList<>();
        } catch (Exception e) {
            log.error("getDetailedSummary error, searchEsTermChange: {}", searchEsTermChange, e);
            return new ArrayList<>();
        }
    }

    /**
     * es分页汇总数据查询
     */
    public ESPage<EsUserAmountSummary> getDetailedSummaryWithAggPage(SearchEsTermChange searchEsTermChange) {
        final int MAX_AGGREGATION_SIZE = 10000;

        try {
            // 统一处理分页参数默认值
            int pageNum = searchEsTermChange.getPage() != null ? searchEsTermChange.getPage() : 1;
            int pageSize = searchEsTermChange.getSize() != null ? searchEsTermChange.getSize() : 10;
            int totalNeeded = pageNum * pageSize;

            if (totalNeeded > MAX_AGGREGATION_SIZE) {
                throw new IllegalArgumentException("分页超出最大聚合桶限制（" + MAX_AGGREGATION_SIZE + "）");
            }

            ESRestTemplate.ESRestTemplateFactory esRestTemplateFactory = esRestTemplate.Builder()
                    .indexName(ESIndexRule.orderTermIndex(searchEsTermChange.getProductId()))
                    .mapping(EsOrderTermEntity.class);

            // 设置查询条件
            this.buildOrderTermDetailQuery(esRestTemplateFactory, searchEsTermChange);

            // 构建聚合查询
            TermsAggregationBuilder groupByUserId = buildUserAggregation(pageNum * pageSize);

            ESRestTemplate.ESRestTemplateOps esRestTemplateOps = esRestTemplateFactory.bulkOps();
            esRestTemplateOps.addAggregation(groupByUserId);

            // 获取聚合结果
            List<EsUserAmountSummary> result = esRestTemplateOps.searchGroupSummary();

            // 获取总数并计算分页信息
            long total = countGroupByUserId(searchEsTermChange);
            int pages = (int) Math.ceil((double) total / pageSize);

            // 分页处理 - 防止越界
            List<EsUserAmountSummary> pageData = new ArrayList<>();
            if (result != null && !result.isEmpty()) {
                int fromIndex = (pageNum - 1) * pageSize;
                if (fromIndex < result.size()) {
                    int toIndex = Math.min(fromIndex + pageSize, result.size());
                    pageData = result.subList(fromIndex, toIndex);
                }
            }

            return ESPage.<EsUserAmountSummary>builder()
                    .current(pageNum)
                    .size(pageSize)
                    .total(total)
                    .pages(pages)
                    .data(pageData)
                    .build();
        } catch (IllegalArgumentException e) {
            log.warn("分页参数超出限制: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("getDetailedSummaryWithAggPage error", e); // 避免打印敏感对象
            return ESPage.<EsUserAmountSummary>builder()
                    .current(searchEsTermChange.getPage() != null ? searchEsTermChange.getPage() : 1)
                    .size(searchEsTermChange.getSize() != null ? searchEsTermChange.getSize() : 10)
                    .total(0)
                    .pages(0)
                    .data(new ArrayList<>())
                    .build();
        }
    }

    // 封装聚合构建逻辑，提高可读性
    private TermsAggregationBuilder buildUserAggregation(int size) {
        return AggregationBuilders.terms("groupByUserId")
                .field("userId")
                .size(size)
                .order(BucketOrder.key(true))
                .subAggregation(AggregationBuilders.sum("betAmount").field("betAmount"))
                .subAggregation(AggregationBuilders.sum("winLoss").field("winLoss"))
                .subAggregation(AggregationBuilders.sum("codeAmount").field("codeAmount"))
                .subAggregation(AggregationBuilders.sum("win").field("win"))
                .subAggregation(AggregationBuilders.count("betNum").field("id"))
                .subAggregation(AggregationBuilders.topHits("userInfo")
                        .fetchSource(new String[]{"plateCode", "gameTypeCode"}, null)
                        .size(1));
    }

    /**
     * 查询按用户ID分组后的总数（即满足条件的不同用户数量）
     *
     * @param searchEsTermChange 查询条件
     * @return 按用户ID分组后的总数
     */
    public long countGroupByUserId(SearchEsTermChange searchEsTermChange) {
        try {
            ESRestTemplate.ESRestTemplateFactory esRestTemplateFactory = esRestTemplate.Builder()
                    .indexName(ESIndexRule.orderTermIndex(searchEsTermChange.getProductId()))
                    .mapping(EsOrderTermEntity.class);

            // 设置查询条件
            this.buildOrderTermDetailQuery(esRestTemplateFactory, searchEsTermChange);

            // 构建 cardinality 聚合来统计不同用户ID的数量
            CardinalityAggregationBuilder groupByUserIdCount = AggregationBuilders
                    .cardinality("groupByUserIdCount")
                    .field("userId")
                    .precisionThreshold(40000L); // 提高精度

            ESRestTemplate.ESRestTemplateOps ops = esRestTemplateFactory.bulkOps();
            ops.addAggregation(groupByUserIdCount);

            NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder()
                    .withQuery(ops.getBoolQueryBuilder());

            builder.addAggregation(groupByUserIdCount);
            SearchHits<EsOrderTermEntity> searchHits = esRestTemplate.getElasticsearchRestTemplate().search(
                    builder.build(),
                    EsOrderTermEntity.class,
                    IndexCoordinates.of(ESIndexRule.orderTermIndex(searchEsTermChange.getProductId()))
            );

            Aggregations aggregations = searchHits.getAggregations();
            if (aggregations == null) {
                return 0L;
            }

            ParsedCardinality cardinality = aggregations.get("groupByUserIdCount");
            return cardinality != null ? cardinality.getValue() : 0L;

        } catch (Exception e) {
            log.error("countGroupByUserId error, searchEsTermChange: {}", searchEsTermChange, e);
            return 0L;
        }
    }


}
