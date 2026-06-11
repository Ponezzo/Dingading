package com.mickey.dinggading.infra.rabbitmq.producer;

import com.mickey.dinggading.infra.rabbitmq.dto.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JsonMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.audio-analysis-request-exchange.name}")
    private String audioAnalysisRequestExchangeName;

    @Value("${rabbitmq.audio-analysis-request.routing.key}")
    private String audioAnalysisRequestRoutingKey;

    public JsonMessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendJsonMessage(MessageDTO messageDto) {
        log.info("Sending JSON message: {}", messageDto);
        rabbitTemplate.convertAndSend(audioAnalysisRequestExchangeName, audioAnalysisRequestRoutingKey, messageDto);
    }
}