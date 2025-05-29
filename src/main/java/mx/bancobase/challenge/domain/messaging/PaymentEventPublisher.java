package mx.bancobase.challenge.domain.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.bancobase.challenge.domain.entity.Payment;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    public void publishPaymentStatusChanged(Payment payment) {
        log.info("process=publishPaymentStatusChanged, status=started, id={}", payment.getId());
        var event = mapToPaymentStatusChangedEvent(payment);
        var correlationData = new CorrelationData(event.getId());
        rabbitTemplate.convertAndSend(exchange, routingKey, event, correlationData);
    }

    private PaymentStatusChangedEvent mapToPaymentStatusChangedEvent(Payment payment) {
        return PaymentStatusChangedEvent.builder()
                .id(payment.getId())
                .concept(payment.getConcept())
                .status(payment.getStatus())
                .payer(payment.getPayer())
                .recipient(payment.getRecipient())
                .totalAmount(payment.getTotalAmount().toPlainString())
                .createdAt(payment.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
    }
}
