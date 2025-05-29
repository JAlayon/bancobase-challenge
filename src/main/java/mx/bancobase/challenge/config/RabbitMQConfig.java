package mx.bancobase.challenge.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMQConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                log.info("process=sendEvent, status=success, correlationId={}", correlationData != null ? correlationData.getId() : "null");
            } else {
                log.error("process=sendEvent, status=failed, cause={}, correlationId={}",
                        cause, correlationData != null ? correlationData.getId() : "null");
            }
        });
        return template;
    }
}
