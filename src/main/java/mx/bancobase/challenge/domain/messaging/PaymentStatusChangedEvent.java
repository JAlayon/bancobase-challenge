package mx.bancobase.challenge.domain.messaging;

import lombok.Builder;
import lombok.Data;
import mx.bancobase.challenge.domain.enums.PaymentStatus;

@Data
@Builder
public class PaymentStatusChangedEvent {
    private String id;
    private PaymentStatus status;
    private String concept;
    private String payer;
    private String recipient;
    private String totalAmount;
    private String createdAt;
}
