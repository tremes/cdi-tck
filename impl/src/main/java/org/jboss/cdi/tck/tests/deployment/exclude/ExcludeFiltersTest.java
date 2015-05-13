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
package org.jboss.cdi.tck.tests.deployment.exclude;

import static org.jboss.cdi.tck.TestGroups.INTEGRATION;
import static org.jboss.cdi.tck.TestGroups.SYSTEM_PROPERTIES;
import static org.jboss.cdi.tck.cdi.Sections.BEAN_DISCOVERY_STEPS;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.cdi.tck.AbstractTest;
import org.jboss.cdi.tck.TestSystemProperty;
import org.jboss.cdi.tck.shrinkwrap.WebArchiveBuilder;
import org.jboss.cdi.tck.tests.deployment.exclude.food.Meat;
import org.jboss.cdi.tck.tests.deployment.exclude.haircut.Chonmage;
import org.jboss.cdi.tck.tests.deployment.exclude.mustache.Mustache;
import org.jboss.cdi.tck.tests.deployment.exclude.mustache.beard.Beard;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans11.BeanDiscoveryMode;
import org.jboss.shrinkwrap.descriptor.api.beans11.BeansDescriptor;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

/**
 * @author Martin Kouba
 */
@SpecVersion(spec = "cdi", version = "1.1 Final Release")
public class ExcludeFiltersTest extends AbstractTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return new WebArchiveBuilder()
                .withTestClassPackage(ExcludeFiltersTest.class)
                .withPackage(Mustache.class.getPackage())
                .withPackage(Beard.class.getPackage())
                .withPackage(Chonmage.class.getPackage())
                .withPackage(Meat.class.getPackage())
                .withBeansXml(
                        Descriptors.create(BeansDescriptor.class).beanDiscoveryMode(BeanDiscoveryMode._ALL.toString()).createScan().getOrCreateExclude()
                                .name(Chonmage.class.getPackage().getName() + ".*").name(Mustache.class.getPackage().getName() + ".**")
                                .name(Meat.class.getPackage().getName() + ".*").createIfClassAvailable().name("com.some.unreal.class.Name").up()
                                .name(Meat.class.getPackage().getName() + ".*").createIfClassNotAvailable().name(ExcludeFiltersTest.class.getName()).up()
                                .name(Stubble.class.getName()).name(Alpha.class.getName()).createIfClassAvailable()
                                .name(Stubble.class.getName()).up()
                                .name(Foxtrot.class.getName()).createIfClassAvailable().name("com.some.unreal.class.Name").up()
                                .name(Bravo.class.getName()).createIfClassNotAvailable().name("com.some.unreal.class.Name").up()
                                .name(Echo.class.getName()).createIfClassNotAvailable().name(ExcludeFiltersTest.class.getName()).up()
                                .name(Charlie.class.getName()).createIfSystemProperty().name(TestSystemProperty.EXCLUDE_DUMMY.getKey()).up()
                                .name(Delta.class.getName()).createIfSystemProperty().name(TestSystemProperty.EXCLUDE_DUMMY.getKey())
                                .value(TestSystemProperty.EXCLUDE_DUMMY.getValue()).up().up().up()
                )
                .withExtension(VerifyingExtension.class).build();
    }

    @Inject
    VerifyingExtension extension;

    @Test
    @SpecAssertions({ @SpecAssertion(section = BEAN_DISCOVERY_STEPS, id = "pa"), @SpecAssertion(section = BEAN_DISCOVERY_STEPS, id = "qa") })
    public void testTypeFcqnMatchesExcludeFilterName() {
        assertTypeIsExcluded(Stubble.class);
        assertTypeIsNotExcluded(Golf.class);
    }

    @Test
    @SpecAssertions({ @SpecAssertion(section = BEAN_DISCOVERY_STEPS, id = "pa"), @SpecAssertion(section = BEAN_DISCOVERY_STEPS, id = "qb"),
            @SpecAssertion(section = BEAN_DISCOVERY_STEPS, id = "qc") })
    public void testTypePackageMatchesExcludeFilterName() {
        assertTypeIsExcluded(Mustache.class);
        assertTypeIsExcluded(Beard.class);
        assertTypeIsExcluded(Chonmage.class);
    }

    @Test
    @SpecAssertions({ @SpecAssertion(section = BEAN_DISCOVERY_STEPS, id = "pb"), @SpecAssertion(section = BEAN_DISCOVERY_STEPS, id = "pc") })
    public void testExcludeClassActivators() {
        assertTypeIsExcluded(Alpha.class);
        assertTypeIsNotExcluded(Foxtrot.class);
        assertTypeIsExcluded(Bravo.class);
        assertTypeIsNotExcluded(Echo.class);
        assertTypeIsNotExcluded(Meat.class);
    }

    @Test(groups = { INTEGRATION, SYSTEM_PROPERTIES })
    @SpecAssertions({ @SpecAssertion(section = BEAN_DISCOVERY_STEPS, id = "pd"), @SpecAssertion(section = BEAN_DISCOVERY_STEPS, id = "pe") })
    public void testExcludeSystemPropertyActivator() {
        assertTypeIsExcluded(Charlie.class);
        assertTypeIsExcluded(Delta.class);
    }

    private void assertTypeIsExcluded(Class<?> type) {
        assertTrue(getBeans(type).isEmpty());
        assertFalse(extension.getObservedAnnotatedTypes().contains(type));
    }

    private void assertTypeIsNotExcluded(Class<?> type) {
        assertFalse(getBeans(type).isEmpty());
        assertTrue(extension.getObservedAnnotatedTypes().contains(type));
    }

}
