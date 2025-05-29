package mx.bancobase.challenge.messaging;

import mx.bancobase.challenge.domain.entity.Payment;
import mx.bancobase.challenge.domain.enums.PaymentStatus;
import mx.bancobase.challenge.domain.messaging.PaymentEventPublisher;
import mx.bancobase.challenge.domain.messaging.PaymentStatusChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentEventPublisherTest {

    private RabbitTemplate rabbitTemplate;
    private PaymentEventPublisher publisher;

    private final String exchange = "test-exchange";
    private final String routingKey = "test-routing";

    @BeforeEach
    void setUp() {
        rabbitTemplate = mock(RabbitTemplate.class);

        // We create a concrete instance and inject field values using reflection (or use @TestConfiguration)
        publisher = new PaymentEventPublisher(rabbitTemplate);
        setField(publisher, "exchange", exchange);
        setField(publisher, "routingKey", routingKey);
    }

    @Test
    void publishPaymentStatusChanged_shouldSendEventWithCorrectData() {
        Payment payment = new Payment();
        payment.setId("payment123");
        payment.setConcept("concept");
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPayer("payer");
        payment.setRecipient("recipient");
        payment.setTotalAmount(new BigDecimal("1000.00"));
        payment.setCreatedAt(LocalDateTime.of(2025, 5, 29,12,12));

        publisher.publishPaymentStatusChanged(payment);

        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<CorrelationData> correlationCaptor = ArgumentCaptor.forClass(CorrelationData.class);

        verify(rabbitTemplate).convertAndSend(eq(exchange), eq(routingKey), messageCaptor.capture(), correlationCaptor.capture());

        PaymentStatusChangedEvent event = (PaymentStatusChangedEvent) messageCaptor.getValue();
        assertEquals("payment123", event.getId());
        assertEquals("concept", event.getConcept());
        assertEquals(PaymentStatus.COMPLETED, event.getStatus());
        assertEquals("payer", event.getPayer());
        assertEquals("recipient", event.getRecipient());
        assertEquals("1000.00", event.getTotalAmount());
        assertEquals("2025-05-29T12:12:00", event.getCreatedAt());

        assertEquals("payment123", correlationCaptor.getValue().getId());
    }

    private void setField(Object target, String field, Object value) {
        try {
            var f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
