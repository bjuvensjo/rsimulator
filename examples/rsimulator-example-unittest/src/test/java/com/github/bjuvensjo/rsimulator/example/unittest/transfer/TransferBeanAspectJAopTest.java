package com.github.bjuvensjo.rsimulator.example.unittest.transfer;

import com.github.bjuvensjo.rsimulator.aop.AspectJSimulatorAdapter;
import com.github.bjuvensjo.rsimulator.aop.AspectJSimulatorAdapterImpl;
import com.github.bjuvensjo.rsimulator.example.unittest.transfer.account.Account;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class TransferBeanAspectJAopTest {
    private TransferBean transferBean;

    @SuppressWarnings("unused")
    @Aspect
    private static class SimulatorAspect {
        private AspectJSimulatorAdapter aspectJSimulatorAdapter = new AspectJSimulatorAdapterImpl();

        @Around("call(* TransferBean.*(..)) && within(TransferBeanAspectJAopTest)")
        public Object invoke(ProceedingJoinPoint pjp) throws Exception {
            return aspectJSimulatorAdapter.invoke(pjp, TransferBeanAspectJAopTest.class, false);
        }
    }

    @Test
    public void testGetAccounts() {
        List<Account> accounts = transferBean.getAccounts("1111");
        assertNotNull(accounts);
    }
}
