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
package org.eclipse.che.plugin.pullrequest.client.parts.contribute;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * {@link com.google.gwt.uibinder.client.UiBinder} interface for the configure contribution dialog.
 */
@UiTemplate("ContributePartViewImpl.ui.xml")
public interface ContributePartViewUiBinder extends UiBinder<ScrollPanel, ContributePartViewImpl> {
}
