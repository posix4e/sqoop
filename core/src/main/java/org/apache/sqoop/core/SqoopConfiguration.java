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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class SqoopConfiguration {

  public static final String SYSPROP_CONFIG_DIR = "sqoop.config.dir";
  public static final String FILENAME_BOOTSTRAP_CONFIG =
      "sqoop_bootstrap.properties";

  public static final String BOOTPROP_CONFIG_PROVIDER = "sqoop.config.provider";

  public static final Logger LOG = Logger.getLogger(SqoopConfiguration.class);

  private static final String PREFIX_GLOBAL_CONFIG = "org.apache.sqoop.";

  private static final String PREFIX_LOG_CONFIG = PREFIX_GLOBAL_CONFIG
      + "log4j.";

  private static File configDir = null;
  private static boolean initialized = false;
  private static ConfigurationProvider provider = null;
  private static Map<String, String> config;

  public synchronized static void initialize() {
    if (initialized) {
      LOG.warn("Attempt to reinitialize the system, ignoring");
      return;
    }

    String configDirPath = System.getProperty(SYSPROP_CONFIG_DIR);
    if (configDirPath == null || configDirPath.trim().length() == 0) {
      throw new SqoopException(CoreError.CORE_0001, "Environment variable "
          + SYSPROP_CONFIG_DIR + " is not set.");
    }

    configDir = new File(configDirPath);
    if (!configDir.exists() || !configDir.isDirectory()) {
      throw new SqoopException(CoreError.CORE_0001, configDirPath);
    }

    String bootstrapConfigFilePath = null;
    try {
      String configDirCanonicalPath = configDir.getCanonicalPath();
      bootstrapConfigFilePath = configDirCanonicalPath
              + "/" + FILENAME_BOOTSTRAP_CONFIG;

    } catch (IOException ex) {
      throw new SqoopException(CoreError.CORE_0001, configDirPath, ex);
    }

    File bootstrapConfig = new File(bootstrapConfigFilePath);
    if (!bootstrapConfig.exists() || !bootstrapConfig.isFile()
        || !bootstrapConfig.canRead()) {
      throw new SqoopException(CoreError.CORE_0002, bootstrapConfigFilePath);
    }

    Properties bootstrapProperties = new Properties();
    InputStream bootstrapPropStream = null;
    try {
      bootstrapPropStream = new FileInputStream(bootstrapConfig);
      bootstrapProperties.load(bootstrapPropStream);
    } catch (IOException ex) {
      throw new SqoopException(
          CoreError.CORE_0002, bootstrapConfigFilePath, ex);
    }

    String configProviderClassName = bootstrapProperties.getProperty(
        BOOTPROP_CONFIG_PROVIDER);

    if (configProviderClassName == null
        || configProviderClassName.trim().length() == 0) {
      throw new SqoopException(
          CoreError.CORE_0003, BOOTPROP_CONFIG_PROVIDER);
    }

    Class<?> configProviderClass = null;
    try {
      configProviderClass = Class.forName(configProviderClassName);
    } catch (ClassNotFoundException cnfe) {
      LOG.warn("Exception while trying to load configuration provider", cnfe);
    }

    if (configProviderClass == null) {
      ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
      if (ctxLoader != null) {
        try {
          configProviderClass = ctxLoader.loadClass(configProviderClassName);
        } catch (ClassNotFoundException cnfe) {
          LOG.warn("Exception while trying to load configuration provider",
              cnfe);
        }
      }
    }

    if (configProviderClass == null) {
      throw new SqoopException(CoreError.CORE_0004, configProviderClassName);
    }

    try {
      provider = (ConfigurationProvider) configProviderClass.newInstance();
    } catch (Exception ex) {
      throw new SqoopException(CoreError.CORE_0005,
          configProviderClassName, ex);
    }

    provider.initialize(configDir, bootstrapProperties);
    refreshConfiguration();
    provider.registerListener(new CoreConfigurationListener());

    initialized = true;
  }

  public synchronized static void registerListener(
      ConfigurationListener listener) {
    if (!initialized) {
      throw new SqoopException(CoreError.CORE_0007);
    }
    provider.registerListener(listener);
  }

  private synchronized static void configureLogging() {
    Properties props = new Properties();
    for (String key : config.keySet()) {
      if (key.startsWith(PREFIX_LOG_CONFIG)) {
        String logConfigKey = key.substring(PREFIX_GLOBAL_CONFIG.length());
        props.put(logConfigKey, config.get(key));
      }
    }

    PropertyConfigurator.configure(props);
  }

  private synchronized static void refreshConfiguration() {
    config = provider.getConfiguration();
    configureLogging();
  }

  public static class CoreConfigurationListener implements ConfigurationListener
  {
    @Override
    public void configurationChanged() {
      refreshConfiguration();
    }
  }
}
