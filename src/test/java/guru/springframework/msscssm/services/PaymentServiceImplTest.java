package guru.springframework.msscssm.services;

import guru.springframework.msscssm.domain.Payment;
import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import guru.springframework.msscssm.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder().amount(BigDecimal.valueOf(12.99)).build();
    }

    @Transactional
    @Test
    @RepeatedTest(10)
    void preAuthorize() {
        Payment savedPayment = paymentService.newPayment(payment);
        assertEquals(PaymentState.NEW, savedPayment.getState());

        StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuthorize(savedPayment.getId());

        Payment preAuthorizedPayment = paymentRepository.getOne(savedPayment.getId());
        assertEquals(PaymentState.PRE_AUTHORIZED, preAuthorizedPayment.getState());

        System.out.println(sm);
        System.out.println(preAuthorizedPayment);
    }

    @Transactional
    @Test
    @RepeatedTest(10)
    void authorize() {
        Payment savedPayment = paymentService.newPayment(payment);
        assertEquals(PaymentState.NEW, savedPayment.getState());

        paymentService.preAuthorize(savedPayment.getId());
        Payment preAuthorizedPayment = paymentRepository.getOne(savedPayment.getId());
        assertEquals(PaymentState.PRE_AUTHORIZED, preAuthorizedPayment.getState());

        assertEquals(PaymentState.PRE_AUTHORIZED, preAuthorizedPayment.getState());

        paymentService.authorizePayment(savedPayment.getId());
        Payment authorizedPayment = paymentRepository.getOne(savedPayment.getId());
        assertTrue(authorizedPayment.getState() == PaymentState.AUTH_ERROR ||
                authorizedPayment.getState() == PaymentState.AUTHORIZED);
    }
}