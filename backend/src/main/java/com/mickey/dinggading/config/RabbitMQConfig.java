package com.mickey.dinggading.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.audio-analysis-request-queue.name}")
    private String audioAnalysisRequestQueueName;

    @Value("${rabbitmq.audio-analysis-response-queue.name}")
    private String audioAnalysisResponseQueueName;

    @Value("${rabbitmq.audio-analysis-request-exchange.name}")
    private String audioAnalysisRequestExchangeName;

    @Value("${rabbitmq.audio-analysis-response-exchange.name}")
    private String audioAnalysisResponseExchangeName;

    @Value("${rabbitmq.audio-analysis-request.routing.key}")
    private String audioAnalysisRequestRoutingKey;

    @Value("${rabbitmq.audio-analysis-response.routing.key}")
    private String audioAnalysisResponseRoutingKey;

    // 요청 큐 생성
    @Bean
    public Queue audioAnalysisRequestQueue() {
        return QueueBuilder.durable(audioAnalysisRequestQueueName)
                .withArgument("x-message-ttl", 60000) // 메시지 TTL: 60초
                .withArgument("x-max-length", 1000)   // 최대 1000개 메시지 저장
                .build();
    }

    // 응답 큐 생성
    @Bean
    public Queue audioAnalysisResponseQueue() {
        return QueueBuilder.durable(audioAnalysisResponseQueueName)
                .withArgument("x-message-ttl", 300000) // 메시지 TTL: 5분
                .withArgument("x-max-length", 1000)    // 최대 1000개 메시지 저장
                .build();
    }

    // 요청 교환기(Exchange) 생성
    @Bean
    public DirectExchange audioAnalysisRequestExchange() {
        return new DirectExchange(audioAnalysisRequestExchangeName);
    }

    // 응답 교환기(Exchange) 생성
    @Bean
    public DirectExchange audioAnalysisResponseExchange() {
        return new DirectExchange(audioAnalysisResponseExchangeName);
    }

    // 요청 큐와 교환기 바인딩
    @Bean
    public Binding audioAnalysisRequestBinding() {
        return BindingBuilder
                .bind(audioAnalysisRequestQueue())
                .to(audioAnalysisRequestExchange())
                .with(audioAnalysisRequestRoutingKey);
    }

    // 응답 큐와 교환기 바인딩
    @Bean
    public Binding audioAnalysisResponseBinding() {
        return BindingBuilder
                .bind(audioAnalysisResponseQueue())
                .to(audioAnalysisResponseExchange())
                .with(audioAnalysisResponseRoutingKey);
    }

    // JSON 메시지 변환기
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate 설정
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}