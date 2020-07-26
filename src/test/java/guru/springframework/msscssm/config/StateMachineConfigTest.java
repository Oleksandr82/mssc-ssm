package guru.springframework.msscssm.config;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StateMachineConfigTest {

    @Autowired
    StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    void testNewStateMachineHappyFlow() {

        StateMachine<PaymentState, PaymentEvent> machine = factory.getStateMachine(UUID.randomUUID());

        machine.start();
        assertEquals(PaymentState.NEW, machine.getState().getId());

        machine.sendEvent(PaymentEvent.PRE_AUTHORIZE);
        assertEquals(PaymentState.NEW, machine.getState().getId());

        machine.sendEvent(PaymentEvent.PRE_AUTH_APPROVED);
        assertEquals(PaymentState.PRE_AUTH, machine.getState().getId());
    }

}