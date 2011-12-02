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

import org.junit.Assert;
import org.junit.Test;

public class TestConfiguration {

  @Test
  public void testConfigurationInitFailure() {
    // Unset any configuration dir if it is set by another test
    System.getProperties().remove(ConfigurationConstants.SYSPROP_CONFIG_DIR);
    try {
      SqoopConfiguration.initialize();
    } catch (Exception ex) {
      Assert.assertTrue(ex instanceof SqoopException);
      Assert.assertSame(((SqoopException) ex).getErrorCode(),
          CoreError.CORE_0001);
    }
  }

  @Test
  public void testConfigurationInitSuccess() throws Exception {
    TestUtils.setupTestConfiguration(null);
    SqoopConfiguration.initialize();
  }
}
