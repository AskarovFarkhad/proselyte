package com.askfar.fakepaymentprovider.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionMessage {

    VALIDATED("The transaction was successfully validated"),
    PROGRESS("Transaction accepted"),
    SUCCESS("Transaction is successfully completed"),
    FAILED("The payment was not accepted due to a server-side error"),
    CURRENCY_NOT_ALLOWED("There is no wallet with this currency"),
    INSUFFICIENT_FUNDS("Insufficient funds to debit the account");

    private final String msg;
}
