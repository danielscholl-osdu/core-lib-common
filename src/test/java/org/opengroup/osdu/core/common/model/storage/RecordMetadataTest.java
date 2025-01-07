package org.opengroup.osdu.core.common.model.storage;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.model.entitlements.Acl;
import org.opengroup.osdu.core.common.model.http.AppException;
import org.opengroup.osdu.core.common.model.legal.Legal;

@RunWith(MockitoJUnitRunner.class)
public class RecordMetadataTest {

  private static final String TEST_KIND = "test-kind";
  private static final String TEST_ID = "test-id";
  private static final long FIRST_PATH_ID = 1L;
  private static final long SECOND_PATH_ID = 2L;
  private static final long THIRD_PATH_ID = 3L;

  @Mock private Record record;

  private RecordMetadata metadata;

  @Before
  public void setup() {
    when(record.getId()).thenReturn(TEST_ID);
    when(record.getKind()).thenReturn(TEST_KIND);
    when(record.getAcl()).thenReturn(new Acl());
    when(record.getLegal()).thenReturn(new Legal());
    when(record.getTags()).thenReturn(new HashMap<>());
    when(record.getAncestry()).thenReturn(new RecordAncestry());

    metadata = new RecordMetadata(record);
  }

  @Test
  public void shouldReturnNullForLatestVersionWhenNoVersionsExist() {
    assertNull(metadata.getLatestVersion());
  }

  @Test
  public void shouldReturnLatestVersionWhenVersionsExist() {
    metadata.addGcsPath(FIRST_PATH_ID);
    metadata.addGcsPath(SECOND_PATH_ID);
    metadata.addGcsPath(THIRD_PATH_ID);

    Long latestVersion = metadata.getLatestVersion();

    assertEquals(Long.valueOf(THIRD_PATH_ID), latestVersion);
  }

  @Test
  public void shouldReturnFalseWhenNoVersions() {
    assertFalse(metadata.hasVersion());
  }

  @Test
  public void shouldReturnTrueWhenVersionsExist() {
    metadata.addGcsPath(FIRST_PATH_ID);

    assertTrue(metadata.hasVersion());
  }

  @Test
  public void shouldAddGcsPathWithCorrectFormat() {
    metadata.addGcsPath(FIRST_PATH_ID);

    assertEquals(1, metadata.getGcsVersionPaths().size());
    assertEquals("%s/%s/1".formatted(TEST_KIND, TEST_ID), metadata.getGcsVersionPaths().get(0));
  }

  @Test
  public void shouldReturnCorrectVersionPath() {
    metadata.addGcsPath(FIRST_PATH_ID);
    metadata.addGcsPath(SECOND_PATH_ID);

    String path = metadata.getVersionPath(FIRST_PATH_ID);

    assertEquals("%s/%s/1".formatted(TEST_KIND, TEST_ID), path);
  }

  @Test(expected = AppException.class)
  public void shouldThrowExceptionWhenVersionNotFound() {
    metadata.addGcsPath(FIRST_PATH_ID);

    metadata.getVersionPath(SECOND_PATH_ID);
  }

  @Test
  public void shouldResetGcsPath() {
    metadata.addGcsPath(FIRST_PATH_ID);
    metadata.addGcsPath(SECOND_PATH_ID);
    List<String> newPaths = Arrays.asList("path1", "path2");

    metadata.resetGcsPath(newPaths);

    assertEquals(2, metadata.getGcsVersionPaths().size());
    assertTrue(metadata.getGcsVersionPaths().containsAll(newPaths));
  }

  @Test
  public void shouldConstructFromRecord() {
    assertEquals(TEST_ID, metadata.getId());
    assertEquals(TEST_KIND, metadata.getKind());
    assertNotNull(metadata.getAcl());
    assertNotNull(metadata.getLegal());
    assertNotNull(metadata.getTags());
    assertNotNull(metadata.getAncestry());
  }
}
