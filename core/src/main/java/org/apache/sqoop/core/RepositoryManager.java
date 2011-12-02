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

import org.apache.log4j.Logger;

public final class RepositoryManager {

  private static final Logger LOG = Logger.getLogger(RepositoryManager.class);

  private static Repository instance;
  private static RepositoryContext repoContext;

  public synchronized static void initialize() {
    repoContext = new RepositoryContext(SqoopConfiguration.getContext());

    String repoProviderClassName = repoContext.getProviderClass();

    if (repoProviderClassName == null
        || repoProviderClassName.trim().length() == 0) {
      throw new SqoopException(CoreError.CORE_0008,
          ConfigurationConstants.SYSCFG_REPO_PROVIDER);
    }

    Class<?> rpClass = null;
    try {
      rpClass = Class.forName(repoProviderClassName);
    } catch (ClassNotFoundException ex) {
      LOG.debug("Exception while loading class: " + repoProviderClassName, ex);
    }

    if (rpClass == null) {
      // try context loader
      ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
      if (ctxLoader != null) {
        try {
          rpClass = ctxLoader.loadClass(repoProviderClassName);
        } catch (ClassNotFoundException ex) {
          LOG.debug("Exception while load class: " + repoProviderClassName, ex);
        }
      }
    }

    if (rpClass == null) {
      throw new SqoopException(CoreError.CORE_0008, repoProviderClassName);
    }

    try {
      instance = (Repository) rpClass.newInstance();
    } catch (Exception ex) {
      throw new SqoopException(CoreError.CORE_0008, repoProviderClassName, ex);
    }

    String connectUrl = repoContext.getJdbcConnectionUrl();
    if (connectUrl == null || connectUrl.trim().length() == 0) {
      throw new SqoopException(CoreError.CORE_0009);
    }

    instance.initialize(repoContext);
  }

  public synchronized static Repository getInstance() {
    return instance;
  }
}
