package com.common.elasticsearch.service;

import com.common.elasticsearch.entity.ESPage;
import com.common.elasticsearch.entity.EsAmountChange;
import com.common.elasticsearch.rule.ESIndexRule;
import com.common.elasticsearch.search.SearchEsAmountChange;
import com.common.elasticsearch.template.ESRestTemplate;
import com.common.elasticsearch.util.EsDateUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.search.sort.SortOrder;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author axing
 * @version 1.0
 * @date 2023/10/16/016 15:43
 */
@Data
@Slf4j
public class EsAmountChangeService implements Serializable {

    private static final long serialVersionUID = 1L;

    private ESRestTemplate esRestTemplate;

    public ESPage<EsAmountChange> search(EsAmountChange esAmountChange, String productId, int page, int size, String sortField, SortOrder sortOrder) {
        esAmountChange.setEsId(esAmountChange.getId());
        ESRestTemplate.ESRestTemplateFactory esRestTemplateFactory = esRestTemplate.Builder()
                .indexName(ESIndexRule.amountChangeIndex(productId))
                .mapping(EsAmountChange.class)
                .current(page)
                .size(size)
                .sortField(sortField)
                .sortOrder(sortOrder);
        //设置查询条件,
        esRestTemplateFactory.filterTermsQuery("name", "张三"); //全匹配,相当于 ==
        esRestTemplateFactory.filterMatchQuery("name", "张三"); //模糊匹配, 相当于like
        ESPage<EsAmountChange> esPage = esRestTemplateFactory.bulkOps().searchForPage();
        return esPage;
    }

    public ESPage<EsAmountChange> search(SearchEsAmountChange searchEsAmountChange)  {
        ESRestTemplate.ESRestTemplateFactory esRestTemplateFactory = esRestTemplate.Builder()
                .indexName(ESIndexRule.amountChangeIndex(searchEsAmountChange.getProductId()))
                .mapping(EsAmountChange.class)
                .current(searchEsAmountChange.getPage()==null?1:searchEsAmountChange.getPage())
                .size(searchEsAmountChange.getSize()==null?10:searchEsAmountChange.getSize());
        //设置查询条件,
          this.buildFilterQuery(esRestTemplateFactory, searchEsAmountChange);
        ESPage<EsAmountChange> esPage = esRestTemplateFactory.bulkOps().searchForPage();
        return esPage;
    }

    private void buildFilterQuery(ESRestTemplate.ESRestTemplateFactory esRestTemplateFactory, SearchEsAmountChange searchEsAmountChange) {
        Long userId = searchEsAmountChange.getUserId();
        if (userId != null) {
            esRestTemplateFactory.filterTermsQuery("userId", userId);
        }
        Date startTime = searchEsAmountChange.getStartTime();
        if (startTime != null) {
            // 设置时间大于startTime的条件
            esRestTemplateFactory.startTime("createTime", EsDateUtil.getUtcDateStr(startTime)); // startTime, null, 是设置 `createTime` 大于 startTime
        }

        Date endTime = searchEsAmountChange.getEndTime();
        if (endTime != null) {
            esRestTemplateFactory.endTime("createTime",  EsDateUtil.getUtcDateStr(endTime)); // null, endTime, 是设置 `createTime` 小于 endTime
        }
        String sortField = searchEsAmountChange.getSortField();
        if (sortField != null) {
            esRestTemplateFactory.sortField(sortField);
        }
        SortOrder sortOrder = searchEsAmountChange.getSortOrder();
        if (sortOrder != null) {
            esRestTemplateFactory.sortOrder(sortOrder);
        }
        List<Integer> typeArr = searchEsAmountChange.getTypeArr();
        if (typeArr != null && !typeArr.isEmpty()) {
            Integer[] typeArray = typeArr.toArray(new Integer[0]);
            esRestTemplateFactory.filterTermsQuery("type", typeArray);
        }

    }

    public void save(EsAmountChange esAmountChange, String productId) {
        esAmountChange.setEsId(esAmountChange.getId());
        esAmountChange.setVersion(esAmountChange.getUpdateVersion());
        esRestTemplate.Builder()
                .indexName(ESIndexRule.amountChangeIndex(productId))
                .mapping(EsAmountChange.class)
                .bulkOps().save(esAmountChange);
    }

    public void saveAll(List<EsAmountChange> esAmountChanges, String productId) {
        for (EsAmountChange esAmountChange : esAmountChanges) {
            esAmountChange.setEsId(esAmountChange.getId());
            esAmountChange.setVersion(esAmountChange.getUpdateVersion());
        }
        esRestTemplate.Builder()
                .indexName(ESIndexRule.amountChangeIndex(productId))
                .mapping(EsAmountChange.class)
                .bulkOps().saveAll(esAmountChanges);
    }

    public void delById(String id, String productId) {
        esRestTemplate.Builder()
                .indexName(ESIndexRule.amountChangeIndex(productId))
                .mapping(EsAmountChange.class)
                .bulkOps().deleteById(id);
    }

    public void delByIds(List<String> ids, String productId) {
        esRestTemplate.Builder()
                .indexName(ESIndexRule.amountChangeIndex(productId))
                .mapping(EsAmountChange.class)
                .ids(ids)
                .bulkOps().deleteByIds();
    }

}
