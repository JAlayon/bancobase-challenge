package mx.bancobase.challenge.mapper;

import org.mapstruct.Mapper;


import mx.bancobase.challenge.domain.dto.CreatePaymentRequestDto;
import mx.bancobase.challenge.domain.dto.PaymentResponseDto;
import mx.bancobase.challenge.domain.entity.Payment;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    Payment mapToEntity(CreatePaymentRequestDto request);

    PaymentResponseDto mapToResponse(Payment entity);
}
