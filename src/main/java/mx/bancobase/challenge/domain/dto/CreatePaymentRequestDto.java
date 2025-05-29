package mx.bancobase.challenge.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreatePaymentRequestDto(
        @NotBlank(message = "concept field is required") String concept,
        @Min(value = 1, message = "product quantity field must be at least 1") int productQuantity,
        @NotBlank(message = "payer field is required") String payer,
        @NotBlank(message = "recipient field is required") String recipient,

        @NotNull(message = "total amount field is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than zero")
        BigDecimal totalAmount
) {}
