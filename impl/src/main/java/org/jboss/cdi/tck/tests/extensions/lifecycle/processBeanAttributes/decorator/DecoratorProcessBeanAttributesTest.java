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

package org.jboss.cdi.tck.tests.extensions.lifecycle.processBeanAttributes.decorator;

import static org.jboss.cdi.tck.TestGroups.INTEGRATION;
import static org.jboss.cdi.tck.cdi.Sections.PROCESS_BEAN_ATTRIBUTES;
import static org.testng.Assert.assertEquals;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.cdi.tck.AbstractTest;
import org.jboss.cdi.tck.shrinkwrap.WebArchiveBuilder;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans11.BeansDescriptor;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

/**
 * 
 * @author Martin Kouba
 */
@Test(groups = INTEGRATION)
@SpecVersion(spec = "cdi", version = "1.1 Final Release")
public class DecoratorProcessBeanAttributesTest extends AbstractTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return new WebArchiveBuilder()
                .withTestClass(DecoratorProcessBeanAttributesTest.class)
                .withClasses(VerifyingExtension.class, AlphaDecorator.class, Alpha.class, Charlie.class)
                .withExtension(VerifyingExtension.class)
                .withBeanLibrary(BravoDecorator.class, Bravo.class)
                .withBeanLibrary(
                        Descriptors.create(BeansDescriptor.class).getOrCreateDecorators()
                                .clazz(AlphaDecorator.class.getName(), BravoDecorator.class.getName()).up())
                .withBeansXml(
                        Descriptors.create(BeansDescriptor.class).getOrCreateDecorators()
                                .clazz(AlphaDecorator.class.getName(), BravoDecorator.class.getName()).up()).build();
    }

    @Test
    @SpecAssertion(section = PROCESS_BEAN_ATTRIBUTES, id = "ac")
    public void testAlphaDecoratorObserved() {
        assertEquals(VerifyingExtension.aplhaDecoratorObserved.get(), 1);
    }

    @Test
    @SpecAssertion(section = PROCESS_BEAN_ATTRIBUTES, id = "ac")
    public void testBravoDecoratorObserved() {
        assertEquals(VerifyingExtension.bravoDecoratorObserved.get(), 1);
    }
}
