/*
 Copyright 2020-2024 Google LLC
 Copyright 2020-2024 EPAM Systems, Inc

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

package org.opengroup.osdu.core.common.secret;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.*;
import org.opengroup.osdu.core.common.model.entitlements.Acl;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecretModel {

  @Pattern(
      regexp = "[a-zA-Z_\\-0-9]+",
      groups = {PostValidation.class})
  @NotEmpty(message = "Secret ID cannot be empty", groups = PostValidation.class)
  @Null(message = "Secret ID cannot be updated.", groups = PutValidation.class)
  @Size(
      min = 2,
      max = 100,
      message = "Secret ID must be between 2 and 100 characters",
      groups = {PostValidation.class})
  private String id;

  @NotEmpty(
      message = "Secret value cannot be empty",
      groups = {PostValidation.class, PutValidation.class})
  private String value;
  
  @Null(message = "createdAt value cannot be defined by the user.")
  @EqualsAndHashCode.Exclude
  private OffsetDateTime createdAt;

  @NotNull(groups = {PostValidation.class, PutValidation.class})
  private boolean isEnabled;

  private Acl secretAcls;

  private Map<String, Object> metadata;
  
  @Null(message = "createdBy value cannot be defined by the user.")
  @EqualsAndHashCode.Exclude
  private String createdBy;

  public interface PostValidation {}

  public interface PutValidation {}
}
