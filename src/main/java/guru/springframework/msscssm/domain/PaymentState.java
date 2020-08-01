package guru.springframework.msscssm.domain;

public enum PaymentState {
    NEW,
    PRE_AUTHORIZED,
    PRE_AUTH_ERROR,
    AUTHORIZED,
    AUTH_ERROR
}
