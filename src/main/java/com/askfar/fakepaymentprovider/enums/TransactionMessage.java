package com.askfar.fakepaymentprovider.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionMessage {

    VALIDATED("The transaction was successfully validated"),
    PROGRESS("Transaction accepted"),
    SUCCESS("Transaction is successfully completed"),
    PAYOUT_MIN_AMOUNT("Insufficient funds to complete the operation");

    private final String msg;
}
