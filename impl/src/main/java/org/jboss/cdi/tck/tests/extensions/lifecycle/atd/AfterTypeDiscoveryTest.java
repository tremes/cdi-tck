/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc., and individual contributors
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
package org.jboss.cdi.tck.tests.extensions.lifecycle.atd;

import static org.jboss.cdi.tck.cdi.Sections.ATD;
import static org.jboss.cdi.tck.cdi.Sections.BEAN_DISCOVERY;
import static org.jboss.cdi.tck.cdi.Sections.PP;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.cdi.tck.AbstractTest;
import org.jboss.cdi.tck.shrinkwrap.WebArchiveBuilder;
import org.jboss.cdi.tck.tests.extensions.lifecycle.atd.lib.Bar;
import org.jboss.cdi.tck.tests.extensions.lifecycle.atd.lib.Boss;
import org.jboss.cdi.tck.tests.extensions.lifecycle.atd.lib.Foo;
import org.jboss.cdi.tck.tests.extensions.lifecycle.atd.lib.Pro;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

/**
 *
 * @author Martin Kouba
 */
@SpecVersion(spec = "cdi", version = "1.1 Final Release")
public class AfterTypeDiscoveryTest extends AbstractTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return new WebArchiveBuilder()
                .withTestClassPackage(AfterTypeDiscoveryTest.class)
                .withExtension(AfterTypeDiscoveryObserver.class)
                .withLibrary(Boss.class, Foo.class, Bar.class, Pro.class)
                .withBeansXml(
                        Descriptors.create(BeansDescriptor.class).getOrCreateInterceptors()
                                .clazz(CharlieInterceptor.class.getName()).up().getOrCreateDecorators()
                                .clazz(CharlieDecorator.class.getName()).up().getOrCreateAlternatives()
                                .clazz(CharlieAlternative.class.getName()).up()).build();
    }

    @Inject
    AfterTypeDiscoveryObserver extension;

    @Test
    @SpecAssertions({ @SpecAssertion(section = AFTER_TYPE_DISCOVERY, id = "a"), @SpecAssertion(section = AFTER_TYPE_DISCOVERY, id = "c"),
            @SpecAssertion(section = AFTER_TYPE_DISCOVERY, id = "hb") })
    public void testInitialInterceptors() {
        assertTrue(extension.getInterceptors().contains(BravoInterceptor.class));
        assertTrue(extension.getInterceptors().contains(AlphaInterceptor.class));
    }

    @Test
    @SpecAssertions({ @SpecAssertion(section = AFTER_TYPE_DISCOVERY, id = "b"), @SpecAssertion(section = AFTER_TYPE_DISCOVERY, id = "ha") })
    public void testInitialAlternatives() {
        assertEquals(extension.getAlternatives().size(), 2);
        assertEquals(extension.getAlternatives().get(0), AlphaAlternative.class);
        assertEquals(extension.getAlternatives().get(1), BravoAlternative.class);
    }

    @Test
    @SpecAssertions({ @SpecAssertion(section = AFTER_TYPE_DISCOVERY, id = "d"), @SpecAssertion(section = AFTER_TYPE_DISCOVERY, id = "hc") })
    public void testInitialDecorators() {
        assertEquals(extension.getDecorators().size(), 2);
        assertEquals(extension.getDecorators().get(0), AlphaDecorator.class);
        assertEquals(extension.getDecorators().get(1), BravoDecorator.class);
    }

    @Test(dataProvider = ARQUILLIAN_DATA_PROVIDER)
    @SpecAssertions({ @SpecAssertion(section = AFTER_TYPE_DISCOVERY, id = "gb") })
    public void testFinalInterceptors(TransactionLogger logger) {

        AlphaInterceptor.reset();
        BravoInterceptor.reset();
        CharlieInterceptor.reset();

        logger.ping();

        assertTrue(AlphaInterceptor.isIntercepted());
        assertFalse(BravoInterceptor.isIntercepted());
        assertTrue(CharlieInterceptor.isIntercepted());
    }

    @Test(dataProvider = ARQUILLIAN_DATA_PROVIDER)
    @SpecAssertions({ @SpecAssertion(section = AFTER_TYPE_DISCOVERY, id = "gc") })
    public void testFinalDecorators(TransactionLogger logger) {
        assertEquals(logger.log("ping"), "pingbravoalphacharlie");
    }

    @Test(dataProvider = ARQUILLIAN_DATA_PROVIDER)
    @SpecAssertions({ @SpecAssertion(section = AFTER_TYPE_DISCOVERY, id = "ga") })
    public void testFinalAlternatives(TransactionLogger logger) {
        // assert that proper alternative is injected
        assertEquals(logger.getAlternativeClass(), DeltaAlternative.class);
        assertTrue(getBeans(AlphaAlternative.class).isEmpty());
        assertTrue(getBeans(BravoAlternative.class).isEmpty());
    }

    @SuppressWarnings("serial")
    @Test
    @SpecAssertions({ @SpecAssertion(section = AFTER_TYPE_DISCOVERY, id = "e"), @SpecAssertion(section = BEAN_DISCOVERY, id = "r") })
    public void testAddAnnotatedType() {
        assertTrue(extension.isBossObserved());
        getUniqueBean(Boss.class);

        assertEquals(getBeans(Bar.class).size(), 0);
        assertEquals(getBeans(Bar.class, new AnnotationLiteral<Pro>() {
        }).size(), 1);

        assertEquals(getBeans(Foo.class).size(), 0);
        assertEquals(getBeans(Foo.class, new AnnotationLiteral<Pro>() {
        }).size(), 1);
    }

    @Test
    @SpecAssertions({ @SpecAssertion(section = PROCESS_PRODUCER, id = "ab"), @SpecAssertion(section = PROCESS_PRODUCER, id = "bb") })
    public void testProcessProducerEventFiredForProducerField() {
        assertTrue(extension.isProcessProcuderFieldObserved());
    }

    @Test
    @SpecAssertions({ @SpecAssertion(section = PROCESS_PRODUCER, id = "aa"), @SpecAssertion(section = PROCESS_PRODUCER, id = "ba") })
    public void testProcessProducerEventFiredForProducerMethod() {
        assertTrue(extension.isProcessProcuderMethodObserved());
    }

}
