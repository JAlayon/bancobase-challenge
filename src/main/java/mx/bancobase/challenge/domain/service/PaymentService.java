package mx.bancobase.challenge.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.bancobase.challenge.domain.dto.CreatePaymentRequestDto;
import mx.bancobase.challenge.domain.entity.Payment;
import mx.bancobase.challenge.domain.enums.PaymentStatus;
import mx.bancobase.challenge.domain.messaging.PaymentEventPublisher;
import mx.bancobase.challenge.domain.repository.PaymentRepository;
import mx.bancobase.challenge.mapper.PaymentMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService{

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentEventPublisher paymentEventPublisher;

    public Payment createPayment(CreatePaymentRequestDto request) {
        log.info("process=createPayment, status=started, request={}", request);
        var paymentEntity = paymentRepository.save((paymentMapper.mapToEntity(request)));
        log.info("process=createPayment, status=completed, request={}", request);
        return paymentEntity;
    }

    public Payment getPaymentDetails(String id) {
        log.info("process=getPaymentStatus, status=started, id={}", id);
        return paymentRepository.findById(id)
                .map(payment -> {
                    log.info("process=getPaymentStatus, status=completed, id={}", id);
                    return payment;
                })
                .orElseThrow(() -> {
                   log.warn("process=getPaymentStatus, status=not_found, id={}", id);
                   return new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found");
                });

    }

    public Payment updatePaymentStatus(String id, PaymentStatus newStatus) {
        log.info("process=updatePaymentStatus, status=started, id={}, newStatus={}", id, newStatus);
        var payment = paymentRepository.findById(id)
                .map(p -> {
                    p.setStatus(newStatus);
                    return paymentRepository.save(p);
                })
                .orElseThrow(() -> {
                    log.warn("process=updatePaymentStatus, status=not_found, id={}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found");
                });
        paymentEventPublisher.publishPaymentStatusChanged(payment);
        log.info("process=updatePaymentStatus, status=completed, id={}, newStatus={}", id, newStatus);

        return payment;
    }
}
