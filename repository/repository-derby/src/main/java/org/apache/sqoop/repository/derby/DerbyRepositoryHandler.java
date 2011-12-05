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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.sqoop.core.SqoopException;
import org.apache.sqoop.repository.JdbcRepositoryContext;
import org.apache.sqoop.repository.JdbcRepositoryHandler;
import org.apache.sqoop.repository.Repository;

public class DerbyRepositoryHandler implements JdbcRepositoryHandler {

  private static final Logger LOG =
      Logger.getLogger(DerbyRepositoryHandler.class);

  private static final String SCHEMA_SQOOP = "SQOOP";

  private static final String QUERY_SYSSCHEMA_SQOOP =
      "SELECT SCHEMAID FROM SYS.SYSSCHEMAS WHERE SCHEMANAME = '"
          + SCHEMA_SQOOP + "'";

  private JdbcRepositoryContext repoContext;
  private DataSource dataSource;

  @Override
  public synchronized void initialize(DataSource dataSource,
      JdbcRepositoryContext ctx) {
    if (LOG.isTraceEnabled()) {
      LOG.trace("DerbyRepositoryHandler begin initialization");
    }

    repoContext = ctx;

    if (repoContext.shouldCreateSchema()) {
      if (!schemaExists()) {
        createSchema();
      }
    }

    LOG.info("DerbyRepositoryHandler initialized.");
  }

  @Override
  public synchronized Repository getRepository() {
    // TODO Auto-generated method stub
    return null;
  }


  private void createSchema() {
    // TODO implement this
  }


  private boolean schemaExists() {
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
      try {
        connection.rollback();
      } catch (SQLException ex2) {
        LOG.error("Unable to rollback transaction", ex2);
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



}
