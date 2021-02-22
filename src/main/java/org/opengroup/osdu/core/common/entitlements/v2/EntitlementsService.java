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
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.entitlements.CreateGroup;
import org.opengroup.osdu.core.common.model.entitlements.GetMembers;
import org.opengroup.osdu.core.common.model.entitlements.GroupEmail;
import org.opengroup.osdu.core.common.model.entitlements.GroupInfo;
import org.opengroup.osdu.core.common.model.entitlements.Groups;
import org.opengroup.osdu.core.common.model.entitlements.MemberInfo;
import org.opengroup.osdu.core.common.model.entitlements.Members;
import org.opengroup.osdu.core.common.model.entitlements.UpdateGroupOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.opengroup.osdu.core.common.http.HttpRequest;
import org.opengroup.osdu.core.common.http.HttpResponse;
import org.opengroup.osdu.core.common.http.IHttpClient;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyMapper;
import org.opengroup.osdu.core.common.http.json.HttpResponseBodyParsingException;

public class EntitlementsService implements IEntitlementsService {

    public static final String EMPTY_BODY = "{}";
    public static final String GROUPS = "/groups";

    private final String rootUrl;
    private final IHttpClient httpClient;
    private final DpsHeaders headers;
    private final HttpResponseBodyMapper responseBodyMapper;

    public EntitlementsService(EntitlementsAPIConfig config,
                               IHttpClient httpClient,
                               DpsHeaders headers,
                               HttpResponseBodyMapper mapper) {
        this.rootUrl = config.getRootUrl();
        this.httpClient = httpClient;
        this.headers = headers;
        this.responseBodyMapper = mapper;
        if (config.apiKey != null) {
            this.headers.put("AppKey", config.apiKey);
        }
    }    
    
    public List<String> listBackupVersions() throws EntitlementsException {
        String url = createUrl("/versions");
        HttpRequest request = HttpRequest.get().url(url).headers(headers.getHeaders()).build();
        HttpResponse result = this.httpClient.send(request);
        if (result.IsNotFoundCode()) {
            return new ArrayList<>();
        } else {
            List<String> output = getResult(result, List.class);
            if (output != null) {
                return output;
            } else {
                throw new EntitlementsException("Error parsing response. Check the inner HttpResponse for more info.", result);
            }
        }
    }

    public static List<UpdateGroupOperation> buildUpdateGroupRequestBody(String operation, String path, String... value) {
        List<UpdateGroupOperation> request = new ArrayList<>();
        List<String> operationValue = new ArrayList<>();
        for (String v : value) {
            operationValue.add(v);
        }

        UpdateGroupOperation updateGroupOperation = UpdateGroupOperation.builder()
                .operation(operation)
                .path(path)
                .value(operationValue)
                .build();
        request.add(updateGroupOperation);

        return request;
    }

    @Override
    public Groups getGroups() throws EntitlementsException {
    return get(GROUPS, Groups.class);
    }

    @Override
    public Groups getCrossPartitionGroups() throws EntitlementsException {
    return get("/cross/groups", Groups.class);
    }

    @Override
    public MemberInfo addMemberCrossPartition(GroupEmail groupEmail, MemberInfo memberInfo) throws EntitlementsException {
    return post(String.format("/groups/data/%s/members", groupEmail.getGroupEmail()), memberInfo, MemberInfo.class);
    }

    @Override
    public MemberInfo addMember(GroupEmail groupEmail, MemberInfo memberInfo) throws EntitlementsException {
    return post(String.format("/groups/%s/members", groupEmail.getGroupEmail()), memberInfo, MemberInfo.class);
    }

    @Override
    public Members getMembers(GroupEmail groupEmail, GetMembers getMembers) throws EntitlementsException {
    String path = String
        .format("/groups/%s/members?cursor=%s&limit=%s&role=%s", groupEmail.getGroupEmail(), getMembers.getCursor(),
            getMembers.getLimit(), getMembers.getRole());
    return get(path, Members.class);
    }

    @Override
    public GroupInfo createGroup(CreateGroup group) throws EntitlementsException {
    return post(GROUPS, group, GroupInfo.class);
    }

    @Override
    public void deleteMember(String groupEmail, String memberEmail) throws EntitlementsException {
    delete(String.format("/groups/%s/members/%s", groupEmail, memberEmail), String.class);
    }

    @Override
    public void authenticate() throws EntitlementsException {
    get("/auth/validate", Object.class);
    }

    @Override
    public void deleteGroup(String groupEmail) throws EntitlementsException {
    delete(String.format("/groups/%s", groupEmail), String.class);
    }

    @Override
    public GroupInfo updateGroup(String existingGroupEmail, List<UpdateGroupOperation> updateGroupRequest) throws EntitlementsException {
      return patch(String.format("/groups/%s", existingGroupEmail), updateGroupRequest, GroupInfo.class);
    }
  
    @Override
    public void revokeMembership(String memberEmail) throws EntitlementsException {
      delete(String.format("/members/%s", memberEmail), String.class);
    }
  
    @Override
    public Groups authorizeAny(String... groupNames) throws EntitlementsException {
      Groups groups = this.getGroups();
      if (groups.any(groupNames)) {
        return groups;
      }
  
      throw new EntitlementsException(
              String.format("User is unauthorized. %s does not belong to any of the given groups %s",
                      groups.getMemberEmail(), groupNames), null);
    }

    @Override
    public Groups authorizeAnyRolesAllDataPartitions(String... groupNames) throws EntitlementsException {
        Groups groups = this.getCrossPartitionGroups();
        String[] dataPartitionIds = this.headers.getPartitionId().trim().split("\\s*,\\s*");
        List<GroupInfo> cachedGroups = new ArrayList<>(groups.getGroups());
    
        for (String dataPartitionId : dataPartitionIds) {
        groups.setGroups(groups.getGroups()
                .stream()
                .filter(groupInfo -> groupInfo.getEmail().contains(String.format("@%s", dataPartitionId)))
                .collect(Collectors.toList())
        );
    
        if (groups.any(groupNames)) {
            groups.setGroups(cachedGroups);
            continue;
        }
    
        throw new EntitlementsException(
                String.format("User is unauthorized in partition: %s. %s does not belong to any of the given groups %s",
                        dataPartitionId, groups.getMemberEmail(), groupNames),
                null
        );
        }
        return groups;
    }
    
    private <T> T get(String path, Class<T> type) throws EntitlementsException {
        String url = this.createUrl(path);
        HttpRequest rq = HttpRequest.get().url(url).headers(this.headers.getHeaders()).build();
    
        return execute(rq, type);
    }
    
    private <T> T delete(String path, Class<T> type) throws EntitlementsException {
        String url = this.createUrl(path);
        HttpRequest rq = HttpRequest.delete().url(url).headers(this.headers.getHeaders()).build();
    
        return execute(rq, type);
    }
    
    private <B, T> T post(String path, B body, Class<T> type) throws EntitlementsException {
        String url = this.createUrl(path);
        HttpRequest rq = HttpRequest.post(body).url(url).headers(this.headers.getHeaders()).build();
    
        return execute(rq, type);
    }
    
    private <B, T> T patch(String path, B body, Class<T> type) throws EntitlementsException {
        String url = this.createUrl(path);
        HttpRequest rq = HttpRequest.patch(body).url(url).headers(this.headers.getHeaders()).build();
    
        return execute(rq, type);
    }
    
    private <T> T execute(HttpRequest request, Class<T> type) throws EntitlementsException {
        HttpResponse response = this.httpClient.send(request);
        return getResult(response, type);
    }
    
    private <T> T getResult(HttpResponse result, Class<T> type) throws EntitlementsException {
        if (result.isSuccessCode()) {
            try {
                return responseBodyMapper.parseBody(result, type);
            } catch (HttpResponseBodyParsingException e) {
                throw new EntitlementsException("Error parsing response. Check the inner HttpResponse for more info.",
                        result);
            }
        } else {
            throw this.generateEntitlementsException(result);
        }
    }

    private EntitlementsException generateEntitlementsException(HttpResponse result) {
        return new EntitlementsException(
                "Error making request to Entitlements service. Check the inner HttpResponse for more info.", result);
    }

    private String createUrl(String pathAndQuery) {
        return StringUtils.join(this.rootUrl, pathAndQuery);
    }

}
