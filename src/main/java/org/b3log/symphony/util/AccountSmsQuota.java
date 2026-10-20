package org.b3log.symphony.util;

import org.b3log.symphony.processor.LoginProcessor;

/** All SMS entry points retain the existing shared budgets and evaluation order. */
public final class AccountSmsQuota {
    private AccountSmsQuota(){ }
    public static boolean access(String ip,String name,String phone){
        synchronized(LoginProcessor.verifySMSCodeLimiterOfIPLong){
            return LoginProcessor.verifySMSCodeLimiterOfIPLong.access(ip)
                    &&LoginProcessor.verifySMSCodeLimiterOfIP.access(ip)
                    &&LoginProcessor.verifySMSCodeLimiterOfName.access(name)
                    &&LoginProcessor.verifySMSCodeLimiterOfPhone.access(phone);
        }
    }
}
