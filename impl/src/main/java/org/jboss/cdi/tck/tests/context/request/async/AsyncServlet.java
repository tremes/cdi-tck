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
package org.jboss.cdi.tck.tests.context.request.async;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.cdi.tck.util.SimpleLogger;

@WebServlet(name = "AsyncServlet", urlPatterns = { "/AsyncServlet" }, asyncSupported = true)
@SuppressWarnings("serial")
public class AsyncServlet extends HttpServlet {

    public static final String TEST_TIMEOUT = "timeout";
    public static final String TEST_COMPLETE = "complete";
    public static final String TEST_ERROR = "error";
    public static final String TEST_LOOP = "loop";
    private static final String[] VALID_TESTS = new String[] { TEST_TIMEOUT, TEST_COMPLETE, TEST_ERROR, TEST_LOOP };

    private static final SimpleLogger logger = new SimpleLogger(AsyncServlet.class);

    private static final long TIMEOUT = 200l;

    private static boolean inLoop = false;

    private ExecutorService executorService;

    @Override
    public void init() throws ServletException {
        // Note that executor thread does not use the same CL
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String test = req.getParameter("test");
        if (!Arrays.asList(VALID_TESTS).contains(test)) {
            resp.setStatus(404);
            return;
        }

        SimpleAsyncListener.reset();
        final AsyncContext actx = req.startAsync();
        actx.addListener(actx.createListener(SimpleAsyncListener.class), req, resp);
        resp.setContentType("text/plain");

        if (TEST_TIMEOUT.equals(test)) {
            actx.setTimeout(TIMEOUT);
        } else if (TEST_COMPLETE.equals(test)) {
            executorService.execute(new AsyncRequestProcessor(actx, 50l, false, null));
        } else if (TEST_ERROR.equals(test)) {
            executorService.execute(new AsyncRequestProcessor(actx, 50l, true, "/FailingServlet"));
        } else if (TEST_LOOP.equals(test)) {
            if (inLoop) {
                executorService.execute(new AsyncRequestProcessor(actx, 50l, false, null));
            } else {
                executorService.execute(new AsyncRequestProcessor(actx, 50l, true, null));
                inLoop = true;
            }
        }
        logger.log("Service finished: {0}", test);
    }
}
