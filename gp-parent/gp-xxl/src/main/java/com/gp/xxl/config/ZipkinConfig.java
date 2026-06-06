package com.gp.xxl.config;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultSaslConfig;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.reporter.Sender;
import zipkin2.reporter.amqp.RabbitMQSender;

import javax.net.ssl.SSLContext;

/**
 * @author Natsu
 * @date 2019/10/17
 */
@Configuration
@EnableConfigurationProperties(RabbitProperties.class)
public class ZipkinConfig {

    @SneakyThrows
    @Bean("zipkinSender")
    public Sender rabbitSender(RabbitProperties rabbitProperties) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(rabbitProperties.getHost());
        connectionFactory.setPort(rabbitProperties.getPort());
        connectionFactory.setPassword(rabbitProperties.getPassword());
        connectionFactory.setUsername(rabbitProperties.getUsername());
        // ✅ 启用 SSL (TLSv1.2)
        if (rabbitProperties.getSsl().getEnabled()) {
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            connectionFactory.useSslProtocol(sslContext);
        }

        // ✅ 强制使用 PLAIN 认证（AWS MQ 仅支持 PLAIN）
        connectionFactory.setSaslConfig(DefaultSaslConfig.PLAIN);
        return RabbitMQSender.newBuilder()
                .connectionFactory(connectionFactory)
                .queue("zipkin")
                .addresses(rabbitProperties.getHost()+":"+rabbitProperties.getPort())
                .build();
    }

}
