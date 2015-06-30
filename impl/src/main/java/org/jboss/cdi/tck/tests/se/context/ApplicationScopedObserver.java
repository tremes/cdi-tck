/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.cdi.tck.tests.se.context;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

public class ApplicationScopedObserver {

    public static boolean isInitialized;
    public static boolean isDestroyed;
    public static Object initializedEventPayload;
    public static Object destroyedEventPayload;

    public void observesApplicationScopedIsInitialized(@Observes @Initialized(ApplicationScoped.class) Object obj) {
        isInitialized = true;
        initializedEventPayload = obj;
    }

    public void observesApplicationScopedIsDestroyed(@Observes @Destroyed(ApplicationScoped.class) Object obj) {
        isDestroyed = true;
        destroyedEventPayload = obj;
    }
}
