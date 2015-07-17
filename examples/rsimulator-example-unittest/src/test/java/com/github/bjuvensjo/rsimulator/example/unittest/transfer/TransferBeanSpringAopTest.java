package com.github.bjuvensjo.rsimulator.example.unittest.transfer;

import com.github.bjuvensjo.rsimulator.aop.AopAllianceSimulator;
import com.github.bjuvensjo.rsimulator.example.unittest.transfer.account.Account;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TransferBeanSpringAopTest {
    @Autowired
    private AopAllianceSimulator aopAllianceSimulator;
    @Autowired
    private TransferBean transferBean;

    @Before
    public void init() {
        aopAllianceSimulator.setRootPath(this.getClass());
        aopAllianceSimulator.setUseRootRelativePath(false);
    }

    @Test
    public void testGetAccounts() {
        List<Account> accounts = transferBean.getAccounts("1111");
        assertNotNull(accounts);
    }
}
