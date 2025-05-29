package mx.bancobase.challenge.domain.dto;

import lombok.Builder;
import mx.bancobase.challenge.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record PaymentResponseDto(
        String id,
        String concept,
        int productQuantity,
        String payer,
        String recipient,
        BigDecimal totalAmount,
        PaymentStatus status,
        LocalDateTime createdAt
) {}
