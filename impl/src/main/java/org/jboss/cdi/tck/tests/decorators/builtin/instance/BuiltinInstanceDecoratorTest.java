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

package org.jboss.cdi.tck.tests.decorators.builtin.instance;

import static org.jboss.cdi.tck.TestGroups.INTEGRATION;
import static org.jboss.cdi.tck.cdi.Sections.DECORATOR_INVOCATION;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;
import javax.inject.Provider;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.cdi.tck.shrinkwrap.WebArchiveBuilder;
import org.jboss.cdi.tck.tests.decorators.AbstractDecoratorTest;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans11.BeansDescriptor;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

/**
 * Basic test for decorating built-in {@link Instance} bean.
 *
 * @author Martin Kouba
 *
 */
@SpecVersion(spec = "cdi", version = "1.1 Final Release")
public class BuiltinInstanceDecoratorTest extends AbstractDecoratorTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return new WebArchiveBuilder()
                .withTestClassPackage(BuiltinInstanceDecoratorTest.class)
                .withClass(AbstractDecoratorTest.class)
                .withBeansXml(
                        Descriptors.create(BeansDescriptor.class).getOrCreateDecorators()
                                .clazz(MuleInstanceDecorator.class.getName()).up()).build();
    }

    @Inject
    Instance<Mule> instance;

    @SuppressWarnings({ "serial" })
    @Test(groups = INTEGRATION)
    @SpecAssertions({ @SpecAssertion(section = DECORATOR_INVOCATION, id = "acb") })
    public void testDecoratorIsResolved() {
        TypeLiteral<Instance<Mule>> instanceLiteral = new TypeLiteral<Instance<Mule>>() {
        };
        TypeLiteral<Provider<Mule>> providerLiteral = new TypeLiteral<Provider<Mule>>() {
        };
        TypeLiteral<Iterable<Mule>> iterableLiteral = new TypeLiteral<Iterable<Mule>>() {
        };
        checkDecorator(
                resolveUniqueDecorator(Collections.singleton(instanceLiteral.getType())),
                MuleInstanceDecorator.class,
                new HashSet<Type>(
                        Arrays.asList(instanceLiteral.getType(), providerLiteral.getType(), iterableLiteral.getType())),
                instanceLiteral.getType());
    }

    @Test(groups = INTEGRATION)
    @SpecAssertions({ @SpecAssertion(section = DECORATOR_INVOCATION, id = "acb") })
    public void testDecoratorIsInvoked() {
        assertTrue(instance.isAmbiguous());
        Mule mule = instance.get();
        assertNotNull(mule);
    }
}
