package com.common.elasticsearch.config;

import com.common.elasticsearch.service.EsAmountChangeService;
import com.common.elasticsearch.service.EsOrderTermService;
import com.common.elasticsearch.service.EsUserExtChangeService;
import com.common.elasticsearch.template.ESRestTemplate;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.io.Serializable;

/**
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 19:33
 */
@Configuration
public class EsConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Bean
    public ESRestTemplate eSRestTemplate(ElasticsearchRestTemplate elasticsearchRestTemplate, RestHighLevelClient restHighLevelClient) {
        ESRestTemplate esRestTemplate = new ESRestTemplate();
        esRestTemplate.setElasticsearchRestTemplate(elasticsearchRestTemplate);
        esRestTemplate.setRestHighLevelClient(restHighLevelClient);
        return esRestTemplate;
    }

    @Bean
    public EsAmountChangeService esAmountChangeService(ESRestTemplate esRestTemplate) {
        EsAmountChangeService esAmountChangeService = new EsAmountChangeService();
        esAmountChangeService.setEsRestTemplate(esRestTemplate);
        return esAmountChangeService;
    }



    @Bean
    public EsOrderTermService esOrderTermService(ESRestTemplate esRestTemplate) {
        EsOrderTermService esOrderTermService = new EsOrderTermService();
        esOrderTermService.setEsRestTemplate(esRestTemplate);
        return esOrderTermService;
    }

    @Bean
    public EsUserExtChangeService esUserExtChangeService(ESRestTemplate esRestTemplate) {
        EsUserExtChangeService esUserExtChangeService = new EsUserExtChangeService();
        esUserExtChangeService.setEsRestTemplate(esRestTemplate);
        return esUserExtChangeService;
    }
}



