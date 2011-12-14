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

import org.apache.sqoop.core.ErrorCode;

public enum ConnectorError implements ErrorCode {

  /** An unknown error has occurred. */
  CONN_0000("An unknown error has occurred"),

  /** The system was not able to initialize the configured connectors. */
  CONN_0001("Unable to initialize connectors"),

  /** No connectors were found in the system. */
  CONN_0002("No connectors were found in the system"),

  /** A problem was encountered while loading the connector configuration. */
  CONN_0003("Failed to load connector configuration"),

  /** A connector configuration file did not include the provider class name.*/
  CONN_0004("Connector configuration did not include provider class name"),

  /** An exception occurred while attempting to instantiate the connector. */
  CONN_0005("Failed to instantiate connector class");

  private final String message;

  private ConnectorError(String message) {
    this.message = message;
  }

  public String getCode() {
    return name();
  }

  public String getMessage() {
    return message;
  }
}
