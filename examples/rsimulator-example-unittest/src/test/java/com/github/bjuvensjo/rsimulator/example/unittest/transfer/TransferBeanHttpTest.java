package com.github.bjuvensjo.rsimulator.example.unittest.transfer;

import com.github.bjuvensjo.rsimulator.example.unittest.transfer.account.Account;
import com.github.bjuvensjo.rsimulator.example.unittest.transfer.transfer.TransferResponse.Receipt;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import static com.github.bjuvensjo.rsimulator.http.config.HttpSimulatorConfig.config;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"beans.xml", "TransferBeanHttpTest-context.xml"})
public class TransferBeanHttpTest {

    @Autowired
    private TransferBean transferBean;

    @Before
    public void init() {
        try {
            config(getClass());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Ignore
    @Test
    public void testGetAccounts1() {
        List<Account> accounts = transferBean.getAccounts("1111");
        assertNotNull(accounts);
        assertEquals(3, accounts.size());
    }

    @Ignore
    @Test
    public void testGetAccounts2() {
        List<Account> accounts = transferBean.getAccounts("2221");
        assertNotNull(accounts);
        assertEquals(1, accounts.size());
        assertEquals(1, transferBean.getAccounts("2222").size());
    }

    @Ignore
    @Test
    public void testTransfer1() {
        Account from = new Account();
        from.setAccountId("1");
        from.setBalance(100);
        Account to = new Account();
        to.setAccountId("2");
        to.setBalance(0);
        double amount = 100;
        Receipt receipt = transferBean.transfer(from, to, amount);
        assertEquals("OK", receipt.getStatus());
        assertEquals("1", receipt.getFrom().getAccountId());
        assertEquals(0, receipt.getFrom().getBalance(), 0);
        assertEquals("2", receipt.getTo().getAccountId());
        assertEquals(100, receipt.getTo().getBalance(), 0);
    }

    @Ignore
    @Test
    public void testTransfer2() {
        Account from = new Account();
        from.setAccountId("11");
        from.setBalance(1000);
        Account to = new Account();
        to.setAccountId("22");
        to.setBalance(1000);
        double amount = 500;
        Receipt receipt = transferBean.transfer(from, to, amount);
        assertEquals("OK", receipt.getStatus());
        assertEquals("11", receipt.getFrom().getAccountId());
        assertEquals(500, receipt.getFrom().getBalance(), 0);
        assertEquals("22", receipt.getTo().getAccountId());
        assertEquals(1500, receipt.getTo().getBalance(), 0);
    }
}
