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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.parts.PartPresenter;
import org.eclipse.che.ide.api.parts.PartStackView;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * The class offers special main panel to add tab container. The class is a wrapper of tab container view.
 *
 * @author Dmitry Shnurenko
 */
public class SampleViewImpl extends Composite implements SampleView, PartStackView {
    interface MachineInfoContainerUiBinder extends UiBinder<Widget, SampleViewImpl> {
    }

    private final static MachineInfoContainerUiBinder UI_BINDER = GWT.create(MachineInfoContainerUiBinder.class);

    @UiField
    Button button;

    @Inject
    public SampleViewImpl(CustomPerspectiveResources resources, Label unavailableLabel, NotificationManager notificationManager) {
        initWidget(UI_BINDER.createAndBindUi(this));
        setMaximized(false);
        button.addClickHandler(clickEvent -> notificationManager.notify("Central Part", "Hello world!"));
    }

    /** {@inheritDoc} */
    @Override
    public void show() {
         asWidget().setVisible(true);
    }

    /** {@inheritDoc} */
    @Override
    public void addTab(@NotNull TabItem tabItem, @NotNull PartPresenter presenter) {
        //to do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeTab(@NotNull PartPresenter partPresenter) {
        //to do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void selectTab(@NotNull PartPresenter partPresenter) {
        //to do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void setTabPositions(List<PartPresenter> partPositions) {
        //to do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus(boolean focused) {
        //to do nothing
    }

    @Override
    public void setMaximized(boolean maximized) {
        getElement().setAttribute("maximized", "" + maximized);
    }

    /** {@inheritDoc} */
    @Override
    public void updateTabItem(@NotNull PartPresenter partPresenter) {
        //to do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        //to do nothing
    }
}