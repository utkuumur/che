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

import org.eclipse.che.commons.test.tck.JpaCleaner;
import org.h2.tools.RunScript;

import javax.sql.DataSource;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Yevhenii Voevodin
 */
public class H2JpaCleaner extends JpaCleaner {

    private final DataSource dataSource;

    private H2DBTestServer server;

    public H2JpaCleaner(H2DBTestServer server) {
        this(server.getDataSource());
        this.server = server;
    }

    public H2JpaCleaner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @deprecated use {@link H2JpaCleaner(H2DBTestServer)} instead.
     */
    @Deprecated
    public H2JpaCleaner() {
        this(H2TestHelper.inMemoryDefault());
    }

    @Override
    public void clean() {
        super.clean();
        if (server != null) {
            server.shutdown();
        } else {
            try (Connection conn = dataSource.getConnection()) {
                RunScript.execute(conn, new StringReader("SHUTDOWN"));
            } catch (SQLException x) {
                throw new RuntimeException(x.getMessage(), x);
            }
        }
    }
}
