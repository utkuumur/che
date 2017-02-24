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

import org.eclipse.che.plugin.pullrequest.shared.ContributionProjectTypeConstants;

import org.eclipse.che.api.project.server.type.ProjectTypeDef;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.eclipse.che.plugin.pullrequest.shared.ContributionProjectTypeConstants.CONTRIBUTION_PROJECT_TYPE_DISPLAY_NAME;
import static org.eclipse.che.plugin.pullrequest.shared.ContributionProjectTypeConstants.CONTRIBUTION_PROJECT_TYPE_ID;

/**
 * The contribution project type definition.
 *
 * @author Kevin Pollet
 */
@Singleton
public class ContributionProjectType extends ProjectTypeDef {
    @Inject
    public ContributionProjectType() {
        super(CONTRIBUTION_PROJECT_TYPE_ID, CONTRIBUTION_PROJECT_TYPE_DISPLAY_NAME, false, true);

        addVariableDefinition(ContributionProjectTypeConstants.CONTRIBUTE_LOCAL_BRANCH_NAME, "Name of local branch", false);
        addVariableDefinition(ContributionProjectTypeConstants.CONTRIBUTE_TO_BRANCH_VARIABLE_NAME, "Branch where the contribution has to be pushed", true);
    }
}
