package mx.bancobase.challenge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.bancobase.challenge.domain.dto.CreatePaymentRequestDto;
import mx.bancobase.challenge.domain.dto.PaymentResponseDto;
import mx.bancobase.challenge.domain.dto.UpdatePaymentStatusRequestDto;
import mx.bancobase.challenge.domain.service.PaymentService;
import mx.bancobase.challenge.mapper.PaymentMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payments", description = "API for managing payments")
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentService paymentService, PaymentMapper paymentMapper) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    @Operation(
            summary = "Create a new payment",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payment created",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PaymentResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
            }
    )
    @PostMapping
    public PaymentResponseDto createPayment(@Valid @RequestBody CreatePaymentRequestDto request) {
        return paymentMapper.mapToResponse(paymentService.createPayment(request));
    }

    @Operation(
            summary = "Get payment details by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payment found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PaymentResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public PaymentResponseDto getPaymentDetails(@PathVariable String id) {
        return paymentMapper.mapToResponse(paymentService.getPaymentDetails(id));
    }

    @Operation(
            summary = "Update payment status",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payment status updated",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PaymentResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid status", content = @Content)
            }
    )
    @PatchMapping("/status")
    public PaymentResponseDto updatePaymentStatus(@Valid @RequestBody UpdatePaymentStatusRequestDto request) {
        return paymentMapper.mapToResponse(paymentService.updatePaymentStatus(request.id(), request.newStatus()));
    }
}
