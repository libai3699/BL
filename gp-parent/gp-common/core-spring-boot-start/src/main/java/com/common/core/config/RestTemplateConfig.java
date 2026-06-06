package com.common.core.config;

import com.common.core.prop.RestTemplateProp;
import com.common.core.util.RestTemplateUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;


/**
 * @author admin
 * restTempLate配置
 */
@Configuration
@EnableConfigurationProperties(RestTemplateProp.class)
@Slf4j
public class RestTemplateConfig {

    @SneakyThrows
    @Bean
    public RestTemplate restTemplate(RestTemplateProp restTemplateProp) {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        if (restTemplateProp.getProxyEnable()) {
            log.info("enable Proxy.. properties={}", restTemplateProp);
            TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
            HttpClientBuilder httpClientBuilder = HttpClients.custom();
            httpClientBuilder.setSSLSocketFactory(connectionSocketFactory);
            httpClientBuilder.setProxy(new HttpHost(restTemplateProp.getProxyUri(), restTemplateProp.getProxyPort(), "http"));
//            CloseableHttpClient httpClient = httpClientBuilder.build();
//            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
//                .register("http", PlainConnectionSocketFactory.getSocketFactory())
//                .register("https", SSLConnectionSocketFactory.getSocketFactory())
//                .build();
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
            connectionManager.setDefaultSocketConfig(SocketConfig.custom().setTcpNoDelay(true).build());
            connectionManager.setDefaultMaxPerRoute(50);
            connectionManager.setMaxTotal(200); // 总的最大连接数
//
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(10000) //服务器返回数据(response)的时间，超过该时间抛出read timeout
                    .setConnectTimeout(5000)//连接上服务器(握手成功)的时间，超出该时间抛出connect timeout
                    .setConnectionRequestTimeout(1000)//从连接池中获取连接的超时时间，超过该时间未拿到可用连接，会抛出org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
                    .build();
            CloseableHttpClient httpClient = HttpClientBuilder.create()
                    .setConnectionManager(connectionManager)
                    .setDefaultRequestConfig(requestConfig).build();
            httpRequestFactory.setHttpClient(httpClient);
        }
        httpRequestFactory.setConnectionRequestTimeout(restTemplateProp.getRequestTimeout());
        httpRequestFactory.setConnectTimeout(restTemplateProp.getCTimeout());
        httpRequestFactory.setReadTimeout(restTemplateProp.getReadTimeout());
        log.info("ConnectionRequestTimeout:{},ConnectTimeout:{},ReadTimeout:{}", restTemplateProp.getRequestTimeout(), restTemplateProp.getCTimeout(), restTemplateProp.getReadTimeout());
        return new RestTemplate(httpRequestFactory);
    }

    @Bean
    public RestTemplateUtil restTemplateUtil(RestTemplate restTemplate, RestTemplateProp restTemplateProp) {
        return new RestTemplateUtil<T>(restTemplate);
    }

}
