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
package org.eclipse.che.ide.command.editor.page.name;

import org.eclipse.che.ide.api.mvp.View;

/**
 * The view of {@link NamePage}.
 *
 * @author Artem Zatsarynnyi
 */
public interface NamePageView extends View<NamePageView.ActionDelegate> {

    /** Sets the command's name value. */
    void setCommandName(String name);

    /** The action delegate for this view. */
    interface ActionDelegate {

        /**
         * Called when command's name has been changed.
         *
         * @param name
         *         changed value of the command's name
         */
        void onNameChanged(String name);

        /** Called when testing command is requested. */
        void onCommandTest();
    }
}