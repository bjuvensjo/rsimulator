package com.github.bjuvensjo.rsimulator.example.unittest.transfer;

import com.github.bjuvensjo.rsimulator.example.unittest.transfer.account.Account;
import com.github.bjuvensjo.rsimulator.example.unittest.transfer.transfer.TransferResponse;

import java.util.List;

/**
 * TransferBean.
 *
 * @author Magnus Bjuvensjö
 */
public interface TransferBean {

    /**
     * Returns the account for the user with the specified userId.
     *
     * @param userId user id
     * @return the account for the user with the specified userId
     */
    List<Account> getAccounts(String userId);

    /**
     * Returns a receipt of the transfer.
     *
     * @param from   the from account
     * @param to     the to account
     * @param amount the amount
     * @return a receipt of the transfer
     */
    TransferResponse.Receipt transfer(Account from, Account to, double amount);

}