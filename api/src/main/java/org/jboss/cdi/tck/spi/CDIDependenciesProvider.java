package org.jboss.cdi.tck.spi;

public class CDIDependenciesProvider {

  /*  public static List<JavaArchive> getDependenciesJars() {

        List<JavaArchive> jars = new ArrayList<>();
        File[] mavenDependencies = Resolvers.use(MavenResolverSystem.class).resolve("org.jboss.weld.se:weld-se-core:3.0.0.Alpha10", "javax.enterprise:cdi-api:2.0-EDR1").withTransitivity().asFile();
        for (File file : mavenDependencies) {
            JavaArchive jar = ShrinkWrap.create(ZipImporter.class, file.getName()).importFrom(file).as(JavaArchive.class);
            jars.add(jar);
        }
        return jars;
    }    */
}
