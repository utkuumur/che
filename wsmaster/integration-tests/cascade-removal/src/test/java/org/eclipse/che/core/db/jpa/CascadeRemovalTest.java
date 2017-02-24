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
package org.eclipse.che.core.db.jpa;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.google.inject.name.Names;

import org.eclipse.che.account.api.AccountManager;
import org.eclipse.che.account.api.AccountModule;
import org.eclipse.che.account.event.BeforeAccountRemovedEvent;
import org.eclipse.che.account.spi.AccountImpl;
import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.core.notification.EventService;
import org.eclipse.che.api.machine.server.jpa.MachineJpaModule;
import org.eclipse.che.api.machine.server.model.impl.CommandImpl;
import org.eclipse.che.api.machine.server.model.impl.MachineSourceImpl;
import org.eclipse.che.api.machine.server.model.impl.SnapshotImpl;
import org.eclipse.che.api.machine.server.recipe.RecipeImpl;
import org.eclipse.che.api.machine.server.spi.SnapshotDao;
import org.eclipse.che.api.ssh.server.jpa.JpaSshDao.RemoveSshKeysBeforeUserRemovedEventSubscriber;
import org.eclipse.che.api.ssh.server.jpa.SshJpaModule;
import org.eclipse.che.api.ssh.server.model.impl.SshPairImpl;
import org.eclipse.che.api.ssh.server.spi.SshDao;
import org.eclipse.che.api.user.server.event.BeforeUserRemovedEvent;
import org.eclipse.che.api.user.server.jpa.JpaPreferenceDao.RemovePreferencesBeforeUserRemovedEventSubscriber;
import org.eclipse.che.api.user.server.jpa.JpaProfileDao.RemoveProfileBeforeUserRemovedEventSubscriber;
import org.eclipse.che.api.user.server.jpa.PreferenceEntity;
import org.eclipse.che.api.user.server.jpa.UserJpaModule;
import org.eclipse.che.api.user.server.model.impl.ProfileImpl;
import org.eclipse.che.api.user.server.model.impl.UserImpl;
import org.eclipse.che.api.user.server.spi.PreferenceDao;
import org.eclipse.che.api.user.server.spi.ProfileDao;
import org.eclipse.che.api.user.server.spi.UserDao;
import org.eclipse.che.api.workspace.server.WorkspaceManager;
import org.eclipse.che.api.workspace.server.WorkspaceRuntimes;
import org.eclipse.che.api.workspace.server.WorkspaceSharedPool;
import org.eclipse.che.api.workspace.server.event.BeforeWorkspaceRemovedEvent;
import org.eclipse.che.api.workspace.server.jpa.JpaWorkspaceDao.RemoveSnapshotsBeforeWorkspaceRemovedEventSubscriber;
import org.eclipse.che.api.workspace.server.jpa.JpaWorkspaceDao.RemoveWorkspaceBeforeAccountRemovedEventSubscriber;
import org.eclipse.che.api.workspace.server.jpa.WorkspaceJpaModule;
import org.eclipse.che.api.workspace.server.model.impl.EnvironmentImpl;
import org.eclipse.che.api.workspace.server.model.impl.EnvironmentRecipeImpl;
import org.eclipse.che.api.workspace.server.model.impl.ExtendedMachineImpl;
import org.eclipse.che.api.workspace.server.model.impl.ProjectConfigImpl;
import org.eclipse.che.api.workspace.server.model.impl.ServerConf2Impl;
import org.eclipse.che.api.workspace.server.model.impl.SourceStorageImpl;
import org.eclipse.che.api.workspace.server.model.impl.WorkspaceConfigImpl;
import org.eclipse.che.api.workspace.server.model.impl.WorkspaceImpl;
import org.eclipse.che.api.workspace.server.model.impl.stack.StackImpl;
import org.eclipse.che.api.workspace.server.spi.WorkspaceDao;
import org.eclipse.che.commons.test.db.H2DBTestServer;
import org.eclipse.che.commons.test.db.PersistTestModuleBuilder;
import org.eclipse.che.core.db.DBInitializer;
import org.eclipse.che.core.db.cascade.CascadeEventSubscriber;
import org.eclipse.che.core.db.cascade.event.CascadeEvent;
import org.eclipse.che.core.db.h2.jpa.eclipselink.H2ExceptionHandler;
import org.eclipse.che.core.db.schema.SchemaInitializer;
import org.eclipse.che.core.db.schema.impl.flyway.FlywaySchemaInitializer;
import org.eclipse.che.inject.lifecycle.InitModule;
import org.h2.Driver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.persistence.EntityManagerFactory;
import java.util.Map;
import java.util.concurrent.Callable;

import static org.eclipse.che.core.db.jpa.TestObjectsFactory.createPreferences;
import static org.eclipse.che.core.db.jpa.TestObjectsFactory.createProfile;
import static org.eclipse.che.core.db.jpa.TestObjectsFactory.createSnapshot;
import static org.eclipse.che.core.db.jpa.TestObjectsFactory.createSshPair;
import static org.eclipse.che.core.db.jpa.TestObjectsFactory.createUser;
import static org.eclipse.che.core.db.jpa.TestObjectsFactory.createWorkspace;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * Tests top-level entities cascade removals.
 *
 * @author Yevhenii Voevodin
 */
public class CascadeRemovalTest {

    private Injector      injector;
    private EventService  eventService;
    private PreferenceDao preferenceDao;
    private UserDao       userDao;
    private ProfileDao    profileDao;
    private WorkspaceDao  workspaceDao;
    private SnapshotDao   snapshotDao;
    private SshDao        sshDao;

    /** User is a root of dependency tree. */
    private UserImpl user;

    /** Profile depends on user. */
    private ProfileImpl profile;

    /** Preferences depend on user. */
    private Map<String, String> preferences;

    /** Workspaces depend on user. */
    private WorkspaceImpl workspace1;
    private WorkspaceImpl workspace2;

    /** SshPairs depend on user. */
    private SshPairImpl sshPair1;
    private SshPairImpl sshPair2;

    /** Snapshots depend on workspace. */
    private SnapshotImpl snapshot1;
    private SnapshotImpl snapshot2;
    private SnapshotImpl snapshot3;
    private SnapshotImpl snapshot4;

    private H2DBTestServer server;

    @BeforeMethod
    public void setUp() throws Exception {
        server = H2DBTestServer.startDefault();
        injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                install(new PersistTestModuleBuilder().setDriver(Driver.class)
                                                      .runningOn(server)
                                                      .addEntityClasses(AccountImpl.class,
                                                                        UserImpl.class,
                                                                        ProfileImpl.class,
                                                                        PreferenceEntity.class,
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
                                                                        RecipeImpl.class,
                                                                        SshPairImpl.class)
                                                      .addEntityClass("org.eclipse.che.api.workspace.server.model.impl.ProjectConfigImpl$Attribute")
                                                      .setExceptionHandler(H2ExceptionHandler.class)
                                                      .build());
                bind(EventService.class).in(Singleton.class);
                install(new InitModule(PostConstruct.class));
                bind(SchemaInitializer.class).toInstance(new FlywaySchemaInitializer(server.getDataSource(), "che-schema"));
                bind(DBInitializer.class).asEagerSingleton();
                install(new UserJpaModule());
                install(new AccountModule());
                install(new SshJpaModule());
                install(new WorkspaceJpaModule());
                install(new MachineJpaModule());
                bind(WorkspaceManager.class);
                final WorkspaceRuntimes wR = mock(WorkspaceRuntimes.class);
                when(wR.hasRuntime(anyString())).thenReturn(false);
                bind(WorkspaceRuntimes.class).toInstance(wR);
                bind(AccountManager.class);
                bind(Boolean.class).annotatedWith(Names.named("che.workspace.auto_snapshot")).toInstance(false);
                bind(Boolean.class).annotatedWith(Names.named("che.workspace.auto_restore")).toInstance(false);
                bind(WorkspaceSharedPool.class).toInstance(new WorkspaceSharedPool("cached", null, null));
            }
        });

        eventService = injector.getInstance(EventService.class);
        userDao = injector.getInstance(UserDao.class);
        preferenceDao = injector.getInstance(PreferenceDao.class);
        profileDao = injector.getInstance(ProfileDao.class);
        sshDao = injector.getInstance(SshDao.class);
        snapshotDao = injector.getInstance(SnapshotDao.class);
        workspaceDao = injector.getInstance(WorkspaceDao.class);
    }

    @AfterMethod
    public void cleanup() {
        injector.getInstance(EntityManagerFactory.class).close();
        server.shutdown();
    }

    @Test
    public void shouldDeleteAllTheEntitiesWhenUserIsDeleted() throws Exception {
        createTestData();

        // Remove the user, all entries must be removed along with the user
        userDao.remove(user.getId());

        // Check all the entities are removed
        assertNull(notFoundToNull(() -> userDao.getById(user.getId())));
        assertNull(notFoundToNull(() -> profileDao.getById(user.getId())));
        assertTrue(preferenceDao.getPreferences(user.getId()).isEmpty());
        assertTrue(sshDao.get(user.getId()).isEmpty());
        assertTrue(workspaceDao.getByNamespace(user.getId()).isEmpty());
        assertTrue(snapshotDao.findSnapshots(workspace1.getId()).isEmpty());
        assertTrue(snapshotDao.findSnapshots(workspace2.getId()).isEmpty());
    }

    @Test(dataProvider = "beforeRemoveRollbackActions")
    public void shouldRollbackTransactionWhenFailedToRemoveAnyOfEntries(
            Class<CascadeEventSubscriber<CascadeEvent>> subscriberClass,
            Class<CascadeEvent> eventClass) throws Exception {
        createTestData();
        eventService.unsubscribe(injector.getInstance(subscriberClass), eventClass);

        // Remove the user, all entries must be rolled back after fail
        try {
            userDao.remove(user.getId());
            fail("UserDao#remove had to throw exception");
        } catch (Exception ignored) {
        }

        // Check all the data rolled back
        assertNotNull(userDao.getById(user.getId()));
        assertNotNull(profileDao.getById(user.getId()));
        assertFalse(preferenceDao.getPreferences(user.getId()).isEmpty());
        assertFalse(sshDao.get(user.getId()).isEmpty());
        assertFalse(workspaceDao.getByNamespace(user.getName()).isEmpty());
        assertFalse(snapshotDao.findSnapshots(workspace1.getId()).isEmpty());
        assertFalse(snapshotDao.findSnapshots(workspace2.getId()).isEmpty());
        wipeTestData();
    }

    @DataProvider(name = "beforeRemoveRollbackActions")
    public Object[][] beforeRemoveActions() {
        return new Class[][] {
                {RemovePreferencesBeforeUserRemovedEventSubscriber.class, BeforeUserRemovedEvent.class},
                {RemoveProfileBeforeUserRemovedEventSubscriber.class, BeforeUserRemovedEvent.class},
                {RemoveWorkspaceBeforeAccountRemovedEventSubscriber.class, BeforeAccountRemovedEvent.class},
                {RemoveSnapshotsBeforeWorkspaceRemovedEventSubscriber.class, BeforeWorkspaceRemovedEvent.class},
                {RemoveSshKeysBeforeUserRemovedEventSubscriber.class, BeforeUserRemovedEvent.class},
                };
    }

    private void createTestData() throws ConflictException, ServerException {
        userDao.create(user = createUser("bobby"));

        profileDao.create(profile = createProfile(user.getId()));

        preferenceDao.setPreferences(user.getId(), preferences = createPreferences());

        workspaceDao.create(workspace1 = createWorkspace("workspace1", user.getAccount()));
        workspaceDao.create(workspace2 = createWorkspace("workspace2", user.getAccount()));

        sshDao.create(sshPair1 = createSshPair(user.getId(), "service", "name1"));
        sshDao.create(sshPair2 = createSshPair(user.getId(), "service", "name2"));

        snapshotDao.saveSnapshot(snapshot1 = createSnapshot("snapshot1", workspace1.getId()));
        snapshotDao.saveSnapshot(snapshot2 = createSnapshot("snapshot2", workspace1.getId()));
        snapshotDao.saveSnapshot(snapshot3 = createSnapshot("snapshot3", workspace2.getId()));
        snapshotDao.saveSnapshot(snapshot4 = createSnapshot("snapshot4", workspace2.getId()));
    }

    private void wipeTestData() throws ConflictException, ServerException, NotFoundException {
        snapshotDao.removeSnapshot(snapshot1.getId());
        snapshotDao.removeSnapshot(snapshot2.getId());
        snapshotDao.removeSnapshot(snapshot3.getId());
        snapshotDao.removeSnapshot(snapshot4.getId());

        sshDao.remove(sshPair1.getOwner(), sshPair1.getService(), sshPair1.getName());
        sshDao.remove(sshPair2.getOwner(), sshPair2.getService(), sshPair2.getName());

        workspaceDao.remove(workspace1.getId());
        workspaceDao.remove(workspace2.getId());

        preferenceDao.remove(user.getId());

        profileDao.remove(user.getId());

        userDao.remove(user.getId());
    }

    private static <T> T notFoundToNull(Callable<T> action) throws Exception {
        try {
            return action.call();
        } catch (NotFoundException x) {
            return null;
        }
    }
}
