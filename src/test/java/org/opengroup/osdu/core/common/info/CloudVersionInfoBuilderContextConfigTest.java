/*
 *  Copyright 2020-2025 Google LLC
 *  Copyright 2020-2025 EPAM Systems, Inc
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.opengroup.osdu.core.common.info;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opengroup.osdu.core.common.info.CloudVersionInfoBuilderContextConfigTest.TestConfiguration;
import org.opengroup.osdu.core.common.model.info.FeatureFlagStateResolver;
import org.opengroup.osdu.core.common.model.info.FeatureFlagStateResolver.FeatureFlagState;
import org.opengroup.osdu.core.common.model.info.VersionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class CloudVersionInfoBuilderContextConfigTest {

  public static final String TEST_FEATURE_1 = "test.feature.1";
  public static final String TEST_FEATURE_2 = "test.feature.2";
  public static final String PARTITION = "osdu";
  public static final String SOURCE = "partition";

  @Autowired
  CloudVersionInfoBuilder versionInfoBuilder;

  @Configuration
  static class TestConfiguration {

    @Bean
    public CloudVersionInfoBuilder cloudVersionInfoBuilder() {
      return new CloudVersionInfoBuilder(new VersionInfoProperties());
    }

    @Bean
    public FeatureFlagStateResolver featureFlagState() {
      return new FeatureFlagStateResolver() {
        @Override
        public List<FeatureFlagState> retrieveStates() {
          return List.of(FeatureFlagState.builder()
              .name(TEST_FEATURE_1)
              .enabled(true)
              .partition(PARTITION)
              .source(SOURCE)
              .build());
        }
      };
    }

    @Bean
    public FeatureFlagStateResolver featureFlagState2() {
      return new FeatureFlagStateResolver() {
        @Override
        public List<FeatureFlagState> retrieveStates() {
          return List.of(FeatureFlagState.builder()
              .name(TEST_FEATURE_2)
              .enabled(true)
              .partition(PARTITION)
              .source(SOURCE)
              .build());
        }
      };
    }
  }

  @Test
  public void testFeatureFlagsAutowired() throws IOException {
    VersionInfo versionInfo = versionInfoBuilder.buildVersionInfo();
    List<FeatureFlagState> featureFlagStates = versionInfo.getFeatureFlagStates();
    assertEquals(2, featureFlagStates.size());
    boolean ffs1IsPresent = featureFlagStates.stream().anyMatch(ffs -> ffs.getName().equals(TEST_FEATURE_1));
    assertTrue(ffs1IsPresent);
    boolean ffs2IsPresent = featureFlagStates.stream().anyMatch(ffs -> ffs.getName().equals(TEST_FEATURE_2));
    assertTrue(ffs2IsPresent);
  }

}
