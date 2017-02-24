/*******************************************************************************
 * Copyright (c) 2012-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.pullrequest.server;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import org.eclipse.che.api.project.server.type.ProjectTypeDef;
import org.eclipse.che.inject.DynaModule;

/**
 * The contribution project type module bindings.
 *
 * @author Kevin Pollet
 */
@DynaModule
public class ContributionProjectTypeModule extends AbstractModule {
    @Override
    protected void configure() {
        final Multibinder<ProjectTypeDef> projectTypeMultibinder = Multibinder.newSetBinder(binder(), ProjectTypeDef.class);
        projectTypeMultibinder.addBinding().to(ContributionProjectType.class);
    }
}

