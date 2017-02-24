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
package org.eclipse.che.plugin.pullrequest.client.vcs;

import org.eclipse.che.api.core.model.project.ProjectConfig;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import static org.eclipse.che.ide.ext.git.client.GitUtil.isUnderGit;

/**
 * Provider for the {@link VcsService}.
 *
 * @author Kevin Pollet
 */
public class VcsServiceProvider {
    private final GitVcsService gitVcsService;

    @Inject
    public VcsServiceProvider(@NotNull final GitVcsService gitVcsService) {
        this.gitVcsService = gitVcsService;
    }

    /**
     * Returns the {@link VcsService} implementation corresponding to the current project VCS.
     *
     * @return the {@link VcsService} implementation or {@code null} if not supported or not
     * initialized.
     */
    public VcsService getVcsService(final ProjectConfig project) {
        if (project != null) {
            if (isUnderGit(project)) {
                return gitVcsService;
            }
        }
        return null;
    }
}
