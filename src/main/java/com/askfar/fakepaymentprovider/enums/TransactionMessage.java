package com.askfar.fakepaymentprovider.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionMessage {

    VALIDATED("The transaction was successfully validated"),
    PROGRESS("Transaction accepted"),
    SUCCESS("Transaction is successfully completed"),
    PAYOUT_MIN_AMOUNT("Insufficient funds to complete the operation"),
    CURRENCY_NOT_ALLOWED("There is no wallet with this currency");

    private final String msg;
}
