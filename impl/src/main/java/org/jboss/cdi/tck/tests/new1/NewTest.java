package org.jboss.cdi.tck.tests.new1;

import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.CDIProvider;

import org.jboss.arquillian.container.composite.archive.ClassPathCompositeArchive;
import org.jboss.arquillian.container.composite.archive.ClassPathCompositeArchiveBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.cdi.tck.tests.SimpleTestBean;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NewTest extends Arquillian {

    @Deployment
    public static ClassPathCompositeArchive deployment() {

        JavaArchive testJar = ShrinkWrap.create(JavaArchive.class).addClasses(SimpleTestBean.class, NewTest.class).addAsManifestResource(
                new StringAsset(Descriptors.create(BeansDescriptor.class).exportAsString()), "beans.xml");
        return ClassPathCompositeArchiveBuilder.create().addJavaArchive(testJar);
    }

    @Test
    public void test() {
        CDIProvider cdiProvider = CDI.getCDIProvider();
        SimpleTestBean testBean = cdiProvider.initialize().select(SimpleTestBean.class).get();
        Assert.assertNotNull(testBean);
    }

}
