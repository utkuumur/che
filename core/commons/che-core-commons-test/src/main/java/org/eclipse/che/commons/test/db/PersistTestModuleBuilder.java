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
package org.eclipse.che.commons.test.db;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.persist.jpa.JpaPersistModule;

import org.eclipse.che.commons.xml.NewElement;
import org.eclipse.che.commons.xml.XMLTree;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.ExceptionHandler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static org.eclipse.che.commons.xml.NewElement.createElement;

/**
 * Helps to build persistence.xml for test purposes.
 * If bound creates META-INF/persistence.xml with generated content.
 *
 * <p>The example of generated content is:
 * <pre>{@code
 *  // for binding
 *  new PersistTestModuleBuilder().setDriver("org.h2.Driver")
 *                                .addEntityClass(MyEntity.class)
 *                                .addEntityClass(MyEntity2.class)
 *                                .setUrl("jdbc:h2:mem:test")
 *                                .setUser("username")
 *                                .setPassword("secret")
 *                                .build();
 *
 *  // generated persistence.xml
 *  <persistence xmlns="http://java.sun.com/xml/ns/persistence"
 *               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *               xsi:schemaLocation="http://java.sun.com/xml/ns/persistence persistence_1_0.xsd" version="1.0">
 *      <persistence-unit name="test" transaction-type="RESOURCE_LOCAL">
 *          <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
 *          <class>org.eclipse.che.module.MyEntity</class>
 *          <class>org.eclipse.che.module.MyEntity2</class>
 *          <properties>
 *              <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>
 *              <property name="javax.persistence.jdbc.url" value="jdbc:h2:mem:test"/>
 *              <property name="javax.persistence.jdbc.user" value="username"/>
 *              <property name="javax.persistence.jdbc.password" value="secret"/>
 *          </properties>
 *      </persistence-unit>
 * </persistence>
 * }</pre>
 *
 * @author Yevhenii Voevodin
 */
public class PersistTestModuleBuilder {

    private final Map<String, String> properties   = new LinkedHashMap<>();
    private final Set<String>         entityFqnSet = new LinkedHashSet<>();

    private String persistenceUnit = "test";

    /**
     * Sets url, user and password equal to the values provided by server.
     */
    public PersistTestModuleBuilder runningOn(DBTestServer server) {
        setUrl(server.getUrl());
        setUser(server.getUser());
        setPassword(server.getPassword());
        return this;
    }

    /**
     * Sets the value of {@value PersistenceUnitProperties#JDBC_DRIVER} property.
     */
    public PersistTestModuleBuilder setDriver(String driver) {
        return setProperty(PersistenceUnitProperties.JDBC_DRIVER, driver);
    }

    /**
     * Sets the value of {@value PersistenceUnitProperties#JDBC_DRIVER} property,
     * the value would be equal to class fqn.
     */
    public PersistTestModuleBuilder setDriver(Class<? extends java.sql.Driver> driverClass) {
        return setDriver(driverClass.getName());
    }

    /**
     * Sets the value of {@value PersistenceUnitProperties#JDBC_URL} property.
     */
    public PersistTestModuleBuilder setUrl(String url) {
        return setProperty(PersistenceUnitProperties.JDBC_URL, url);
    }

    /**
     * Sets the value of {@value PersistenceUnitProperties#JDBC_USER} property.
     */
    public PersistTestModuleBuilder setUser(String user) {
        return setProperty(PersistenceUnitProperties.JDBC_USER, user);
    }

    /**
     * Sets the value of {@value PersistenceUnitProperties#JDBC_PASSWORD} property.
     */
    public PersistTestModuleBuilder setPassword(String password) {
        return setProperty(PersistenceUnitProperties.JDBC_PASSWORD, password);
    }

    /**
     * Sets the value of {@value PersistenceUnitProperties#EXCEPTION_HANDLER_CLASS} property.
     */
    public PersistTestModuleBuilder setExceptionHandler(Class<? extends ExceptionHandler> exHandler) {
        return setProperty(PersistenceUnitProperties.EXCEPTION_HANDLER_CLASS, exHandler.getName());
    }

    /**
     * Adds class to the listing of the entities defined by persistence unit.
     */
    public PersistTestModuleBuilder addEntityClass(Class<?> entityClass) {
        entityFqnSet.add(entityClass.getName());
        return this;
    }

    /**
     * Adds class to the listing of the entities defined by persistence unit.
     */
    public PersistTestModuleBuilder addEntityClass(String fqn) {
        entityFqnSet.add(fqn);
        return this;
    }

    /**
     * Batch add of entity classes to the persistence unit listing.
     */
    public PersistTestModuleBuilder addEntityClasses(Class<?>... entityClasses) {
        for (Class<?> entityClass : entityClasses) {
            addEntityClass(entityClass);
        }
        return this;
    }

    /**
     * Sets persistence unit custom property.
     */
    public PersistTestModuleBuilder setProperty(String name, String value) {
        if (name != null && value != null) {
            properties.put(name, value);
        }
        return this;
    }

    /**
     * Sets the the value of {@link PersistenceUnitProperties#LOGGING_LEVEL} property.
     */
    public PersistTestModuleBuilder setLogLevel(String logLevel) {
        return setProperty(PersistenceUnitProperties.LOGGING_LEVEL, logLevel);
    }

    /**
     * Sets the name of persistence unit.
     */
    public PersistTestModuleBuilder setPersistenceUnit(String persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
        return this;
    }

    /**
     * Creates persistence.xml and builds module for testing.
     */
    public Module build() {
        return new PersistTestModule();
    }

    private class PersistTestModule extends AbstractModule {
        @Override
        protected void configure() {
            JpaPersistModule persistModule = new JpaPersistModule(persistenceUnit);
            try {
                Path persistenceXmlPath = getOrCreateMetaInf().resolve("persistence.xml");
                createPersistenceXmlTree().writeTo(persistenceXmlPath);
            } catch (Exception x) {
                throw new RuntimeException(x.getMessage());
            }
            install(persistModule);
        }
    }

    private Path getOrCreateMetaInf() throws URISyntaxException, IOException {
        Path root = Paths.get(Thread.currentThread().getContextClassLoader().getResource(".").toURI());
        Path metaInf = root.resolve("META-INF");
        if (!Files.exists(metaInf)) {
            Files.createDirectory(metaInf);
        }
        return metaInf;
    }

    private XMLTree createPersistenceXmlTree() {
        NewElement unit = persistenceUnit(persistenceUnit, "RESOURCE_LOCAL");

        for (String entityClassFqn : entityFqnSet) {
            unit.appendChild(createElement("class", entityClassFqn));
        }

        if (!properties.isEmpty()) {
            NewElement propsEl = createElement("properties");
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                NewElement propertyEl = createElement("property");
                propertyEl.setAttribute("name", entry.getKey());
                propertyEl.setAttribute("value", entry.getValue());
                propsEl.appendChild(propertyEl);
            }
            unit.appendChild(propsEl);
        }

        XMLTree tree = XMLTree.from(persistenceXmlInitialContent());
        tree.getRoot().appendChild(unit);
        return tree;
    }

    private NewElement persistenceUnit(String name, String transactionType) {
        NewElement unit = createElement("persistence-unit");
        unit.setAttribute("name", name);
        unit.setAttribute("transaction-type", transactionType);
        unit.appendChild(createElement("provider", "org.eclipse.persistence.jpa.PersistenceProvider"));
        return unit;
    }

    private String persistenceXmlInitialContent() {
        return "<persistence xmlns=\"http://java.sun.com/xml/ns/persistence\"\n" +
               "             xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
               "             xsi:schemaLocation=\"http://java.sun.com/xml/ns/persistence persistence_1_0.xsd\" version=\"1.0\">\n" +
               "</persistence>\n";
    }
}
