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

  private static final String TEST_BUILD_INFO_PATH = "testdata/build-info.properties";
  private static final String TEST_GIT_INFO_PATH = "testdata/git-info.properties";

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
