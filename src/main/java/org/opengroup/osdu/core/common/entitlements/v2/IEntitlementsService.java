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

package org.opengroup.osdu.core.common.entitlements.v2;

import org.opengroup.osdu.core.common.model.entitlements.EntitlementsException;

import java.util.List;

import org.opengroup.osdu.core.common.model.entitlements.CreateGroup;
import org.opengroup.osdu.core.common.model.entitlements.GetMembers;
import org.opengroup.osdu.core.common.model.entitlements.GroupEmail;
import org.opengroup.osdu.core.common.model.entitlements.GroupInfo;
import org.opengroup.osdu.core.common.model.entitlements.Groups;
import org.opengroup.osdu.core.common.model.entitlements.MemberInfo;
import org.opengroup.osdu.core.common.model.entitlements.Members;
import org.opengroup.osdu.core.common.model.entitlements.UpdateGroupOperation;

public interface IEntitlementsService {

    GroupInfo createGroup(CreateGroup group) throws EntitlementsException;

    GroupInfo updateGroup(String existingGroupEmail, List<UpdateGroupOperation> updateGroupRequest) throws EntitlementsException;
  
    Groups getGroups() throws EntitlementsException;
  
    void deleteGroup(String groupEmail) throws EntitlementsException;
  
    void revokeMembership(String memberEmail) throws EntitlementsException;
  
    MemberInfo addMemberCrossPartition(GroupEmail groupEmail, MemberInfo memberInfo) throws EntitlementsException;
  
    MemberInfo addMember(GroupEmail groupEmail, MemberInfo memberInfo) throws EntitlementsException;
  
    Members getMembers(GroupEmail groupEmail, GetMembers getMembers) throws EntitlementsException;
  
    void deleteMember(String groupEmail, String memberEmail) throws EntitlementsException;
  
    Groups getCrossPartitionGroups() throws EntitlementsException;
  
    Groups authorizeAny(String... groupNames) throws EntitlementsException;
  
    Groups authorizeAnyRolesAllDataPartitions(String... groupNames) throws EntitlementsException;
  
    void authenticate() throws EntitlementsException;
}