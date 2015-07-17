package com.github.bjuvensjo.rsimulator.example.unittest.transfer;

import com.github.bjuvensjo.rsimulator.aop.AopAllianceSimulator;
import com.github.bjuvensjo.rsimulator.aop.AopAllianceSimulatorImpl;
import com.github.bjuvensjo.rsimulator.example.unittest.transfer.account.Account;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;


public class TransferBeanGuiceAopTest {
    private TransferBean transferBean;

    @Before
    public void init() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                AopAllianceSimulator aopAllianceSimulator = new AopAllianceSimulatorImpl();
                aopAllianceSimulator.setRootPath(TransferBeanGuiceAopTest.class);
                bindInterceptor(Matchers.subclassesOf(TransferBeanImpl.class), Matchers.any(), aopAllianceSimulator);
            }
        });
        transferBean = injector.getInstance(TransferBeanImpl.class);
    }

    @Test
    public void testGetAccounts() {
        List<Account> accounts = transferBean.getAccounts("1111");
        assertNotNull(accounts);
    }
}
