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

package org.jboss.cdi.tck.tests.deployment.packaging.rar;

import static org.jboss.cdi.tck.TestGroups.JAVAEE_FULL;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.cdi.tck.AbstractTest;
import org.jboss.cdi.tck.shrinkwrap.EnterpriseArchiveBuilder;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;
import org.jboss.shrinkwrap.descriptor.api.connector16.ConnectorDescriptor;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

/**
 * Test resource adapter bean archive included in an EAR.
 * 
 * TODO I'm not completely sure about the test RAR structure and connector descriptor.
 * 
 * @author Martin Kouba
 */
@SpecVersion(spec = "cdi", version = "20091101")
public class ResourceAdapterArchiveTest extends AbstractTest {

    @Deployment
    public static EnterpriseArchive createTestArchive() {

        EnterpriseArchive enterpriseArchive = new EnterpriseArchiveBuilder().withTestClass(ResourceAdapterArchiveTest.class)
                .build();

        ResourceAdapterArchive rar = ShrinkWrap.create(ResourceAdapterArchive.class);
        rar.addAsLibrary(ShrinkWrap
                .create(JavaArchive.class)
                .addClasses(Translator.class, TestResourceAdapter.class)
                .addAsManifestResource(new StringAsset(Descriptors.create(BeansDescriptor.class).exportAsString()), "beans.xml"));
        rar.addAsManifestResource(new StringAsset(Descriptors.create(BeansDescriptor.class).exportAsString()), "beans.xml");
        rar.addAsManifestResource(
                new StringAsset(Descriptors.create(ConnectorDescriptor.class).version("1.6").displayName("Test RA")
                        .vendorName("Red Hat Middleware LLC").eisType("Test RA").resourceadapterVersion("0.1")
                        .getOrCreateResourceadapter().resourceadapterClass(TestResourceAdapter.class.getName())
                        .getOrCreateOutboundResourceadapter().transactionSupport("NoTransaction")
                        .reauthenticationSupport(false).up().up().exportAsString()), "ra.xml");

        enterpriseArchive.addAsModule(rar);
        return enterpriseArchive;
    }

    @Test(groups = JAVAEE_FULL, dataProvider = ARQUILLIAN_DATA_PROVIDER)
    @SpecAssertions({ @SpecAssertion(section = "12.1", id = "bbd") })
    public void testInjection(Translator translator) {
        assertNotNull(translator);
        assertEquals(translator.ping(), 1);
    }

    @Test(groups = JAVAEE_FULL)
    @SpecAssertions({ @SpecAssertion(section = "12.1", id = "bbd") })
    public void testResolution() {
        getUniqueBean(Translator.class);
    }

}