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
package org.apache.sqoop.connector;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.sqoop.core.ConfigurationConstants;
import org.apache.sqoop.core.SqoopException;
import org.apache.sqoop.spi.SqoopConnector;

public final class ConnectorHandler {

  private static final Logger LOG = Logger.getLogger(ConnectorHandler.class);

  private final Properties properties = new Properties();

  private final String connectorUrl;
  private final String connectorClassName;
  private final String connectorShortName;
  private final SqoopConnector connector;

  public ConnectorHandler(URL configFileUrl) {
    connectorUrl = configFileUrl.toString();
    try {
      properties.load(configFileUrl.openStream());
    } catch (IOException ex) {
      throw new SqoopException(ConnectorError.CONN_0003,
          configFileUrl.toString(), ex);
    }

    connectorClassName =
        properties.getProperty(ConfigurationConstants.CONPROP_PROVIDER_CLASS);

    if (connectorClassName == null || connectorClassName.trim().length() == 0) {
      throw new SqoopException(ConnectorError.CONN_0004,
          ConfigurationConstants.CONPROP_PROVIDER_CLASS);
    }

    Class<?> connectorClass = null;
    try {
      connectorClass = Class.forName(connectorClassName);
    } catch (ClassNotFoundException ex) {
      throw new SqoopException(ConnectorError.CONN_0005,
              connectorClassName, ex);
    }

    connectorShortName = connectorClass.getSimpleName();

    try {
      connector = (SqoopConnector) connectorClass.newInstance();
    } catch (IllegalAccessException ex) {
      throw new SqoopException(ConnectorError.CONN_0005,
              connectorClassName, ex);
    } catch (InstantiationException ex) {
      throw new SqoopException(ConnectorError.CONN_0005,
          connectorClassName, ex);
    }

    if (LOG.isInfoEnabled()) {
      LOG.info("Connector [" + connectorClassName + "] initialized.");
    }
  }

  public String toString() {
    return "{" + connectorShortName + ":" + connectorClassName
        + ":" + connectorUrl + "}";
  }

  public String getShortName() {
    return connectorShortName;
  }

  public String getCanonicalName() {
    return connectorClassName;
  }

  public String getConnectorUrl() {
    return connectorUrl;
  }
}
