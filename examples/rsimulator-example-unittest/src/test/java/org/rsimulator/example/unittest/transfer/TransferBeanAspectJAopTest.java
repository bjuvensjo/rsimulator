package org.rsimulator.example.unittest.transfer;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.Test;
import org.rsimulator.aop.AspectJSimulatorAdapter;
import org.rsimulator.aop.AspectJSimulatorAdapterImpl;
import org.rsimulator.example.unittest.transfer.account.Account;

public class TransferBeanAspectJAopTest {
    private TransferBean transferBean;
    
    @SuppressWarnings("unused")
    @Aspect
    private static class SimulatorAspect {   
        private AspectJSimulatorAdapter aspectJSimulatorAdapter = new AspectJSimulatorAdapterImpl();
        
        @Around("call(* TransferBean.*(..)) && within(TransferBeanAspectJAopTest)")
        public Object invoke(ProceedingJoinPoint pjp) throws IOException {
            return aspectJSimulatorAdapter.invoke(pjp, TransferBeanAspectJAopTest.class, false);
        }   
    }
      
    @Test
    public void testGetAccounts() {
        List<Account> accounts = transferBean.getAccounts("1111");
        assertNotNull(accounts);
    }
}
