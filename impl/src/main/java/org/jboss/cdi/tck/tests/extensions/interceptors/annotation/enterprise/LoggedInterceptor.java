package org.jboss.cdi.tck.tests.extensions.interceptors.annotation.enterprise;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@Logged
@Priority(1000)
public class LoggedInterceptor {
    
    public static AtomicBoolean intercepted = new AtomicBoolean();

    @AroundInvoke
    public Object logInvocation(InvocationContext ctx) throws Exception {
        intercepted.set(true);
        return ctx.proceed();
    }
}
