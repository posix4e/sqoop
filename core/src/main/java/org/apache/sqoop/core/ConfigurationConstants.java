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

public final class ConfigurationConstants {

  /**
   * All configuration keys are prefixed with this:
   * <tt>org.apache.sqoop.</tt>
   */
  public static final String PREFIX_GLOBAL_CONFIG = "org.apache.sqoop.";

  /**
   * All logging related configuration is prefixed with this:
   * <tt>org.apache.sqoop.log4j.</tt>
   */
  public static final String PREFIX_LOG_CONFIG = PREFIX_GLOBAL_CONFIG
      + "log4j.";

  /**
   * All repository related configuration is prefixed with this:
   * <tt>org.apache.sqoop.repository.</tt>
   */
  public static final String PREFIX_REPO_CONFIG = PREFIX_GLOBAL_CONFIG
      + "repository.";

  /**
   * The system property that must be set for specifying the system
   * configuration directory: <tt>sqoop.config.dir</tt>.
   */
  public static final String SYSPROP_CONFIG_DIR = "sqoop.config.dir";

  /**
   * Bootstrap configuration property that specifies the system configuration
   * provider: <tt>sqoop.config.provider</tt>.
   */
  public static final String BOOTCFG_CONFIG_PROVIDER = "sqoop.config.provider";

  /**
   * Filename for the bootstrap configuration file:
   * <tt>sqoop_bootstrap.properties</tt>.
   */
  public static final String FILENAME_BOOTCFG_FILE =
      "sqoop_bootstrap.properties";

  /**
   * Class name of the repository implementation specified by:
   * <tt>org.apache.sqoop.repository.provider</tt>
   */
  public static final String SYSCFG_REPO_PROVIDER = PREFIX_REPO_CONFIG
      + "provider";

  /**
   * Indicates if the repository should create the schema objects as necessary,
   * specified as a boolean value for the key:
   * <tt>org.apache.sqoop.repository.create.schema</tt>
   */
  public static final String SYSCFG_REPO_CREATE_SCHEMA = PREFIX_REPO_CONFIG
      + "create.schema";

  /**
   * JDBC connection URL specified by:
   * <tt>org.apache.sqoop.repository.jdbc.url</tt>
   */
  public static final String SYSCFG_REPO_JDBC_URL = PREFIX_REPO_CONFIG
      + "jdbc.url";

  /**
   * JDBC driver to be used, specified by:
   * <tt>org.apache.sqoop.repository.jdbc.driver</tt>
   */
  public static final String SYSCFG_REPO_JDBC_DRIVER = PREFIX_REPO_CONFIG
      + "jdbc.driver";

  /**
   * JDBC connection user name, specified by:
   * <tt>org.apache.sqoop.repository.jdbc.user</tt>
   */
  public static final String SYSCFG_REPO_JDBC_USER = PREFIX_REPO_CONFIG
      + "jdbc.user";

  /**
   * JDBC connection password, specified by:
   * <tt>org.apache.sqoop.repository.jdbc.password</tt>
   */
  public static final String SYSCFG_REPO_JDBC_PASSWORD = PREFIX_REPO_CONFIG
      + "jdbc.password";

  /**
   * Prefix that is used to provide any JDBC specific properties for the
   * system. Configuration keys which start with this prefix will be stripped
   * of the prefix and used as regular properties for JDBC connection
   * initialization.
   */
  public static final String PREFIX_SYSCFG_REPO_JDBC_PROPERTIES =
      PREFIX_REPO_CONFIG + "jdbc.properties.";

  private ConfigurationConstants() {
    // Disable explicit object creation
  }
}
