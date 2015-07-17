package com.github.bjuvensjo.rsimulator.example.unittest.transfer;

import com.github.bjuvensjo.rsimulator.example.unittest.transfer.account.Account;
import com.github.bjuvensjo.rsimulator.example.unittest.transfer.account.AccountPort;
import com.github.bjuvensjo.rsimulator.example.unittest.transfer.account.GetAccountsRequest;
import com.github.bjuvensjo.rsimulator.example.unittest.transfer.account.GetAccountsResponse;
import com.github.bjuvensjo.rsimulator.example.unittest.transfer.transfer.Transfer;
import com.github.bjuvensjo.rsimulator.example.unittest.transfer.transfer.TransferPort;
import com.github.bjuvensjo.rsimulator.example.unittest.transfer.transfer.TransferRequest;
import com.github.bjuvensjo.rsimulator.example.unittest.transfer.transfer.TransferResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TransferBeanImpl.
 *
 * @author Magnus Bjuvensjö
 */
@Service
public class TransferBeanImpl implements TransferBean {
    @Autowired
    private AccountPort accountService;
    @Autowired
    private TransferPort transferService;

    /**
     * {@inheritDoc}
     */
    public List<Account> getAccounts(String userId) {
        GetAccountsRequest getAccountsRequest = new GetAccountsRequest();
        getAccountsRequest.setUserId(userId);
        GetAccountsResponse getAccountsResponse = accountService.getAccounts(getAccountsRequest);
        return getAccountsResponse.getAccount();
    }

    /**
     * {@inheritDoc}
     */
    public TransferResponse.Receipt transfer(Account from, Account to, double amount) {
        TransferRequest transferRequest = new TransferRequest();
        Transfer transfer = new Transfer();
        transfer.setFrom(from);
        transfer.setTo(to);
        transfer.setAmount(amount);
        transferRequest.setTransfer(transfer);
        TransferResponse transferResponse = transferService.transfer(transferRequest);
        return transferResponse.getReceipt();
    }
}
