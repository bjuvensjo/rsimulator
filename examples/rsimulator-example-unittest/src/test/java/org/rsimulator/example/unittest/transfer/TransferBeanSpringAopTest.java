package org.rsimulator.example.unittest.transfer;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rsimulator.aop.AopAllianceSimulator;
import org.rsimulator.example.unittest.transfer.account.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
