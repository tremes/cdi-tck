package org.jboss.cdi.tck.tests.context.passivating.broken.producer.method.managed.dependent;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;

@SessionScoped
public class ProducerMethodParamInjectionCorralBroken extends Ranch {
    @Override
    public void ping() {
        
    }
    
    @Produces
    @British
    @SessionScoped
    public Herd produce(@British Cow cow){
       return new Herd();
    }
}
