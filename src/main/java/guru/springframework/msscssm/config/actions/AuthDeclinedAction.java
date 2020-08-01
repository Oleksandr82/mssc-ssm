package guru.springframework.msscssm.config.actions;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
public class AuthDeclinedAction implements Action<PaymentState, PaymentEvent>  {

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        // Some logic to send a notification for a given action
        System.out.println(">>> Sending Notification for 'AUTHENTICATION DECLINED'");
    }
}
