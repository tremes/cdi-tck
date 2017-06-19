/*
 * JBoss, Home of Professional Open Source
 * Copyright 2017, Red Hat, Inc., and individual contributors
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
package org.jboss.cdi.tck.tests.extensions.beanDiscovery.event.ordering;

import static org.jboss.cdi.tck.cdi.Sections.BEAN_DISCOVERY_STEPS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import javax.enterprise.inject.spi.ProcessBeanAttributes;
import javax.enterprise.inject.spi.ProcessInjectionPoint;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.inject.spi.ProcessManagedBean;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.cdi.tck.AbstractTest;
import org.jboss.cdi.tck.shrinkwrap.WebArchiveBuilder;
import org.jboss.cdi.tck.util.ActionSequence;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

/**
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
@SpecVersion(spec = "cdi", version = "2.0")
public class LifecycleEventOrderingTest extends AbstractTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return new WebArchiveBuilder()
            .withTestClassPackage(LifecycleEventOrderingTest.class)
            .withExtension(ProductManagement.class).build();
    }

    @Inject
    ProductManagement extension;

    @Test
    @SpecAssertions({
        @SpecAssertion(section = BEAN_DISCOVERY_STEPS, id = "ja"),
        @SpecAssertion(section = BEAN_DISCOVERY_STEPS, id = "jb"),
        @SpecAssertion(section = BEAN_DISCOVERY_STEPS, id = "jc"),
        @SpecAssertion(section = BEAN_DISCOVERY_STEPS, id = "jd") })
    public void testEventsWereFiredInCorrectOrderForProducer() {
        ActionSequence producerEventsSeq = ActionSequence.getSequence(ProductManagement.PRODUCER_SEQ);
        producerEventsSeq.assertSequenceDataEquals("PIP", "PP", "PBA", "PPM");
//        List<Object> actualListOfEvents = extension.getListOfProducerEvents();
//        assertEquals(4, actualListOfEvents.size());
//        assertTrue(actualListOfEvents.get(0) instanceof ProcessInjectionPoint);
//        assertTrue(actualListOfEvents.get(1) instanceof ProcessProducer);
//        assertTrue(actualListOfEvents.get(2) instanceof ProcessBeanAttributes);
//        assertTrue(actualListOfEvents.get(3) instanceof ProcessProducerMethod);
    }

    @Test
    @SpecAssertions({
        @SpecAssertion(section = BEAN_DISCOVERY_STEPS, id = "e"),
        @SpecAssertion(section = BEAN_DISCOVERY_STEPS, id = "f"),
        @SpecAssertion(section = BEAN_DISCOVERY_STEPS, id = "h"),
        @SpecAssertion(section = BEAN_DISCOVERY_STEPS, id = "i") })
    public void testEventsWereFiredInCorrectOrderForManagedBean() {
        List<Object> actualListOfEvents = extension.getListOfBeanEvents();
        assertEquals(4, actualListOfEvents.size());
        assertTrue(actualListOfEvents.get(0) instanceof ProcessInjectionPoint);
        assertTrue(actualListOfEvents.get(1) instanceof ProcessInjectionTarget);
        assertTrue(actualListOfEvents.get(2) instanceof ProcessBeanAttributes);
        assertTrue(actualListOfEvents.get(3) instanceof ProcessManagedBean);
    }
}
