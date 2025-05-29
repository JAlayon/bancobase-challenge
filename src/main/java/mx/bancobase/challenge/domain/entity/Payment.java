package mx.bancobase.challenge.domain.entity;

import lombok.*;
import mx.bancobase.challenge.domain.enums.PaymentStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Payment {

    @Id
    private String id;

    private String concept;

    private int productQuantity;

    private String payer;

    private String recipient;

    private BigDecimal totalAmount;

    private PaymentStatus status = PaymentStatus.PENDING;

    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();

}
