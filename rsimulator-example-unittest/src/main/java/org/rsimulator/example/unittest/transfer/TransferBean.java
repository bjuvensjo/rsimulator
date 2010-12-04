package org.rsimulator.example.unittest.transfer;

import java.util.List;

import org.rsimulator.example.unittest.transfer.account.Account;
import org.rsimulator.example.unittest.transfer.transfer.TransferResponse;

public interface TransferBean {

    /**
     * Returns the account for the user with the specified userId.
     * 
     * @param userId user id
     * @return the account for the user with the specified userId
     */
    public abstract List<Account> getAccounts(String userId);

    /**
     * Returns a receipt of the transfer.
     * 
     * @param from the from account
     * @param to the to account
     * @param amount the amount
     * @return a receipt of the transfer
     */
    public abstract TransferResponse.Receipt transfer(Account from, Account to, double amount);

}