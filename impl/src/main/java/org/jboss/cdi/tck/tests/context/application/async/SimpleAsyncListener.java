/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.cdi.tck.tests.context.application.async;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.jboss.cdi.tck.util.SimpleLogger;

/**
 * @author Martin Kouba
 */
public class SimpleAsyncListener implements AsyncListener {

    public static Long onStartAsync = null;
    public static Long onError = null;
    public static Long onTimeout = null;
    public static Long onComplete = null;
    public static boolean isApplicationContextActive = false;
    public static boolean isSimpleApplicationBeanAvailable = false;

    private static final SimpleLogger logger = new SimpleLogger(SimpleAsyncListener.class);

    @Inject
    SimpleApplicationBean simpleApplicationBean;

    @Inject
    BeanManager beanManager;

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.AsyncListener#onComplete(javax.servlet.AsyncEvent)
     */
    @Override
    public void onComplete(AsyncEvent event) throws IOException {
        logger.log("onComplete");
        onComplete = System.currentTimeMillis();

        if (onTimeout == null && onError == null) {
            // Do not check and write info in case of post timeout/error action
            checkApplicationContextAvailability(event);
            writeInfo(event.getSuppliedResponse());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.AsyncListener#onTimeout(javax.servlet.AsyncEvent)
     */
    @Override
    public void onTimeout(AsyncEvent event) throws IOException {
        logger.log("onTimeout");
        onTimeout = System.currentTimeMillis();
        checkApplicationContextAvailability(event);
        writeInfo(event.getAsyncContext().getResponse());
        event.getAsyncContext().complete();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.AsyncListener#onError(javax.servlet.AsyncEvent)
     */
    @Override
    public void onError(AsyncEvent event) throws IOException {
        logger.log("onError");
        onError = System.currentTimeMillis();
        checkApplicationContextAvailability(event);
        writeInfo(event.getAsyncContext().getResponse());
        event.getAsyncContext().complete();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.AsyncListener#onStartAsync(javax.servlet.AsyncEvent)
     */
    @Override
    public void onStartAsync(AsyncEvent event) throws IOException {
        logger.log("onStartAsync");
        onStartAsync = System.currentTimeMillis();
        checkApplicationContextAvailability(event);
    }

    private boolean checkApplicationContextAvailability(AsyncEvent event) throws IOException {
        try {
            isSimpleApplicationBeanAvailable = simpleApplicationBean.ping();
            isApplicationContextActive = beanManager.getContext(ApplicationScoped.class).isActive();
        } catch (Throwable e) {
            logger.log("Problem while checking application scope: " + e.getMessage());
        }
        if (!isApplicationContextActive || !isSimpleApplicationBeanAvailable) {
            ((HttpServletResponse) event.getAsyncContext().getResponse()).setStatus(500);
            return false;
        }
        return true;
    }

    public static void reset() {
        onStartAsync = null;
        onError = null;
        onTimeout = null;
        onComplete = null;
        isApplicationContextActive = false;
        isSimpleApplicationBeanAvailable = false;
    }

    private void writeInfo(ServletResponse response) throws IOException {
        response.getWriter().print(getInfo());
        response.getWriter().flush();
    }

    public static String getInfo() {
        return String
                .format("onStartAsync: %s, onError: %s, onTimeout: %s, onComplete: %s, isApplicationContextActive: %s, isSimpleApplicationBeanAvailable: %s",
                        onStartAsync, onError, onTimeout, onComplete, isApplicationContextActive,
                        isSimpleApplicationBeanAvailable);
    }

}
