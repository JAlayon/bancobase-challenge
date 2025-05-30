package mx.bancobase.challenge.domain.service;

import mx.bancobase.challenge.domain.dto.CreatePaymentRequestDto;
import mx.bancobase.challenge.domain.entity.Payment;
import mx.bancobase.challenge.domain.enums.PaymentStatus;
import mx.bancobase.challenge.domain.messaging.PaymentEventPublisher;
import mx.bancobase.challenge.domain.repository.PaymentRepository;
import mx.bancobase.challenge.mapper.PaymentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentEventPublisher paymentEventPublisher;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createPayment_shouldSaveAndReturnPayment() {
        var request = buildCreatePaymentRequest();
        var mappedPayment = buildPaymentFromRequest(request);

        when(paymentMapper.mapToEntity(request)).thenReturn(mappedPayment);
        when(paymentRepository.save(mappedPayment)).thenReturn(mappedPayment);

        var paymentEntity = paymentService.createPayment(request);

        assertThat(paymentEntity).isEqualTo(mappedPayment);
        verify(paymentRepository).save(mappedPayment);
    }

    @Test
    void getPaymentDetails_shouldReturnPayment_whenExists() {
        var payment = buildPayment();
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));

        var result = paymentService.getPaymentDetails(payment.getId());
        assertThat(result).isEqualTo(payment);
    }

    @Test
    void getPaymentDetails_shouldThrow_whenNotFound() {
        when(paymentRepository.findById("not_found")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> paymentService.getPaymentDetails("not_found"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Payment not found");
    }

    @Test
    void updatePaymentStatus_shouldUpdateStatusAndPublishEvent() {
        var originalPayment = buildPayment();

        when(paymentRepository.findById(originalPayment.getId())).thenReturn(Optional.of(originalPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        var result = paymentService.updatePaymentStatus(originalPayment.getId(), PaymentStatus.COMPLETED);

        verify(paymentRepository).save(paymentCaptor.capture());
        verify(paymentEventPublisher).publishPaymentStatusChanged(paymentCaptor.getValue());

        Payment savedPayment = paymentCaptor.getValue();
        assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(savedPayment.getId()).isEqualTo(originalPayment.getId());
        assertThat(savedPayment.getConcept()).isEqualTo(originalPayment.getConcept());
        assertThat(savedPayment.getPayer()).isEqualTo(originalPayment.getPayer());
        assertThat(savedPayment.getRecipient()).isEqualTo(originalPayment.getRecipient());
        assertThat(savedPayment.getUpdatedAt()).isAfterOrEqualTo(originalPayment.getUpdatedAt());
        assertThat(result).isEqualTo(savedPayment);
    }

    @Test
    void updatePaymentStatus_shouldThrow_whenNotFound() {
        when(paymentRepository.findById("not_found")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> paymentService.updatePaymentStatus("not_found", PaymentStatus.COMPLETED))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Payment not found");
    }

    private CreatePaymentRequestDto buildCreatePaymentRequest() {
        return CreatePaymentRequestDto.builder()
                .concept("Loan payment")
                .productQuantity(1)
                .payer("JohnDoe123")
                .recipient("JaneDoe456")
                .totalAmount(new BigDecimal("1500.00"))
                .build();
    }

    private Payment buildPaymentFromRequest(CreatePaymentRequestDto request) {
        return Payment.builder()
                .id("abc123")
                .concept(request.concept())
                .productQuantity(request.productQuantity())
                .payer(request.payer())
                .recipient(request.recipient())
                .totalAmount(request.totalAmount())
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Payment buildPayment() {
        return Payment.builder()
                .id("abc123")
                .concept("Loan payment")
                .productQuantity(1)
                .payer("JohnDoe123")
                .recipient("JaneDoe456")
                .totalAmount(new BigDecimal("1500.00"))
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusHours(12))
                .build();
    }

}
