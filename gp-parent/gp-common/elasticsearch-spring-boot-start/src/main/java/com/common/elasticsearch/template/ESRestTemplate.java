package com.common.elasticsearch.template;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.common.elasticsearch.entity.ESBaseEntity;
import com.common.elasticsearch.entity.ESPage;
import com.common.elasticsearch.entity.EsUserAmountSummary;
import com.gp.common.base.excel.poi.CsvZipExport2Util;
import com.gp.common.base.utils.BeanCopier;
import com.gp.common.base.utils.BeanUtils;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.ParsedTopHits;
import org.elasticsearch.search.aggregations.metrics.ParsedValueCount;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * es工具类
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/13/013 12:26
 */
@Data
@Slf4j
public class ESRestTemplate<T> {

    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    private RestHighLevelClient restHighLevelClient;

    public ESRestTemplateFactory Builder() {
        return new ESRestTemplateFactory<T>(elasticsearchRestTemplate, restHighLevelClient);
    }

    @Data
    public class ESRestTemplateFactory<T> {
        private String indexName;
        private Class<T> var1;
        private String type;
        private String startTimeKey;
        private String startTime;
        private String endTimeKey;
        private String endTime;
        private List<String> ids;
        private String sortField;
        private SortOrder sortOrder;
        private Map<String, Float> weights;
        private int current = 1;
        private int size = 2000;
        private ElasticsearchRestTemplate template;
        private RestHighLevelClient  client;
        private IndexCoordinates indexCoordinates;
        protected BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        public ESRestTemplateFactory(ElasticsearchRestTemplate template, RestHighLevelClient client) {
            this.template = template;
            this.client = client;
        }


        public ESRestTemplateFactory weights(Map<String, Float> fields) {
            this.weights(fields);
            return this;
        }

        public ESRestTemplateFactory indexName(String index) {
            this.indexName = index;
            this.indexCoordinates = IndexCoordinates.of(new String[]{index});
            return this;
        }

        public ESRestTemplateFactory indexName(String index, String index2) {
            this.indexName = index;
            this.indexCoordinates = IndexCoordinates.of(new String[]{index, index2});
            return this;
        }

        public ESRestTemplateFactory indexName(Class<T> clazz) {
            this.indexName = StringUtils.toRootLowerCase(clazz.getSimpleName());
            this.indexCoordinates = IndexCoordinates.of(new String[]{this.indexName});
            return this;
        }

        public ESRestTemplateFactory type(String type) {
            this.type = type;
            return this;
        }

        public ESRestTemplateFactory startTime(String key, String startTime) {
            this.startTimeKey = key;
            this.startTime = startTime;
            boolQueryBuilder.filter(QueryBuilders.rangeQuery(this.startTimeKey).gte(this.startTime));
            return this;
        }

        public ESRestTemplateFactory endTime(String key, String endTime) {
            this.endTimeKey = key;
            this.endTime = endTime;
            boolQueryBuilder.filter(QueryBuilders.rangeQuery(this.endTimeKey).lte(this.endTime));
            return this;
        }

        public ESRestTemplateFactory ids(List<String> ids) {
            this.ids = ids;
            return this;
        }

        public ESRestTemplateFactory sortField(String sortField) {
            this.sortField = sortField;
            return this;
        }

        public ESRestTemplateFactory sortOrder(SortOrder sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        public ESRestTemplateFactory current(int current) {
            this.current = current;
            return this;
        }

        public ESRestTemplateFactory size(int size) {
            this.size = size;
            return this;
        }

        public ESRestTemplateFactory mapping(Class<T> clazz) {
            this.var1 = clazz;
            return this;
        }

        public ESRestTemplateFactory filterMatchQuery(String name, Object value) {
            boolQueryBuilder.filter(QueryBuilders.matchQuery(name, value));
            return this;
        }

        public ESRestTemplateFactory filterQueryStringQuery(String name, String value) {
            boolQueryBuilder.filter(QueryBuilders.queryStringQuery(value).defaultField(name));
            return this;
        }

        public ESRestTemplateFactory filterWildcardQuery(String name, String value) {
            boolQueryBuilder.filter(QueryBuilders.wildcardQuery(name, "*".concat(value).concat("*")));
            return this;
        }

        public ESRestTemplateFactory shouldMultiMatchQuery(Object text, String... fieldNames) {
            boolQueryBuilder.should(QueryBuilders.multiMatchQuery(text, fieldNames));
            return this;
        }

        public ESRestTemplateFactory shouldMatchQuery(String fileName, Object Value) {
            boolQueryBuilder.should(QueryBuilders.matchQuery(fileName, Value));
            return this;
        }

        public ESRestTemplateFactory shouldTermsQuery(String name, String... values) {
            boolQueryBuilder.should(QueryBuilders.termsQuery(name, values));
            return this;
        }

        public ESRestTemplateFactory mustTermsQuery(String name, String... values) {
            boolQueryBuilder.must(QueryBuilders.termsQuery(name, values));
            return this;
        }

        /**
         * 多字段值匹配
         *
         * @param text       值
         * @param fieldNames 字段
         * @return
         */
        public ESRestTemplateFactory multiMustTermsQuery(Object text, String... fieldNames) {
            if (MapUtil.isEmpty(weights)) {
                boolQueryBuilder.must(QueryBuilders.multiMatchQuery(text, fieldNames));
            } else {
                boolQueryBuilder.must(QueryBuilders.multiMatchQuery(text, fieldNames).fields(weights));
            }
            return this;
        }

        /**
         * @return
         */
        public ESRestTemplateFactory mustBool(QueryBuilder boolQueryBuilder1) {
            boolQueryBuilder.must(boolQueryBuilder1);
            return this;
        }

        /**
         * @param name   单字段值匹配
         * @param values
         * @return
         */
        public ESRestTemplateFactory mustMatchQuery(String name, Object values) {
            boolQueryBuilder.must(QueryBuilders.matchQuery(name, values));
            return this;
        }

        public ESRestTemplateFactory filterTermsQuery(String name, Object... values) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery(name, values));
            return this;
        }

        public ESRestTemplateFactory filterMultiMatchQuery(String name, String... values) {
            boolQueryBuilder.filter(QueryBuilders.multiMatchQuery(name, values));
            return this;
        }

        public ESRestTemplateFactory mustQuery(BoolQueryBuilder queryBuilder) {
            boolQueryBuilder.must(queryBuilder);
            return this;
        }

        public ESRestTemplateOps bulkOps() {
            return new ESRestTemplateOps(this);
        }

    }

    @Data
    public class ESRestTemplateOps {
        private String indexName;
        private Class<T> var1;
        private String type;
        private String startTimeKey;
        private String startTime;
        private String endTimeKey;
        private String endTime;
        private List<String> ids;
        private String sortField;
        private SortOrder sortOrder;
        private int current;
        private int size;
        private ElasticsearchRestTemplate template;
        private RestHighLevelClient  client;
        private IndexCoordinates indexCoordinates;
        private BoolQueryBuilder boolQueryBuilder;
        private List<AbstractAggregationBuilder> aggregations = new ArrayList<>();

        public void addAggregation(AbstractAggregationBuilder aggregation) {
            this.aggregations.add(aggregation);
        }
        public List<EsUserAmountSummary> searchGroupSummary() {
            checkAssertPage();
            this.exist(this.indexName, this.var1);

            NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder()
                    .withQuery(boolQueryBuilder);

            for (AbstractAggregationBuilder aggregation : aggregations) {
                builder.addAggregation(aggregation);
            }

            SearchHits<T> searchHits = template.search(builder.build(), this.var1, this.indexCoordinates);
            Aggregations aggregations = searchHits.getAggregations();

            // 添加空值检查
            if (aggregations == null) {
                return new ArrayList<>();
            }

            List<EsUserAmountSummary> results = new ArrayList<>();
            ParsedLongTerms userIdAgg = aggregations.get("groupByUserId");

            // 添加空值检查
            if (userIdAgg == null) {
                return results;
            }

            for (Terms.Bucket bucket : userIdAgg.getBuckets()) {
                Long userId = (Long) bucket.getKey();
                ParsedSum betAmount = bucket.getAggregations().get("betAmount");
                ParsedSum winLoss = bucket.getAggregations().get("winLoss");
                ParsedSum codeAmount = bucket.getAggregations().get("codeAmount");
                ParsedValueCount betNum = bucket.getAggregations().get("betNum");
                ParsedSum win = bucket.getAggregations().get("win");
                ParsedTopHits userInfo = bucket.getAggregations().get("userInfo");
                String plateCode = "";
                String gameTypeCode = "";
                if (userInfo != null && userInfo.getHits() != null && userInfo.getHits().getHits().length > 0) {
                    org.elasticsearch.search.SearchHit hit = userInfo.getHits().getAt(0);
                    if (hit != null && hit.getSourceAsMap() != null) {
                        Object plateCodeObj = hit.getSourceAsMap().get("plateCode");
                        Object gameTypeCodeObj = hit.getSourceAsMap().get("gameTypeCode");
                        if (plateCodeObj instanceof String) {
                            plateCode = (String) plateCodeObj;
                        }
                        if (gameTypeCodeObj instanceof String) {
                            gameTypeCode = (String) gameTypeCodeObj;
                        }
                    }
                }
                // 添加空值检查并提供默认值
                BigDecimal betAmountValue = (betAmount != null) ? BigDecimal.valueOf(betAmount.getValue()) : BigDecimal.ZERO;
                BigDecimal winLossValue = (winLoss != null) ? BigDecimal.valueOf(winLoss.getValue()) : BigDecimal.ZERO;
                BigDecimal codeAmountValue = (codeAmount != null) ? BigDecimal.valueOf(codeAmount.getValue()) : BigDecimal.ZERO;
                long betNumValue = (betNum != null) ? betNum.getValue() : 0L;
                BigDecimal winValue = (win != null) ? BigDecimal.valueOf(win.getValue()) : BigDecimal.ZERO;

                results.add(new EsUserAmountSummary(
                        userId,
                        betAmountValue,
                        winLossValue,
                        (int) betNumValue,
                        winValue,
                        codeAmountValue,
                        plateCode,
                        gameTypeCode

                ));
            }
            return results;
        }

        public ESRestTemplateOps() {
        }

        public ESRestTemplateOps(ESRestTemplateFactory esRestTemplateFactory) {
            BeanUtils.copyPropertiesIgnoreNull(esRestTemplateFactory, this);
        }

        private void exist(String indexName, Class<?> var1) {
            IndexOperations indexOps = elasticsearchRestTemplate.indexOps(IndexCoordinates.of(new String[]{indexName}));
            if (indexOps.exists()) {
                return;
            }
            try {
                Document document = Document.create();
                document.putIfAbsent("index.number_of_shards", 2);
                document.putIfAbsent("index.number_of_replicas", 1);
//                document.putIfAbsent("refresh_interval", "5s");
                indexOps.create(document);
                indexOps.putMapping(indexOps.createMapping(var1));
            } catch (Exception e) {
                // ⭐ 关键：忽略第一次并发创建造成的异常
                if (e.getMessage() != null &&
                        e.getMessage().contains("resource_already_exists_exception")) {
                    return; // 说明别人成功创建了，我不用重复创建
                }
                throw e;
            }
        }

        /**
         * 生成全局版本号
         * @param stageType 阶段类型（1=下注，2=结算，3=取消）
         * @return long 版本号
         */
        private long generateVersion(int stageType) {
            // 阶段前缀放在高位，保证结算 > 下注 > 取消
            long stagePrefix = stageType * 1_0000_0000_0000_0000L; // 16位高位
            return stagePrefix + System.currentTimeMillis();
        }

//        public void save(T data) {
//            FunctionUtil.counterTime(() -> {
//                if (StringUtils.isNotEmpty(this.indexName)) {
//                    this.exist(this.indexName, this.var1);
//                    template.save(data, indexCoordinates);
//                } else {
//                    template.save(data);
//                }
//            }, "EsRestTemplate save");
//        }

        @SneakyThrows
        public void save(ESBaseEntity item) {
            this.exist(this.indexName, this.var1);
//            IndexQuery indexQuery = new IndexQueryBuilder().withId(item.getEsId().toString()).withObject(item).build();
//            template.index(indexQuery, this.indexCoordinates);
            ElasticsearchConverter elasticsearchConverter = template.getElasticsearchConverter();
            Document doc = Document.create();
            elasticsearchConverter.write(item, doc);
            Long version;
            if (item.getVersion() == null) {
                version = System.currentTimeMillis();
            }else {
                version = item.getVersion();
            }
            IndexRequest req = new IndexRequest(this.indexName)
                    .id(String.valueOf(item.getEsId()))       // @Id 不会自动带上，自己设置
                    .source(doc.toJson(), XContentType.JSON)
                    .setRefreshPolicy(WriteRequest.RefreshPolicy.NONE)
                    .version(version)                   // 设置版本号
                    .versionType(VersionType.EXTERNAL);
            client.index(req, RequestOptions.DEFAULT);
        }

//        public void saveAll(List<ESBaseEntity> data) {
//            if (CollectionUtils.isEmpty(data)) {
//                return;
//            }
//            FunctionUtil.counterTime(() -> {
//                if (StringUtils.isNotEmpty(this.indexName)) {
//                    this.exist(this.indexName, this.var1);
//                    template.save(data, indexCoordinates);
//                } else {
//                    template.save(data);
//                }
//            }, String.format("EsRestTemplate saveAll|size: %s", data.size()));
        ////            template.indexOps(this.indexCoordinates).refresh();
//        }

        @SneakyThrows
        public void saveAll(List<ESBaseEntity> data) {
            this.exist(this.indexName, this.var1);
            ElasticsearchConverter elasticsearchConverter = template.getElasticsearchConverter();
            BulkRequest br = new BulkRequest().setRefreshPolicy(WriteRequest.RefreshPolicy.NONE); // 不立即刷新
            for (ESBaseEntity item : data) {
                Document doc = Document.create();
                elasticsearchConverter.write(item, doc);                         // 应用 @Field 的 type/format/pattern 等注解
                Long version;
                if (item.getVersion() == null) {
                    version = System.currentTimeMillis();
                }else {
                    version = item.getVersion();
                }
                IndexRequest ir = new IndexRequest(indexName)
                        .id(String.valueOf(item.getEsId()))
                        .source(doc.toJson(), XContentType.JSON)
                        .setRefreshPolicy(WriteRequest.RefreshPolicy.NONE)
                        .version(version)                   // 设置版本号
                        .versionType(VersionType.EXTERNAL);
                br.add(ir);
            }
            if (br.numberOfActions() > 0) {
                client.bulk(br, RequestOptions.DEFAULT);
//            template.indexOps(this.indexCoordinates).refresh();
            }

        }


//        public void batchUpdate(List<ESBaseEntity> data) {
//            this.exist(this.indexName, this.var1);
//            List<UpdateQuery> queries = new ArrayList<>();
//            UpdateQuery updateQuery;
//            int counter = 0;
//            for (ESBaseEntity item : data) {
//                Document document = Document.from(BeanUtils.beanToMap(item));
//                document.setId(item.getEsId().toString());
//                updateQuery = UpdateQuery.builder(item.getEsId().toString())
//                        .withDocAsUpsert(true).withDocument(document).build();
//                queries.add(updateQuery);
//                //分批提交索引
//                if (counter != 0 && counter % 1000 == 0) {
//                    template.bulkUpdate(queries, this.indexCoordinates);
//                    queries.clear();
//                    System.out.println("bulkIndex counter : " + counter);
//                }
//                counter++;
//            }
//            //不足批的索引最后不要忘记提交
//            if (queries.size() > 0) {
//                template.bulkUpdate(queries, this.indexCoordinates);
//            }
//            template.indexOps(this.indexCoordinates).refresh();
//        }

        @SneakyThrows
        public void batchUpdate(List<ESBaseEntity> data) {
            this.exist(this.indexName, this.var1);
            ElasticsearchConverter elasticsearchConverter = template.getElasticsearchConverter();
            BulkRequest br = new BulkRequest().setRefreshPolicy(WriteRequest.RefreshPolicy.NONE); // 不立即刷新
            for (ESBaseEntity item : data) {
                Document doc = Document.create();
                elasticsearchConverter.write(item, doc);                         // 应用 @Field 的 type/format/pattern 等注解
                Long version;
                if (item.getVersion() == null) {
                    version = System.currentTimeMillis();
                }else {
                    version = item.getVersion();
                }
                IndexRequest ir = new IndexRequest(indexName)
                        .id(String.valueOf(item.getEsId()))
                        .source(doc.toJson(), XContentType.JSON)
                        .setRefreshPolicy(WriteRequest.RefreshPolicy.NONE)
                        .version(version)                   // 设置版本号
                        .versionType(VersionType.EXTERNAL);
                br.add(ir);
            }
            if (br.numberOfActions() > 0) {
                client.bulk(br, RequestOptions.DEFAULT);
//            template.indexOps(this.indexCoordinates).refresh();
            }
//            template.indexOps(this.indexCoordinates).refresh();
        }

//        public void update(ESBaseEntity item) {
//            this.exist(this.indexName, this.var1);
//            Document document = Document.from(BeanUtils.beanToMap(item));
//            document.setId(item.getEsId().toString());
//            UpdateQuery updateQuery = UpdateQuery.builder(item.getEsId().toString())
//                    .withDocAsUpsert(true).withDocument(document).build();
//            template.update(updateQuery, this.indexCoordinates);
//            template.indexOps(this.indexCoordinates).refresh();
//        }

        @SneakyThrows
        public void update(ESBaseEntity item) {
            this.exist(this.indexName, this.var1);
//            IndexQuery indexQuery = new IndexQueryBuilder().withId(item.getEsId().toString()).withObject(item).build();
//            template.index(indexQuery, this.indexCoordinates);
            ElasticsearchConverter elasticsearchConverter = template.getElasticsearchConverter();
            Document doc = Document.create();
            elasticsearchConverter.write(item, doc);
            Long version;
            if (item.getVersion() == null) {
                version = System.currentTimeMillis();
            }else {
                version = item.getVersion();
            }
            IndexRequest req = new IndexRequest(this.indexName)
                    .id(String.valueOf(item.getEsId()))       // @Id 不会自动带上，自己设置
                    .source(doc.toJson(), XContentType.JSON)
                    .setRefreshPolicy(WriteRequest.RefreshPolicy.NONE)
                    .version(version)                   // 设置版本号
                    .versionType(VersionType.EXTERNAL);
            client.index(req, RequestOptions.DEFAULT);
//            template.indexOps(this.indexCoordinates).refresh();
        }

        public void deleteIndex() {
            IndexOperations indexOps = template.indexOps(this.indexCoordinates);
            if (indexOps.exists()) {
                indexOps.delete();
            }
        }

        public void delete(ESBaseEntity item) {
            this.exist(this.indexName, this.var1);
            template.delete(item.getEsId().toString(), this.indexCoordinates);
//            template.indexOps(this.indexCoordinates).refresh();
        }

        public void deleteByIds() {
            this.exist(this.indexName, this.var1);
//            for (String id : this.ids) {
//                template.delete(id, this.indexCoordinates);
//            }
            // 创建批量删除的查询
            Query query = new CriteriaQuery(new Criteria("id").in(this.ids));
            // 执行批量删除操作
            elasticsearchRestTemplate.delete(query, IndexCoordinates.of(indexName));
//            template.indexOps(this.indexCoordinates).refresh();
        }

        public void deleteById(String id) {
            this.exist(this.indexName, this.var1);
            template.delete(id, this.indexCoordinates);
//            template.indexOps(this.indexCoordinates).refresh();
        }

        public void deleteQuery(String name, String value) {
            this.exist(this.indexName, this.var1);
            NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.termQuery(name, value)).build();
            template.delete(nativeSearchQuery, this.var1, this.indexCoordinates);
//            template.indexOps(this.indexCoordinates).refresh();
        }

        /**
         * 删除所有
         */
        public void deleteAll() {
            this.exist(this.indexName, this.var1);
            NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.existsQuery("_id")).build();
            template.delete(nativeSearchQuery, this.var1, this.indexCoordinates);
//            template.indexOps(this.indexCoordinates).refresh();
        }

        public List<T> searchListByIds() {
            IdsQueryBuilder idsQueryBuilder = QueryBuilders.idsQuery();

            idsQueryBuilder.addIds(Convert.toStrArray(ids));
            NativeSearchQuery query = new NativeSearchQueryBuilder()
                    .withQuery(idsQueryBuilder)
                    .build();
            query.setMaxResults(10000);
            SearchHits searchHits = template.search(query, var1, this.indexCoordinates);
            List<SearchHit> searchHit = searchHits.getSearchHits();
            List<Object> res = searchHit.stream().map(v -> v.getContent()).collect(Collectors.toList());
            return BeanCopier.copyList(res, var1);
        }

        public List<T> searchList() {
            checkAssert();

//            IndicesOptions indicesOptions = IndicesOptions.fromOptions(true, true, true, false);
            FieldSortBuilder order = null;
            if (StringUtils.isNotEmpty(this.sortField)) {
                SortOrder sortRule = null;
                if (this.sortOrder == null) {
                    sortRule = SortOrder.ASC;
                } else {
                    sortRule = this.sortOrder;
                }
                order = SortBuilders.fieldSort(sortField).order(sortRule);
            }
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
            if (order != null) {
                nativeSearchQueryBuilder = nativeSearchQueryBuilder.withSort(order);
            }
            NativeSearchQuery query = nativeSearchQueryBuilder.withIds(this.ids)
                    .withFilter(boolQueryBuilder)
                    .withIndicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN)
                    .build();
            SearchHits searchHits = template.search(query, this.var1, this.indexCoordinates);
            List<SearchHit> searchHit = searchHits.getSearchHits();
            List<Object> res = searchHit.stream().map(v -> v.getContent()).collect(Collectors.toList());
            return BeanCopier.copyList(res, var1);
        }

        public ESPage<T> searchForPage() {
            checkAssertPage();
            this.exist(this.indexName, this.var1);

            FieldSortBuilder order = null;
            if (StringUtils.isNotEmpty(this.sortField)) {
                SortOrder sortRule = null;
                if (this.sortOrder == null) {
                    sortRule = SortOrder.DESC;
                } else {
                    sortRule = this.sortOrder;
                }
                order = SortBuilders.fieldSort(sortField).order(sortRule);
            }
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
//           boolQueryBuilder.minimumShouldMatch(1);
            if (order != null) {
                nativeSearchQueryBuilder = nativeSearchQueryBuilder.withSort(order);
            }
            Query query = nativeSearchQueryBuilder
                    .withQuery(boolQueryBuilder)
                    .withPageable(PageRequest.of(current - 1, size))
                    .withTrackScores(true)
                    .build();
            // 关键：build 后设置 trackTotalHits
            query.setTrackTotalHits(true);
            SearchHits searchHits = template.search(query, this.var1, this.indexCoordinates);
            List<SearchHit> searchHit = searchHits.getSearchHits();
            List<Object> res = searchHit.stream().map(v -> v.getContent()).collect(Collectors.toList());
            return ESPage.<T>builder()
                    .current(this.current)
                    .size(this.size)
                    .total(searchHits.getTotalHits())
                    .pages((int) Math.ceil((double) searchHits.getTotalHits() / this.size))
                    .data(BeanCopier.copyList(res, var1)).build();
        }

        public String scrollExport(String filePrefix) {
            checkAssertPage();
            this.exist(this.indexName, this.var1);

            FieldSortBuilder order = null;
            if (StringUtils.isNotEmpty(this.sortField)) {
                SortOrder sortRule = null;
                if (this.sortOrder == null) {
                    sortRule = SortOrder.DESC;
                } else {
                    sortRule = this.sortOrder;
                }
                order = SortBuilders.fieldSort(sortField).order(sortRule);
            }

            // 构建查询
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                    .withQuery(boolQueryBuilder)
                    .withPageable(PageRequest.of(0, 10000)) // scroll 模式不要用 current
                    .withTrackScores(true);

            if (order != null) {
                nativeSearchQueryBuilder.withSort(order);
            }

            Query query = nativeSearchQueryBuilder.build();

            long scrollTimeoutMillis = 60000L; // 1分钟
            String scrollId;
            CsvZipExport2Util<T> exporter = new CsvZipExport2Util<>(this.var1, filePrefix);
            SearchScrollHits<T> scrollHits = template.searchScrollStart(
                    scrollTimeoutMillis,
                    query,
                    this.var1,
                    indexCoordinates
            );
            scrollId = scrollHits.getScrollId();
            int partIndex = 1;
            while (scrollHits.hasSearchHits()) {
                List<T> page = scrollHits.getSearchHits()
                        .stream().map(hit -> (T) hit.getContent()).collect(Collectors.toList());
                exporter.appendCsvPart(page, partIndex++);
//                resultList.addAll(page);

                scrollHits = template.searchScrollContinue(scrollId, scrollTimeoutMillis, this.var1, indexCoordinates);
                scrollId = scrollHits.getScrollId();
            }
            template.searchScrollClear(Collections.singletonList(scrollId));
            return exporter.finalizeZip();

        }

        /**
         * count统计
         *
         * @return
         */
        public long count() {
            checkAssert(); // 复用现有检查逻辑
            this.exist(this.indexName, this.var1);
            NativeSearchQuery query = new NativeSearchQueryBuilder()
                    .withQuery(boolQueryBuilder)
                    .build();
            return template.count(query, var1, indexCoordinates);
        }

        /**
         * sum求和
         *
         * @param field 字段名
         * @return
         */
        public BigDecimal sumFieldAggregation(String field) {
            checkAssert();
            this.exist(this.indexName, this.var1);
            // 构建聚合
            SumAggregationBuilder aggregation = AggregationBuilders.sum("sum_" + field).field(field);
            NativeSearchQuery query = new NativeSearchQueryBuilder()
                    .withQuery(boolQueryBuilder)
                    .addAggregation(aggregation)
                    .build();

            SearchHits<T> searchHits = template.search(query, var1, indexCoordinates);
            // 提取聚合结果
            Aggregations aggregations = searchHits.getAggregations();
            ParsedSum parsedSum = null;
            if (aggregations != null) {
                parsedSum = aggregations.get("sum_" + field);
            }
            return parsedSum != null ? BigDecimal.valueOf(parsedSum.getValue()) : BigDecimal.ZERO;
        }

        /**
         * sum求和 多字段
         *
         * @param fields 字段名
         * @return
         */
        public Map<String, BigDecimal> sumFieldsAggregation(String... fields) {
            checkAssert();
            this.exist(this.indexName, this.var1);

            // 创建聚合构建器
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                    .withQuery(boolQueryBuilder);

            // 为每个字段添加求和聚合
            for (String field : fields) {
                String aggName = "sum_" + field;
                queryBuilder.addAggregation(
                        AggregationBuilders.sum(aggName).field(field)
                );
            }

            // 执行查询
            SearchHits<T> searchHits = template.search(
                    queryBuilder.build(),
                    var1,
                    indexCoordinates
            );

            // 提取所有聚合结果
            Map<String, BigDecimal> resultMap = new HashMap<>();
            Aggregations aggregations = searchHits.getAggregations();

            if (aggregations != null) {
                for (String field : fields) {
                    String aggName = "sum_" + field;
                    ParsedSum parsedSum = aggregations.get(aggName);
                    double value = (parsedSum != null) ? parsedSum.getValue() : 0.0;
                    resultMap.put(field, BigDecimal.valueOf(value));
                }
            } else {
                // 无聚合结果时填充默认值
                for (String field : fields) {
                    resultMap.put(field, BigDecimal.ZERO);
                }
            }

            return resultMap;
        }

        private void checkAssert() {
            if (StringUtils.isEmpty(this.indexName))
                Assert.isFalse(true, "indexName must be not null");
            if (ObjectUtils.isEmpty(var1))
                Assert.isFalse(true, "mapping must be not null");
        }

        private void checkAssertPage() {
            if (CollectionUtils.isNotEmpty(this.ids))
                Assert.isFalse(true, "ids must be null, if for ids, please use searchListByIds() method");
            if (StringUtils.isEmpty(this.indexName))
                Assert.isFalse(true, "indexName must be not null");
            if (ObjectUtils.isEmpty(var1))
                Assert.isFalse(true, "mapping must be not null");
            if (0 == current)
                Assert.isFalse(true, "current must large 0");
            if (0 == size)
                Assert.isFalse(true, "size must large 0");
        }
    }

}
