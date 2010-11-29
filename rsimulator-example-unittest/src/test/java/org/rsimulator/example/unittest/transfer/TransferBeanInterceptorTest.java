package org.rsimulator.example.unittest.transfer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.Test;
import org.rsimulator.example.unittest.transfer.account.Account;
import org.rsimulator.interceptor.AspectJSimulator;

public class TransferBeanInterceptorTest {
    private TransferBean transferBean;
    
    @SuppressWarnings("unused")
    @Aspect
    private static class TransferBeanInterceptorTestAspect extends AspectJSimulator {        
        public TransferBeanInterceptorTestAspect() {
            setRootPath(TransferBeanInterceptorTest.class);
        }       

        //@Pointcut("call(* org.rsimulator.example.unittest.transfer.account.AccountPort.getAccounts(..))")
        @Pointcut("call(* TransferBean.*(..)) && within(TransferBeanInterceptorTest)")
        protected void aspectJSimulatorPointcut() {            
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
