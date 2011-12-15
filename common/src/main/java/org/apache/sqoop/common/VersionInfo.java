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
package org.apache.sqoop.common;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class VersionInfo implements JsonBean {

  public static final String VERSIONS = "versions";

  private String[] versions;

  public VersionInfo(String[] versions) {
    this.versions = new String[versions.length];
    System.arraycopy(versions, 0, this.versions, 0, versions.length);
  }


  @SuppressWarnings("unchecked")
  @Override
  public JSONObject extract() {
    JSONObject result = new JSONObject();
    JSONArray versionsArray = new JSONArray();
    for (String versionEntry : versions) {
      versionsArray.add(versionEntry);
    }
    result.put(VERSIONS, versionsArray);
    return result;
  }

  @Override
  public void restore(JSONObject jsonObject) {
    JSONArray versionsArray = (JSONArray) jsonObject.get(VERSIONS);
    int size = versionsArray.size();
    this.versions = new String[size];
    for (int i = 0; i<size; i++) {
      versions[i] = (String) versionsArray.get(i);
    }
  }
}
