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
package org.apache.sqoop.core;

import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;


public final class RepositoryContext {

  private static final Logger LOG = Logger.getLogger(RepositoryContext.class);

  private final Context context;
  private final String provider;
  private final boolean createSchema;
  private final String jdbcConnectUrl;
  private final String jdbcDriverClass;
  private final String jdbcUserName;
  private final char[] jdbcPassword;
  private final Properties jdbcConnectionProperties;

  public RepositoryContext(Context context) {
    this.context = context;

    provider = context.getString(
        ConfigurationConstants.SYSCFG_REPO_PROVIDER);

    createSchema = context.getBoolean(
        ConfigurationConstants.SYSCFG_REPO_CREATE_SCHEMA);

    jdbcConnectUrl = context.getString(
        ConfigurationConstants.SYSCFG_REPO_JDBC_URL);

    jdbcDriverClass = context.getString(
        ConfigurationConstants.SYSCFG_REPO_JDBC_DRIVER);

    jdbcUserName = context.getString(
        ConfigurationConstants.SYSCFG_REPO_JDBC_USER);

    String password = context.getString(
        ConfigurationConstants.SYSCFG_REPO_JDBC_PASSWORD);

    if (password != null) {
      jdbcPassword = password.toCharArray();
    } else {
      jdbcPassword = null;
    }

    jdbcConnectionProperties = new Properties();

    Map<String, String> params = context.getNestedProperties(
        ConfigurationConstants.PREFIX_SYSCFG_REPO_JDBC_PROPERTIES);
    for (String key : params.keySet()) {
      jdbcConnectionProperties.setProperty(key, params.get(key));
    }

    if (LOG.isInfoEnabled()) {
      StringBuilder sb = new StringBuilder("[repo-ctx] ");
      sb.append("provider=").append(provider).append(", ");
      sb.append("create-schema=").append(createSchema).append(", ");
      sb.append("conn-url=").append(jdbcConnectUrl).append(", ");
      sb.append("driver=").append(jdbcDriverClass).append(", ");
      sb.append("user=").append(jdbcUserName).append(", ");
      sb.append("password=").append("*****").append(", ");
      sb.append("jdbc-props={");
      boolean first = true;
      for (String key : params.keySet()) {
        if (first) {
          first = false;
        } else {
          sb.append(", ");
        }
        sb.append(key).append("=");
        if (key.equalsIgnoreCase("password")) {
          sb.append("*****");
        } else {
          sb.append(params.get(key));
        }
      }
      sb.append("}");

      LOG.info(sb.toString());
    }
  }

  public String getProviderClass() {
    return provider;
  }

  public String getJdbcConnectionUrl() {
    return jdbcConnectUrl;
  }

  public String getJdbcDriverClass() {
    return jdbcDriverClass;
  }

  public String getJdbcUserName() {
    return jdbcUserName;
  }

  public boolean shouldCreateSchema() {
    return createSchema;
  }

  public char[] getJdbcPassword() {
    return jdbcPassword;
  }

  public Properties getJdbcConnectionProperties() {
    Properties props = new Properties();
    props.putAll(jdbcConnectionProperties);

    return props;
  }
}
