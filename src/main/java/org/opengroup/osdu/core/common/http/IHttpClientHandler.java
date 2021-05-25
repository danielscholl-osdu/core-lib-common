package org.opengroup.osdu.core.common.http;

import org.apache.http.client.methods.HttpRequestBase;
import org.opengroup.osdu.core.common.model.http.DpsHeaders;
import org.opengroup.osdu.core.common.model.http.HttpResponse;

public interface IHttpClientHandler {
    HttpResponse sendRequest(HttpRequestBase request, DpsHeaders requestHeaders);
}
