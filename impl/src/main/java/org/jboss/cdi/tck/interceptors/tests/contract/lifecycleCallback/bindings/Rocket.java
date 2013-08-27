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

package org.jboss.cdi.tck.interceptors.tests.contract.lifecycleCallback.bindings;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.jboss.cdi.tck.util.ActionSequence;

@Destructive
@Airborne
public class Rocket extends Weapon {

    @Inject
    private Foo foo;

    public Foo getFoo() {
        return foo;
    }

    public void fire() {
    }

    @PostConstruct
    public void postConstruct() {
        ActionSequence.addAction("postConstruct", Rocket.class.getName());
    }

    @PreDestroy
    public void preDestroy() {
        ActionSequence.addAction("preDestroy", Rocket.class.getName());
    }

}
