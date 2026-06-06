package com.common.elasticsearch.service;

import com.common.elasticsearch.entity.EsUserExtChange;
import com.common.elasticsearch.rule.ESIndexRule;
import com.common.elasticsearch.template.ESRestTemplate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Data
@Slf4j
public class EsUserExtChangeService implements Serializable {

    private static final long serialVersionUID = 1L;

    private ESRestTemplate esRestTemplate;

    public void save(EsUserExtChange esUserExtChange, String productId) {
        esUserExtChange.setEsId(esUserExtChange.getId());
        esUserExtChange.setVersion(esUserExtChange.getUpdateVersion());
        esRestTemplate.Builder()
                .indexName(ESIndexRule.extChangeIndex(productId))
                .mapping(EsUserExtChange.class)
                .bulkOps().save(esUserExtChange);
    }

    public void saveAll(List<EsUserExtChange> esUserExtChanges, String productId) {
        for (EsUserExtChange esUserExtChange : esUserExtChanges) {
            esUserExtChange.setEsId(esUserExtChange.getId());
            esUserExtChange.setVersion(esUserExtChange.getUpdateVersion());
        }
        esRestTemplate.Builder()
                .indexName(ESIndexRule.extChangeIndex(productId))
                .mapping(EsUserExtChange.class)
                .bulkOps().saveAll(esUserExtChanges);
    }

    public void updateById(EsUserExtChange esUserExtChange, String productId) {
        esUserExtChange.setEsId(esUserExtChange.getId());
        esUserExtChange.setVersion(esUserExtChange.getUpdateVersion());
        esRestTemplate.Builder()
                .indexName(ESIndexRule.extChangeIndex(productId))
                .mapping(EsUserExtChange.class)
                .bulkOps().update(esUserExtChange);
    }

    public void updateAll(List<EsUserExtChange> esUserExtChanges, String productId) {
        for (EsUserExtChange esUserExtChange : esUserExtChanges) {
            esUserExtChange.setEsId(esUserExtChange.getId());
            esUserExtChange.setVersion(esUserExtChange.getUpdateVersion());
        }
        esRestTemplate.Builder()
                .indexName(ESIndexRule.extChangeIndex(productId))
                .mapping(EsUserExtChange.class)
                .bulkOps().batchUpdate(esUserExtChanges);
    }


    public void delById(String id, String productId) {
        esRestTemplate.Builder()
                .indexName(ESIndexRule.extChangeIndex(productId))
                .mapping(EsUserExtChange.class)
                .bulkOps().deleteById(id);
    }

    public void delByIds(List<String> ids, String productId) {
        esRestTemplate.Builder()
                .indexName(ESIndexRule.extChangeIndex(productId))
                .mapping(EsUserExtChange.class)
                .ids(ids)
                .bulkOps().deleteByIds();
    }

}
