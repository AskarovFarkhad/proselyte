package com.askfar.fakepaymentprovider.service;

import com.askfar.fakepaymentprovider.AbstractTest;
import com.askfar.fakepaymentprovider.enums.CurrencyEnum;
import com.askfar.fakepaymentprovider.model.Transaction;
import com.askfar.fakepaymentprovider.model.Wallet;
import com.askfar.fakepaymentprovider.model.WebhookHistory;
import com.askfar.fakepaymentprovider.repository.TransactionRepository;
import com.askfar.fakepaymentprovider.repository.WalletRepository;
import com.askfar.fakepaymentprovider.repository.WebhookHistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

import static com.askfar.fakepaymentprovider.enums.TransactionStatus.IN_PROGRESS;
import static com.askfar.fakepaymentprovider.enums.TransactionType.PAY_OUT;
import static com.askfar.fakepaymentprovider.enums.TransactionType.TOP_UP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class TransactionProcessingJobImplTest extends AbstractTest {

    @Autowired
    private TransactionService transactionService;

    @SpyBean
    private WalletRepository walletRepository;

    @SpyBean
    private TransactionRepository transactionRepository;

    @SpyBean
    private WebhookService webhookService;

    @SpyBean
    private WebhookHistoryRepository webhookHistoryRepository;

    /**
     * Тест-кейс: 2 транзакции TOP_UP в статусе IN_PROGRESS, платежи идут по одному кошельку, оба успешны, итоговая сумма 475 USD.
     */
    @Test
    void executeProcessingTopUpTransaction() throws ExecutionException, InterruptedException {
        transactionService.processingTopUpTransaction().toFuture().get();

        verify(transactionRepository, times(1)).findTransactionByTransactionTypeAndStatus(TOP_UP, IN_PROGRESS);
        verify(walletRepository, times(2)).findByMerchantIdAndCurrency(eq("PROSELYTE"), eq("USD"));
        verify(walletRepository, times(2)).updateWalletByWalletId(eq(1L), any(BigDecimal.class));
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(webhookService, times(2)).notificationService(any(Transaction.class));
        verify(webhookHistoryRepository, times(2)).save(any(WebhookHistory.class));

        Wallet wallet = walletRepository.findByMerchantIdAndCurrency("PROSELYTE", CurrencyEnum.USD.name()).toFuture().get();

        assertThat(wallet).isNotNull();
        assertThat(wallet.getBalance()).isEqualByComparingTo(new BigDecimal("475"));
    }

    /**
     * Тест-кейс: 4 транзакции PAY_OUT в статусе IN_PROGRESS:
     * 2 транзакции по одному кошельку USD (один успешен, второй нет из-за недостатка средств),
     * 1 транзакция на не существующий кошелек JPY - статус неуспешный,
     * 1 транзакция пройдет успешно EUR
     */
    @Test
    void executeProcessingPayOutTransaction() throws ExecutionException, InterruptedException {
        transactionService.processingPayOutTransaction().toFuture().get();

        verify(transactionRepository, times(1)).findTransactionByTransactionTypeAndStatus(PAY_OUT, IN_PROGRESS);
        verify(walletRepository, times(4)).findByMerchantIdAndCurrency(any(), any());
        verify(walletRepository, times(2)).updateWalletByWalletId(any(), any(BigDecimal.class));
        verify(transactionRepository, times(4)).save(any(Transaction.class));
        verify(webhookService, times(4)).notificationService(any(Transaction.class));
        verify(webhookHistoryRepository, times(4)).save(any(WebhookHistory.class));

        Wallet walletUSD = walletRepository.findByMerchantIdAndCurrency("merchant2", CurrencyEnum.USD.name()).toFuture().get();
        Wallet walletEUR = walletRepository.findByMerchantIdAndCurrency("PROSELYTE", CurrencyEnum.EUR.name()).toFuture().get();

        assertThat(walletUSD).isNotNull();
        assertThat(walletUSD.getBalance()).isEqualByComparingTo(new BigDecimal("0.00"));

        assertThat(walletEUR).isNotNull();
        assertThat(walletEUR.getBalance()).isEqualByComparingTo(new BigDecimal("50.00"));
    }
}