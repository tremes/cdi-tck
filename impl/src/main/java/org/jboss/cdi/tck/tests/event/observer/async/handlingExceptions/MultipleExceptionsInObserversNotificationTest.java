/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.cdi.tck.tests.event.observer.async.handlingExceptions;

import static org.jboss.cdi.tck.cdi.Sections.ASYNC_EXCEPTION;
import static org.jboss.cdi.tck.cdi.Sections.OBSERVER_NOTIFICATION;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.FireAsyncException;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.cdi.tck.AbstractTest;
import org.jboss.cdi.tck.shrinkwrap.WebArchiveBuilder;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

@SpecVersion(spec = "cdi", version = "2.0-EDR1")
public class MultipleExceptionsInObserversNotificationTest extends AbstractTest {

    @Inject
    Event<RadioMessage> event;

    AtomicBoolean active = new AtomicBoolean(false);

    @Deployment
    public static WebArchive createTestArchive() {
        return new WebArchiveBuilder().withTestClassPackage(MultipleExceptionsInObserversNotificationTest.class).build();
    }

    @Test
    @SpecAssertions({ @SpecAssertion(section = ASYNC_EXCEPTION, id = "a"), @SpecAssertion(section = ASYNC_EXCEPTION, id = "b"),
            @SpecAssertion(section = OBSERVER_NOTIFICATION, id = "cb") })
    public void testMultipleExceptionsDuringVariousObserversNotification() throws InterruptedException {
        BlockingQueue<Throwable> queue = new LinkedBlockingQueue<>();
        event.fireAsync(new RadioMessage()).handle((event, throwable) -> {
            func(event, throwable);
            queue.offer(throwable);
            return throwable;
        });

        Throwable throwable = queue.poll(2, TimeUnit.SECONDS);
        assertNotNull(throwable);
        assertTrue(TokioRadioStation.observed.get());
        assertTrue(LondonRadioStation.observed.get());
        assertTrue(NewYorkRadioStation.observed.get());
        assertTrue(ParisRadioStation.observed.get());
        assertTrue(PragueRadioStation.observed.get());

        assertTrue(throwable instanceof FireAsyncException);

        List<Throwable> suppressedExceptions = Arrays.asList(throwable.getSuppressed());
        assertTrue(suppressedExceptions.contains(ParisRadioStation.exception.get()));
        assertTrue(suppressedExceptions.contains(NewYorkRadioStation.exception.get()));
        assertTrue(suppressedExceptions.contains(LondonRadioStation.exception.get()));
        assertTrue(suppressedExceptions.stream().anyMatch(t -> t.getMessage().equals(ParisRadioStation.class.getName())));
        assertTrue(suppressedExceptions.stream().anyMatch(t -> t.getMessage().equals(NewYorkRadioStation.class.getName())));
        assertTrue(suppressedExceptions.stream().anyMatch(t -> t.getMessage().equals(LondonRadioStation.class.getName())));
        assertFalse(active.get());

    }

    private Throwable func(RadioMessage mess, Throwable t) {
        active.set(getCurrentManager().getContext(SessionScoped.class).isActive());
        return t;
    }
}
