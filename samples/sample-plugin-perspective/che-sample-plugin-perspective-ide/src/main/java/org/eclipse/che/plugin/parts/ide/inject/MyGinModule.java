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
package org.eclipse.che.plugin.parts.ide.inject;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.multibindings.GinMapBinder;

import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.ide.api.parts.Perspective;
import org.eclipse.che.plugin.parts.ide.helloworldview.HelloWorldView;
import org.eclipse.che.plugin.parts.ide.helloworldview.HelloWorldViewImpl;
import org.eclipse.che.plugin.parts.ide.helloworldview.SampleView;
import org.eclipse.che.plugin.parts.ide.helloworldview.SampleViewImpl;
import org.eclipse.che.plugin.parts.ide.helloworldview.ng.HelloWorldView2;
import org.eclipse.che.plugin.parts.ide.helloworldview.ng.HelloWorldViewImpl2;
import org.eclipse.che.plugin.parts.ide.helloworldview.perspective.OperationsPerspective;

import static org.eclipse.che.plugin.parts.ide.helloworldview.perspective.OperationsPerspective.OPERATIONS_PERSPECTIVE_ID;

/**
 * Gin module binding the {@link HelloWorldView} to the {@link HelloWorldViewImpl} implementation class.
 *
 * @author Edgar Mueller
 */
@ExtensionGinModule
public class MyGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(HelloWorldView.class).to(HelloWorldViewImpl.class);
        bind(HelloWorldView2.class).to(HelloWorldViewImpl2.class);
        bind(SampleView.class).to(SampleViewImpl.class);
        GinMapBinder.newMapBinder(binder(), String.class, Perspective.class)
                    .addBinding(OPERATIONS_PERSPECTIVE_ID)
                    .to(OperationsPerspective.class);
    }

}
