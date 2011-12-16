/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sqoop.repository.derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.sqoop.core.SqoopException;
import org.apache.sqoop.repository.JdbcRepositoryContext;
import org.apache.sqoop.repository.JdbcRepositoryHandler;
import org.apache.sqoop.repository.JdbcRepositoryTransactionFactory;
import org.apache.sqoop.repository.Repository;
import org.apache.sqoop.repository.model.MConnector;

public class DerbyRepositoryHandler implements JdbcRepositoryHandler {

  private static final Logger LOG =
      Logger.getLogger(DerbyRepositoryHandler.class);

  private static final String SCHEMA_SQOOP = "SQOOP";

  private static final String QUERY_SYSSCHEMA_SQOOP =
      "SELECT SCHEMAID FROM SYS.SYSSCHEMAS WHERE SCHEMANAME = '"
          + SCHEMA_SQOOP + "'";

  private static final String EMBEDDED_DERBY_DRIVER_CLASSNAME =
          "org.apache.derby.jdbc.EmbeddedDriver";


  private JdbcRepositoryContext repoContext;
  private DataSource dataSource;
  private JdbcRepositoryTransactionFactory txFactory;

  @Override
  public synchronized void initialize(JdbcRepositoryContext ctx) {
    repoContext = ctx;
    dataSource = repoContext.getDataSource();
    txFactory = repoContext.getTransactionFactory();
    LOG.info("DerbyRepositoryHandler initialized.");
  }

  @Override
  public synchronized void shutdown() {
    String driver = repoContext.getDriverClass();
    if (driver != null && driver.equals(EMBEDDED_DERBY_DRIVER_CLASSNAME)) {
      // Using embedded derby. Needs explicit shutdown
      String connectUrl = repoContext.getConnectionUrl();
      if (connectUrl.startsWith("jdbc:derby:")) {
        int index = connectUrl.indexOf(";");
        String baseUrl = null;
        if (index != -1) {
          baseUrl = connectUrl.substring(0, index+1);
        } else {
          baseUrl = connectUrl + ";";
        }
        String shutDownUrl = baseUrl + "shutdown=true";

        LOG.debug("Attempting to shutdown embedded Derby using URL: "
            + shutDownUrl);

        try {
          DriverManager.getConnection(shutDownUrl);
        } catch (SQLException ex) {
          // Shutdown for one db instance is expected to raise SQL STATE 45000
          if (ex.getErrorCode() != 45000) {
            throw new SqoopException(
                DerbyRepoError.DERBYREPO_0002, shutDownUrl, ex);
          }
          LOG.info("Embedded Derby shutdown raised SQL STATE "
              + "45000 as expected.");
        }
      } else {
        LOG.warn("Even though embedded Derby drvier was loaded, the connect "
            + "URL is of an unexpected form: " + connectUrl + ". Therfore no "
            + "attempt will be made to shutdown embedded Derby instance.");
      }

    }
  }

  public void createSchema() {
    runQuery(DerbySchemaQuery.QUERY_CREATE_SCHEMA_SQOOP);
    runQuery(DerbySchemaQuery.QUERY_CREATE_TABLE_SQ_CONNECTOR);
  }

  public boolean schemaExists() {
    Connection connection = null;
    Statement stmt = null;
    try {
      connection = dataSource.getConnection();
      stmt = connection.createStatement();
      ResultSet  rset = stmt.executeQuery(QUERY_SYSSCHEMA_SQOOP);

      if (!rset.next()) {
        LOG.warn("Schema for SQOOP does not exist");
        return false;
      }
      String sqoopSchemaId = rset.getString(1);
      LOG.debug("SQOOP schema ID: " + sqoopSchemaId);

      connection.commit();
    } catch (SQLException ex) {
      if (connection != null) {
        try {
          connection.rollback();
        } catch (SQLException ex2) {
          LOG.error("Unable to rollback transaction", ex2);
        }
      }
      throw new SqoopException(DerbyRepoError.DERBYREPO_0001, ex);
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch(SQLException ex) {
          LOG.error("Unable to  close schema lookup stmt", ex);
        }
      }
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException ex) {
          LOG.error("Unable to close connection", ex);
        }
      }
    }

    return true;
  }

  private void runQuery(String query) {
    Connection connection = null;
    Statement stmt = null;
    try {
      connection = dataSource.getConnection();
      stmt = connection.createStatement();
      if (stmt.execute(query)) {
        ResultSet rset = stmt.getResultSet();
        int count = 0;
        while (rset.next()) {
          count++;
        }
        LOG.info("QUERY(" + query + ") produced unused resultset with "
            + count + " rows");
      } else {
        int updateCount = stmt.getUpdateCount();
        LOG.info("QUERY(" + query + ") Update count: " + updateCount);
      }
      connection.commit();
    } catch (SQLException ex) {
      try {
        connection.rollback();
      } catch (SQLException ex2) {
        LOG.error("Unable to rollback transaction", ex2);
      }
      throw new SqoopException(DerbyRepoError.DERBYREPO_0003,
          query, ex);
    } finally {
      if (stmt != null) {
        try {
          stmt.close();
        } catch (SQLException ex) {
          LOG.error("Unable to close statement", ex);
        }
        if (connection != null) {
          try {
            connection.close();
          } catch (SQLException ex) {
            LOG.error("Unable to close connection", ex);
          }
        }
      }
    }
  }

  @Override
  public MConnector findConnector(String shortName, Connection conn) {
    // FIXME Auto-generated method stub
    return null;
  }
}
