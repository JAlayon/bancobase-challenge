package mx.bancobase.challenge.controller;


import mx.bancobase.challenge.domain.dto.CreatePaymentRequestDto;
import mx.bancobase.challenge.domain.dto.PaymentResponseDto;
import mx.bancobase.challenge.domain.dto.UpdatePaymentStatusRequestDto;
import mx.bancobase.challenge.domain.entity.Payment;
import mx.bancobase.challenge.domain.enums.PaymentStatus;
import mx.bancobase.challenge.domain.service.PaymentService;
import mx.bancobase.challenge.mapper.PaymentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentController controller;


    @Test
    void createPayment_shouldReturnMappedResponse() {
        CreatePaymentRequestDto request = CreatePaymentRequestDto.builder()
                .totalAmount(BigDecimal.TEN)
                .productQuantity(1)
                .payer("payer")
                .recipient("recipient")
                .concept("concept")
                .build();
        Payment mockPayment = new Payment();
        PaymentResponseDto mockResponse = PaymentResponseDto.builder()
                .id("id")
                .concept(request.concept())
                .payer(request.payer())
                .recipient(request.recipient())
                .totalAmount(request.totalAmount())
                .productQuantity(request.productQuantity())
                .status(PaymentStatus.PENDING)
                .build();

        when(paymentService.createPayment(request)).thenReturn(mockPayment);
        when(paymentMapper.mapToResponse(mockPayment)).thenReturn(mockResponse);

        PaymentResponseDto result = controller.createPayment(request);

        assertEquals(mockResponse, result);
        verify(paymentService).createPayment(request);
        verify(paymentMapper).mapToResponse(mockPayment);
    }

    @Test
    void getPaymentDetails_shouldReturnMappedResponse() {
        String paymentId = "abc123";
        Payment mockPayment = new Payment();
        PaymentResponseDto mockResponse = PaymentResponseDto.builder()
                .id(paymentId)
                .status(PaymentStatus.COMPLETED)
                .build();

        when(paymentService.getPaymentDetails(paymentId)).thenReturn(mockPayment);
        when(paymentMapper.mapToResponse(mockPayment)).thenReturn(mockResponse);

        PaymentResponseDto result = controller.getPaymentDetails(paymentId);

        assertEquals(mockResponse, result);
        verify(paymentService).getPaymentDetails(paymentId);
        verify(paymentMapper).mapToResponse(mockPayment);
    }

    @Test
    void updatePaymentStatus_shouldReturnMappedResponse() {
        UpdatePaymentStatusRequestDto request = UpdatePaymentStatusRequestDto.builder()
                .id("payment123")
                .newStatus(PaymentStatus.COMPLETED)
                .build();
        Payment mockPayment = new Payment();
        PaymentResponseDto mockResponse = PaymentResponseDto.builder()
                .id(request.id())
                .concept("concept")
                .payer("payer")
                .createdAt(LocalDateTime.now().minusDays(1))
                .status(PaymentStatus.COMPLETED)
                .build();

        when(paymentService.updatePaymentStatus(request.id(), request.newStatus())).thenReturn(mockPayment);
        when(paymentMapper.mapToResponse(mockPayment)).thenReturn(mockResponse);

        PaymentResponseDto result = controller.updatePaymentStatus(request);

        assertEquals(mockResponse, result);
        verify(paymentService).updatePaymentStatus(request.id(), request.newStatus());
        verify(paymentMapper).mapToResponse(mockPayment);
    }
}
