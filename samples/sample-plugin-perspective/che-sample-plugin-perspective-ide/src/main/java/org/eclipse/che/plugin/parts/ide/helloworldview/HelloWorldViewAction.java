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
package org.eclipse.che.plugin.parts.ide.helloworldview;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.parts.PartStackType;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;

/**
 * Action for showing the Hello World View.
 *
 * @author Edgar Mueller
 */
@Singleton
public class HelloWorldViewAction extends Action {

    private WorkspaceAgent workspaceAgent;

    /**
     * Constructor.
     *
     * @param workspaceAgent the {@link WorkspaceAgent} that will open our sample part
     * @param helloWorldPresenter the {@link HelloWorldPresenter} displaying the view
     *
     */
    @Inject
    public HelloWorldViewAction(WorkspaceAgent workspaceAgent) {
        super("Show Hello World View");
        this.workspaceAgent = workspaceAgent;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
//        workspaceAgent.openPart(helloWorldPresenter, PartStackType.INFORMATION);
//        workspaceAgent.setActivePart(helloWorldPresenter);
    }
}
