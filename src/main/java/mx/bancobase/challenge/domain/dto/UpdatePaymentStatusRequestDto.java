package mx.bancobase.challenge.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import mx.bancobase.challenge.domain.enums.PaymentStatus;

@Builder
public record UpdatePaymentStatusRequestDto(
        @NotBlank(message = "id field is required") String id,
        @NotNull(message = "payer field is required") PaymentStatus newStatus){}
