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
package org.eclipse.che.plugin.pullrequest.client.vcs.hosting;

import javax.validation.constraints.NotNull;

/**
 * Exception raised when a pull request already exists for a branch.
 *
 * @author Kevin Pollet
 */
public class PullRequestAlreadyExistsException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs an instance of {@link PullRequestAlreadyExistsException}.
     *
     * @param headBranch
     *         the head branch name.
     */
    public PullRequestAlreadyExistsException(@NotNull final String headBranch) {
        super("A pull request for " + headBranch + " already exists");
    }
}
