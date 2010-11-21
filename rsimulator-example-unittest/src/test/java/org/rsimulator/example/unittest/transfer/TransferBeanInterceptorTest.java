package org.rsimulator.example.unittest.transfer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.rsimulator.http.config.HttpSimulatorConfig.config;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rsimulator.example.unittest.transfer.account.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
// ApplicationContext will be loaded from
// "classpath:/org/rsimulator/example/unittest/transfer/TransferBeanInterceptorTest-context.xml"
@ContextConfiguration
public class TransferBeanInterceptorTest {
    
    @Autowired
    private TransferBean transferBean;

    //@Before
    public void init() {
        try {
            config(getClass());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }    
    
    @Test
    //TODO
    public void testGetAccounts() {
        List<Account> accounts = transferBean.getAccounts("1111");
        assertNotNull(accounts);
    }

    // @Test
    public void testTransfer() {
        fail("Not yet implemented");
    }
}
