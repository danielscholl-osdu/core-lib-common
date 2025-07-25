// Copyright 2021 Schlumberger
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.opengroup.osdu.core.common.model.units.impl;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;


@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class DateTime extends PersistableReference {
    @NotEmpty
    @JsonProperty("format")
    private String format;

    @NotEmpty
    @JsonProperty("timeZone")
    private String timeZone;
    
    @Override
    public String toJsonString()
    {
        return super.toJsonString();
    }

    public DateTime() {
        super("DTM");
    }

}
