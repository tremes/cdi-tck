package org.jboss.cdi.tck.tests.extensions.interceptors.annotation.enterprise;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.interceptor.InterceptorBinding;

@InterceptorBinding
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Logged {

    public static class LoggedLiteral extends AnnotationLiteral<Logged> implements Logged {

        public LoggedLiteral() {

        }
    }

}