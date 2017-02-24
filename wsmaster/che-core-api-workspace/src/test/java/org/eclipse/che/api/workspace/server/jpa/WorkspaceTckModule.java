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
package org.eclipse.che.api.workspace.server.jpa;

import com.google.inject.TypeLiteral;

import org.eclipse.che.account.spi.AccountImpl;
import org.eclipse.che.api.machine.server.model.impl.CommandImpl;
import org.eclipse.che.api.machine.server.model.impl.SnapshotImpl;
import org.eclipse.che.api.machine.server.recipe.RecipeImpl;
import org.eclipse.che.api.workspace.server.model.impl.EnvironmentImpl;
import org.eclipse.che.api.workspace.server.model.impl.EnvironmentRecipeImpl;
import org.eclipse.che.api.workspace.server.model.impl.ExtendedMachineImpl;
import org.eclipse.che.api.workspace.server.model.impl.ProjectConfigImpl;
import org.eclipse.che.api.workspace.server.model.impl.ServerConf2Impl;
import org.eclipse.che.api.workspace.server.model.impl.SourceStorageImpl;
import org.eclipse.che.api.workspace.server.model.impl.WorkspaceConfigImpl;
import org.eclipse.che.api.workspace.server.model.impl.WorkspaceImpl;
import org.eclipse.che.api.workspace.server.model.impl.stack.StackImpl;
import org.eclipse.che.api.workspace.server.spi.StackDao;
import org.eclipse.che.api.workspace.server.spi.WorkspaceDao;
import org.eclipse.che.commons.test.db.H2DBTestServer;
import org.eclipse.che.commons.test.db.H2JpaCleaner;
import org.eclipse.che.commons.test.db.PersistTestModuleBuilder;
import org.eclipse.che.commons.test.tck.TckModule;
import org.eclipse.che.commons.test.tck.TckResourcesCleaner;
import org.eclipse.che.commons.test.tck.repository.JpaTckRepository;
import org.eclipse.che.commons.test.tck.repository.TckRepository;
import org.eclipse.che.commons.test.tck.repository.TckRepositoryException;
import org.eclipse.che.core.db.DBInitializer;
import org.eclipse.che.core.db.h2.jpa.eclipselink.H2ExceptionHandler;
import org.eclipse.che.core.db.schema.SchemaInitializer;
import org.eclipse.che.core.db.schema.impl.flyway.FlywaySchemaInitializer;
import org.h2.Driver;

import java.util.Collection;

/**
 * @author Yevhenii Voevodin
 */
public class WorkspaceTckModule extends TckModule {

    @Override
    protected void configure() {
        H2DBTestServer server = H2DBTestServer.startDefault();
        install(new PersistTestModuleBuilder().setDriver(Driver.class)
                                              .runningOn(server)
                                              .addEntityClasses(AccountImpl.class,
                                                                WorkspaceImpl.class,
                                                                WorkspaceConfigImpl.class,
                                                                ProjectConfigImpl.class,
                                                                EnvironmentImpl.class,
                                                                EnvironmentRecipeImpl.class,
                                                                ExtendedMachineImpl.class,
                                                                SourceStorageImpl.class,
                                                                ServerConf2Impl.class,
                                                                StackImpl.class,
                                                                CommandImpl.class,
                                                                SnapshotImpl.class,
                                                                RecipeImpl.class)
                                              .addEntityClass("org.eclipse.che.api.workspace.server.model.impl.ProjectConfigImpl$Attribute")
                                              .setExceptionHandler(H2ExceptionHandler.class)
                                              .build());
        bind(DBInitializer.class).asEagerSingleton();
        bind(SchemaInitializer.class).toInstance(new FlywaySchemaInitializer(server.getDataSource(), "che-schema"));
        bind(TckResourcesCleaner.class).toInstance(new H2JpaCleaner(server));

        bind(new TypeLiteral<TckRepository<AccountImpl>>() {}).toInstance(new JpaTckRepository<>(AccountImpl.class));
        bind(new TypeLiteral<TckRepository<WorkspaceImpl>>() {}).toInstance(new WorkspaceRepository());
        bind(new TypeLiteral<TckRepository<StackImpl>>() {}).toInstance(new StackRepository());

        bind(WorkspaceDao.class).to(JpaWorkspaceDao.class);
        bind(StackDao.class).to(JpaStackDao.class);
    }

    private static class WorkspaceRepository extends JpaTckRepository<WorkspaceImpl> {
        public WorkspaceRepository() { super(WorkspaceImpl.class); }

        @Override
        public void createAll(Collection<? extends WorkspaceImpl> entities) throws TckRepositoryException {
            for (WorkspaceImpl entity : entities) {
                entity.getConfig().getProjects().forEach(ProjectConfigImpl::prePersistAttributes);
            }
            super.createAll(entities);
        }
    }

    private static class StackRepository extends JpaTckRepository<StackImpl> {
        public StackRepository() { super(StackImpl.class); }

        @Override
        public void createAll(Collection<? extends StackImpl> entities) throws TckRepositoryException {
            for (StackImpl stack : entities) {
                stack.getWorkspaceConfig().getProjects().forEach(ProjectConfigImpl::prePersistAttributes);
            }
            super.createAll(entities);
        }
    }
}
