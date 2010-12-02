package org.rsimulator.example.unittest.transfer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.Test;
import org.rsimulator.example.unittest.transfer.account.Account;
import org.rsimulator.aop.AspectJSimulatorAdapter;

public class TransferBeanInterceptorTest {
    private TransferBean transferBean;
    
    @SuppressWarnings("unused")
    @Aspect
    private static class SimulatorAspect {   
        //TODO Inject
        private AspectJSimulatorAdapter aspectJSimulatorAdapter = new AspectJSimulatorAdapter();
        
        @Around("call(* TransferBean.*(..)) && within(TransferBeanInterceptorTest)")
        public Object invoke(ProceedingJoinPoint pjp) throws IOException {
            return aspectJSimulatorAdapter.invoke(pjp, TransferBeanInterceptorTest.class, false);
        }   
    }
      
    @Test
    public void testGetAccounts() {
        List<Account> accounts = transferBean.getAccounts("1111");
        assertNotNull(accounts);
    }

    // @Test
    public void testTransfer() {
        fail("Not yet implemented");
    }
}
