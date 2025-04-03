/*
 * Copyright 2020-2023 Google LLC
 * Copyright 2020-2023 EPAM Systems, Inc
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

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.opengroup.osdu.core.common.model.info.ConnectedOuterService;
import org.opengroup.osdu.core.common.model.info.FeatureFlagStateResolver;
import org.opengroup.osdu.core.common.model.info.FeatureFlagStateResolver.FeatureFlagState;
import org.opengroup.osdu.core.common.model.info.VersionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CloudVersionInfoBuilder implements VersionInfoBuilder {

  private static final String BUILD_GROUP = "build.group";
  private static final String BUILD_ARTIFACT = "build.artifact";
  private static final String BUILD_VERSION = "build.version";
  private static final String BUILD_TIME = "build.time";
  private static final String GIT_BRANCH = "git.branch";
  private static final String GIT_COMMIT_ID = "git.commit.id";
  private static final String GIT_COMMIT_MESSAGE_SHORT = "git.commit.message.short";

  private final Properties buildInfoProperties = new Properties();
  private final Properties gitProperties = new Properties();
  private final VersionInfoProperties versionInfoProperties;

  @Autowired(required = false)
  private List<ConnectedOuterServicesBuilder> outerServicesBuilder;
  @Autowired(required = false)
  private List<FeatureFlagStateResolver> featureFlagStateResolvers;

  public CloudVersionInfoBuilder(VersionInfoProperties versionInfoProperties) {
    this.versionInfoProperties = versionInfoProperties;
  }

  @PostConstruct
  public void init() throws IOException {
    loadBuildInfoProperties();
    loadGitProperties();
  }

  public VersionInfo buildVersionInfo() throws IOException {
    List<ConnectedOuterService> connectedOuterServices = loadConnectedOuterServices();

    List<FeatureFlagState> featureFlagStates = getFeatureFlagStates();

    return VersionInfo.builder()
        .groupId(buildInfoProperties.getProperty(BUILD_GROUP))
        .artifactId(buildInfoProperties.getProperty(BUILD_ARTIFACT))
        .version(buildInfoProperties.getProperty(BUILD_VERSION))
        .buildTime(buildInfoProperties.getProperty(BUILD_TIME))
        .branch(gitProperties.getProperty(GIT_BRANCH))
        .commitId(gitProperties.getProperty(GIT_COMMIT_ID))
        .commitMessage(gitProperties.getProperty(GIT_COMMIT_MESSAGE_SHORT))
        .connectedOuterServices(connectedOuterServices)
        .featureFlagStates(featureFlagStates)
        .build();
  }

  private void loadBuildInfoProperties() throws IOException {
    String buildPropertiesPath = versionInfoProperties.getBuildPropertiesPath();
    try (InputStream buildInfoStream = this.getClass().getResourceAsStream(buildPropertiesPath)) {
      if (buildInfoStream != null) {
        buildInfoProperties.load(buildInfoStream);
      } else {
        log.error("Build-info properties file not found by path: {}", buildPropertiesPath);
      }
    }
  }

  private void loadGitProperties() throws IOException {
    String gitPropertiesPath = versionInfoProperties.getGitPropertiesPath();
    try (InputStream gitStream = this.getClass().getResourceAsStream(gitPropertiesPath)) {
      if (gitStream != null) {
        gitProperties.load(gitStream);
      } else {
        log.error("Git properties file not found by path: {}", gitPropertiesPath);
      }
    }
  }

  /**
   * The method collects service-specific values for all outer services connected to OSDU service. To define outer
   * services info for OSDU service need to inject ConnectedOuterServicesBuilder.
   */
  private List<ConnectedOuterService> loadConnectedOuterServices() {
    return Optional.ofNullable(outerServicesBuilder)
        .map(
            builderList ->
                builderList.stream()
                    .map(ConnectedOuterServicesBuilder::buildConnectedOuterServices)
                    .flatMap(List::stream)
                    .collect(Collectors.toList()))
        .orElse(Collections.emptyList());
  }

  private List<FeatureFlagState> getFeatureFlagStates() {
    return Optional.ofNullable(featureFlagStateResolvers)
        .map(list -> list.stream().map(FeatureFlagStateResolver::retrieveStates)
            .flatMap(Collection::stream)
            .toList())
        .orElse(Collections.emptyList());
  }
}
