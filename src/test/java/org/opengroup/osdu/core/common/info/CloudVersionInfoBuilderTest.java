/*
 * Copyright 2021 Google LLC
 * Copyright 2021 EPAM Systems, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opengroup.osdu.core.common.info;

import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.model.info.VersionInfo;

@RunWith(MockitoJUnitRunner.class)
public class CloudVersionInfoBuilderTest {

  private static final String TEST_BUILD_INFO_PATH = "/testdata/build-info.properties";
  private static final String TEST_GIT_INFO_PATH = "/testdata/git-info.properties";

  @InjectMocks
  private CloudVersionInfoBuilder versionInfoBuilder;

  @Mock
  private VersionInfoProperties versionInfoProperties;

  @Before
  public void setUp() {
    when(versionInfoProperties.getBuildPropertiesPath()).thenReturn(TEST_BUILD_INFO_PATH);
    when(versionInfoProperties.getGitPropertiesPath()).thenReturn(TEST_GIT_INFO_PATH);
  }

  @Test
  public void buildVersionInfo() throws IOException {
    VersionInfo versionInfo = versionInfoBuilder.buildVersionInfo();

    Assert.assertNotNull(versionInfo.getGroupId());
    Assert.assertNotNull(versionInfo.getArtifactId());
    Assert.assertNotNull(versionInfo.getVersion());
    Assert.assertNotNull(versionInfo.getBuildTime());
    Assert.assertNotNull(versionInfo.getBranch());
    Assert.assertNotNull(versionInfo.getCommitId());
    Assert.assertNotNull(versionInfo.getCommitMessage());
  }
}
