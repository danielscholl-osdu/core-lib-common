/*
 *  Copyright 2020-2023 Google LLC
 *  Copyright 2020-2023 EPAM Systems, Inc
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

package org.opengroup.osdu.core.common.partition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.opengroup.osdu.core.common.cache.ICache;
import org.opengroup.osdu.core.common.model.http.AppException;
import org.springframework.http.HttpStatus;

@RunWith(MockitoJUnitRunner.class)
public class PartitionPropertyResolverTest {

  private static final String PROPERTY_NAME = "TEST_PROPERTY";
  private static final String TEST_ENV_VARIABLE_NAME = "TEST_ENV_VARIABLE_NAME";
  private static final String TEST_VARIABLE_VALUE = "TEST_ENV_VARIABLE_VALUE";
  private static final String TEST_PARTITION = "test_partition";

  @Rule
  public final ExpectedException expectedException = ExpectedException.none();

  @Rule
  public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

  @Mock
  private ICache<String, PartitionInfo> cache;
  @Mock
  private IPartitionProvider partitionProvider;
  @Mock
  private ISensitivePropertyResolver sensitivePropertyResolver;
  @InjectMocks
  private PartitionPropertyResolver propertyResolver;

  @Before
  public void setUp() {
  }

  @Test
  public void getPropertyValueFromEnv() {
    when(sensitivePropertyResolver.getPropertyValue(anyString(), anyString())).thenReturn(TEST_VARIABLE_VALUE);
    environmentVariables.set(TEST_ENV_VARIABLE_NAME, TEST_VARIABLE_VALUE);
    Property property = new Property(true, TEST_ENV_VARIABLE_NAME);
    ImmutableMap<String, Property> propertyMap = ImmutableMap.of(PROPERTY_NAME, property);

    String resolvedValue = propertyResolver.getPropertyValue(propertyMap, PROPERTY_NAME, TEST_PARTITION);

    assertEquals(TEST_VARIABLE_VALUE, resolvedValue);
  }

  @Test
  public void getPropertyValueFromMapValue() {
    Property property = new Property(false, TEST_VARIABLE_VALUE);
    ImmutableMap<String, Property> propertyMap = ImmutableMap.of(PROPERTY_NAME, property);

    String resolvedValue = propertyResolver.getPropertyValue(propertyMap, PROPERTY_NAME, TEST_PARTITION);

    assertEquals(TEST_VARIABLE_VALUE, resolvedValue);
  }

  @Test
  public void shouldThrowExceptionWhenPropertyNotPresentInEnv() {
    when(sensitivePropertyResolver.getPropertyValue(anyString(), anyString())).thenThrow(
        new AppException(HttpStatus.INTERNAL_SERVER_ERROR.value(), PROPERTY_NAME + " not configured correctly",
            PROPERTY_NAME + " not configured correctly"));
    Property property = new Property(true, TEST_ENV_VARIABLE_NAME);
    ImmutableMap<String, Property> propertyMap = ImmutableMap.of(PROPERTY_NAME, property);

    expectedException.expect(AppException.class);
    expectedException.expectMessage(PROPERTY_NAME + " not configured correctly");

    propertyResolver.getPropertyValue(propertyMap, PROPERTY_NAME, TEST_PARTITION);
  }

  @Test
  public void shouldThrowExceptionWhenEnvReturnNull() {
    when(sensitivePropertyResolver.getPropertyValue(anyString(), anyString())).thenThrow(
        new AppException(HttpStatus.INTERNAL_SERVER_ERROR.value(), PROPERTY_NAME + " not configured correctly",
            PROPERTY_NAME + " not configured correctly"));
    environmentVariables.set(TEST_ENV_VARIABLE_NAME, null);
    Property property = new Property(true, TEST_ENV_VARIABLE_NAME);
    ImmutableMap<String, Property> propertyMap = ImmutableMap.of(PROPERTY_NAME, property);

    expectedException.expect(AppException.class);
    expectedException.expectMessage(PROPERTY_NAME + " not configured correctly");

    propertyResolver.getPropertyValue(propertyMap, PROPERTY_NAME, TEST_PARTITION);
  }

  @Test
  public void shouldNotThrowExceptionWhenEnvReturnEmptyVal() {
    when(sensitivePropertyResolver.getPropertyValue(anyString(), anyString())).thenReturn("");
    Property property = new Property(true, TEST_ENV_VARIABLE_NAME);
    ImmutableMap<String, Property> propertyMap = ImmutableMap.of(PROPERTY_NAME, property);

    String propertyValue = propertyResolver.getPropertyValue(propertyMap, PROPERTY_NAME, TEST_PARTITION);
    assertEquals("", propertyValue);
  }

  @Test
  public void shouldThrowExceptionWhenPropertyNotInMap() {
    expectedException.expect(AppException.class);
    expectedException.expectMessage(
        "Partition service not configured correctly for partition " + TEST_PARTITION + ", missing property: " + PROPERTY_NAME);

    propertyResolver.getPropertyValue(Collections.emptyMap(), PROPERTY_NAME, TEST_PARTITION);
  }

  @Test
  public void shouldThrowExceptionWhenPropertyInMapNull() {
    Map<String, Property> map = Collections.singletonMap(PROPERTY_NAME, null);

    expectedException.expect(AppException.class);
    expectedException.expectMessage(
        "Partition service not configured correctly for partition " + TEST_PARTITION + ", missing property: " + PROPERTY_NAME);

    propertyResolver.getPropertyValue(map, PROPERTY_NAME, TEST_PARTITION);
  }

  @Test
  public void shouldThrowExceptionWhenPropertyValueInMapNull() {
    Property property = new Property(false, null);
    Map<String, Property> map = Collections.singletonMap(PROPERTY_NAME, property);

    expectedException.expect(AppException.class);
    expectedException.expectMessage(
        "Partition service not configured correctly for partition " + TEST_PARTITION + ", missing property value : " + PROPERTY_NAME);

    propertyResolver.getPropertyValue(map, PROPERTY_NAME, TEST_PARTITION);
  }

  @Test
  public void shouldNotThrowExceptionWhenPropertyValueInMapIsEmpty() {
    Property property = new Property(false, "");
    Map<String, Property> map = Collections.singletonMap(PROPERTY_NAME, property);

    String propertyValue = propertyResolver.getPropertyValue(map, PROPERTY_NAME, TEST_PARTITION);
    assertEquals("", propertyValue);
  }

  @Test
  public void shouldGetTypedProperties() {
    Property boolProperty = new Property(false, "true");
    Property intProperty = new Property(false, "100");
    Property doubleProperty = new Property(false, "100.200");
    String boolName = PROPERTY_NAME + "_BOOL";
    String intName = PROPERTY_NAME + "_INT";
    String doubleName = PROPERTY_NAME + "_DOUBLE";

    Map<String, Property> map = new HashMap<>();
    map.put(boolName, boolProperty);
    map.put(intName, intProperty);
    map.put(doubleName, doubleProperty);

    Boolean boolPropertyValue = propertyResolver.getPropertyValue(map, boolName,
        TEST_PARTITION, Boolean.class);
    Integer intPropertyValue = propertyResolver.getPropertyValue(map, intName, TEST_PARTITION,
        Integer.class);
    Double doublePropertyValue = propertyResolver.getPropertyValue(map, doubleName, TEST_PARTITION,
        Double.class);
    assertTrue(boolPropertyValue);
    assertEquals(100, (int) intPropertyValue);
    assertEquals(100.200, doublePropertyValue, 0.0);
  }

}