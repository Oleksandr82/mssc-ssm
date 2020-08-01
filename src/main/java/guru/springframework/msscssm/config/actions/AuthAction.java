package guru.springframework.msscssm.config.actions;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import guru.springframework.msscssm.services.PaymentServiceImpl;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AuthAction implements Action<PaymentState, PaymentEvent>  {

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {

        // Business logic to authorize a payment

        System.out.println("Business logic to randomly authorize a payment");
        if (new Random().nextInt(10) < 7) {
            System.out.println("Authorized");
            context.getStateMachine()
                    .sendEvent(
                            MessageBuilder.withPayload(PaymentEvent.AUTH_APPROVED)
                                    .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,
                                            context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                                    .build());
        } else {
            System.out.println("Authorization declined");
            context.getStateMachine()
                    .sendEvent(
                            MessageBuilder.withPayload(PaymentEvent.AUTH_DECLINED)
                                    .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,
                                            context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                                    .build());

        }

    }
}
