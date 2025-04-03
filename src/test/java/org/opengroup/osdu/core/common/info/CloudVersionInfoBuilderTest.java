/*
 * Copyright 2020-2024 Google LLC
 * Copyright 2020-2024 EPAM Systems, Inc
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.model.info.ConnectedOuterService;
import org.opengroup.osdu.core.common.model.info.FeatureFlagStateResolver.FeatureFlagState;
import org.opengroup.osdu.core.common.model.info.VersionInfo;

@RunWith(MockitoJUnitRunner.class)
public class CloudVersionInfoBuilderTest {

  private static final String TEST_BUILD_INFO_PATH = "/testdata/build-info.properties";
  private static final String TEST_GIT_INFO_PATH = "/testdata/git-info.properties";

  @InjectMocks private CloudVersionInfoBuilder versionInfoBuilder;

  @Mock private VersionInfoProperties versionInfoProperties;

  @Mock private ConnectedOuterServicesBuilder outerServicesBuilder;

  @Before
  public void setUp() throws IOException {
    when(versionInfoProperties.getBuildPropertiesPath()).thenReturn(TEST_BUILD_INFO_PATH);
    when(versionInfoProperties.getGitPropertiesPath()).thenReturn(TEST_GIT_INFO_PATH);

    versionInfoBuilder.init();
  }

  @Test
  public void buildVersionInfo() throws IOException {
    VersionInfo versionInfo = versionInfoBuilder.buildVersionInfo();

    assertNotNull(versionInfo.getGroupId());
    assertNotNull(versionInfo.getArtifactId());
    assertNotNull(versionInfo.getVersion());
    assertNotNull(versionInfo.getBuildTime());
    assertNotNull(versionInfo.getBranch());
    assertNotNull(versionInfo.getCommitId());
    assertNotNull(versionInfo.getCommitMessage());
  }

  @Test
  public void buildVersionInfo_ShouldHandleEmptyOuterServices() throws IOException {
    List<ConnectedOuterService> outerServices =
        versionInfoBuilder.buildVersionInfo().getConnectedOuterServices();

    assertNotNull(outerServices);
    Assert.assertTrue(outerServices.isEmpty());
  }

  @Test
  public void buildVersionInfoShouldHandleEmptyFFResolvers() throws IOException {
    List<FeatureFlagState> featureFlagStates = versionInfoBuilder.buildVersionInfo().getFeatureFlagStates();

    assertNotNull(featureFlagStates);
    assertTrue(featureFlagStates.isEmpty());
  }
}
