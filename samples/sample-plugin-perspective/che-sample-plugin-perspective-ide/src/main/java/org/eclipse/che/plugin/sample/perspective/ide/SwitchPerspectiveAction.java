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
package org.eclipse.che.plugin.sample.perspective.ide;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.CustomComponentAction;
import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.ide.api.parts.PerspectiveManager;
import org.eclipse.che.ide.ui.radiobuttongroup.RadioButtonGroup;

import static org.eclipse.che.ide.workspace.perspectives.project.ProjectPerspective.PROJECT_PERSPECTIVE_ID;
import static org.eclipse.che.plugin.sample.perspective.ide.MyCustomPerspective.OPERATIONS_PERSPECTIVE_ID;

/**
 * Action for switching Project/Machine perspectives.
 *
 * @author Artem Zatsarynnyi
 */
@Singleton
public class SwitchPerspectiveAction extends Action implements CustomComponentAction {

    private final PerspectiveManager          perspectiveManager;

    private final RadioButtonGroup           radioButtonGroup;
    private       CustomPerspectiveResources resources;

    @Inject
    public SwitchPerspectiveAction(PerspectiveManager perspectiveManager,
                                   RadioButtonGroup radioButtonGroup,
                                   CustomPerspectiveResources customPerspectiveResources) {
        super();
        this.perspectiveManager = perspectiveManager;
        this.radioButtonGroup = radioButtonGroup;
        resources = customPerspectiveResources;

        createButtons();
    }

    private void createButtons() {
        radioButtonGroup.addButton("", "Perspective", resources.icon(),
                                   new ClickHandler() {
                                       @Override
                                       public void onClick(ClickEvent event) {
                                           perspectiveManager.setPerspectiveId(PROJECT_PERSPECTIVE_ID);
                                       }
                                   });
        radioButtonGroup.addButton("", "hgfhdgf", resources.icon(),
                                   new ClickHandler() {
                                       @Override
                                       public void onClick(ClickEvent event) {
                                           perspectiveManager.setPerspectiveId(OPERATIONS_PERSPECTIVE_ID);
                                       }
                                   });

        radioButtonGroup.selectButton(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public Widget createCustomComponent(Presentation presentation) {
        return radioButtonGroup;
    }
}
