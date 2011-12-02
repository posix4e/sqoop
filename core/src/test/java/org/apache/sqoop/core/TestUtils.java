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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;

public class TestUtils {

  private static final Logger LOG = Logger.getLogger(TestUtils.class);

  public static final String NEWLINE =
      System.getProperty("line.separator", "\n");

  /**
   * A helper method that creates a temporary directory, populates it with
   * bootstrap and logging configuration and sets it up as the system config
   * directory. This is useful for running tests without necessarily having to
   * create an installation layout.
   *
   * @param config any properties that you would like to set in the system
   * @throws Exception
   */
  public static void setupTestConfiguration(Properties config) throws Exception
  {
    File tempDir = null;
    File targetDir = new File("target");
    if (targetDir.exists() && targetDir.isDirectory()) {
      tempDir = targetDir;
    } else {
      tempDir = new File(System.getProperty("java.io.tmpdir"));
    }

    File tempFile = File.createTempFile("test", "config", tempDir);
    String tempConfigDirPath = tempFile.getCanonicalPath() + ".dir/config";
    if (!tempFile.delete()) {
      throw new Exception("Unable to delete tempfile: " + tempFile);
    }

    File tempConfigDir = new File(tempConfigDirPath);
    if (!tempConfigDir.mkdirs()) {
      throw new Exception("Unable to create temp config dir: "
              + tempConfigDirPath);
    }

    File bootconfigFile = new File(tempConfigDir,
        ConfigurationConstants.FILENAME_BOOTCFG_FILE);

    if (!bootconfigFile.createNewFile()) {
      throw new Exception("Unable to create config file: " + bootconfigFile);
    }

    BufferedWriter bootconfigWriter = null;
    try {
      bootconfigWriter = new BufferedWriter(new FileWriter(bootconfigFile));

      bootconfigWriter.write("sqoop.config.provider = "
            + PropertiesConfigurationProvider.class.getCanonicalName()
            + NEWLINE);

      bootconfigWriter.flush();
    } finally {
      if (bootconfigWriter != null) {
        try {
          bootconfigWriter.close();
        } catch (IOException ex) {
          LOG.error("Failed to close config file writer", ex);
        }
      }
    }

    File sysconfigFile = new File(tempConfigDir,
        PropertiesConfigurationProvider.CONFIG_FILENAME);

    BufferedWriter sysconfigWriter = null;
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(
          ClassLoader.getSystemResourceAsStream("test_config.properties")));

      sysconfigWriter = new BufferedWriter(new FileWriter(sysconfigFile));

      String nextLine = null;
      while ((nextLine = reader.readLine()) != null) {
        sysconfigWriter.write(nextLine + NEWLINE);
      }

      sysconfigWriter.flush();
    } finally {
      if (sysconfigWriter != null) {
        try {
          sysconfigWriter.close();
        } catch (IOException ex) {
          LOG.error("Failed to close log config file writer", ex);
        }
      }

      if (reader != null) {
        try {
          reader.close();
        } catch (IOException ex) {
          LOG.error("Failed to close log config reader", ex);
        }
      }
    }

    System.setProperty(ConfigurationConstants.SYSPROP_CONFIG_DIR,
        tempConfigDirPath);
  }


  private TestUtils() {
    // Disable explicit object creation
  }
}
