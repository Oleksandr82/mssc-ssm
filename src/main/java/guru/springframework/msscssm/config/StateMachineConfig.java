package guru.springframework.msscssm.config;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

    private final Action<PaymentState, PaymentEvent> preAuthAction;
    private final Action<PaymentState, PaymentEvent> authAction;
    private final Action<PaymentState, PaymentEvent> preAuthApprovedAction;
    private final Action<PaymentState, PaymentEvent> preAuthDeclinedAction;
    private final Action<PaymentState, PaymentEvent> authApprovedAction;
    private final Action<PaymentState, PaymentEvent> authDeclinedAction;
    private final Guard<PaymentState, PaymentEvent> paymentIdGuard;

    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {

        states.withStates()
                .initial(PaymentState.NEW)
                .states(EnumSet.allOf(PaymentState.class))
                .end(PaymentState.AUTHORIZED)       // happy end state
                .end(PaymentState.PRE_AUTH_ERROR)   // terminal state
                .end(PaymentState.AUTH_ERROR);      // terminal state
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {

        transitions.withExternal()
                .source(PaymentState.NEW).target(PaymentState.NEW).event(PaymentEvent.PRE_AUTHORIZE).action(preAuthAction).guard(paymentIdGuard)
                .and().withExternal()
                .source(PaymentState.NEW).target(PaymentState.PRE_AUTHORIZED).event(PaymentEvent.PRE_AUTH_APPROVED).action(preAuthApprovedAction)
                .and().withExternal()
                .source(PaymentState.NEW).target(PaymentState.PRE_AUTH_ERROR).event(PaymentEvent.PRE_AUTH_DECLINED).action(preAuthDeclinedAction)
                .and().withExternal()
                .source(PaymentState.PRE_AUTHORIZED).target(PaymentState.PRE_AUTHORIZED).event(PaymentEvent.AUTHORIZE).action(authAction)
                .and().withExternal()
                .source(PaymentState.PRE_AUTHORIZED).target(PaymentState.AUTHORIZED).event(PaymentEvent.AUTH_APPROVED).action(authApprovedAction)
                .and().withExternal()
                .source(PaymentState.PRE_AUTHORIZED).target(PaymentState.AUTH_ERROR).event(PaymentEvent.AUTH_DECLINED).action(authDeclinedAction);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        StateMachineListenerAdapter<PaymentState, PaymentEvent> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                log.info(String.format("stateChanged (from: %s, to: %s)", from, to));
            }
        };

        config.withConfiguration().listener(adapter);
    }

//    private Guard<PaymentState, PaymentEvent> paymentIdGuard() {
//        return context -> context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER) != null;
//    }
//
//    public Action<PaymentState, PaymentEvent> preAuthAction() {
//        return context -> {
//
//            // Business logic to pre authorize a payment
//
//            System.out.println("Business logic to randomly pre authorize a payment");
//            if (new Random().nextInt(10) < 8) {
//                System.out.println("Approved");
//                context.getStateMachine()
//                        .sendEvent(
//                                MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED)
//                                        .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,
//                                                context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
//                                        .build());
//            } else {
//                System.out.println("Declined");
//                context.getStateMachine()
//                        .sendEvent(
//                                MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_DECLINED)
//                                        .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,
//                                                context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
//                                        .build());
//
//            }
//        };
//    }
//
//    public Action<PaymentState, PaymentEvent> authorizeAction() {
//        return context -> {
//
//            // Business logic to authorize a payment
//
//            System.out.println("Business logic to randomly authorize a payment");
//            if (new Random().nextInt(10) < 7) {
//                System.out.println("Authorized");
//                context.getStateMachine()
//                        .sendEvent(
//                                MessageBuilder.withPayload(PaymentEvent.AUTH_APPROVED)
//                                        .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,
//                                                context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
//                                        .build());
//            } else {
//                System.out.println("Authorization declined");
//                context.getStateMachine()
//                        .sendEvent(
//                                MessageBuilder.withPayload(PaymentEvent.AUTH_DECLINED)
//                                        .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,
//                                                context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
//                                        .build());
//
//            }
//        };
//    }
}
