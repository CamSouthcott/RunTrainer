package com.camsouthcott.runtrainer.http;

import java.util.List;
import java.util.Map;

public class HttpResponse {

    Integer responseCode;
    String body;
    Map<String, List<String>> headers;

    public HttpResponse(Integer responseCode, String body, Map<String, List<String>> headers) {
        super();
        this.responseCode = responseCode;
        this.body = body;
        this.headers = headers;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public String getBody() {
        return body;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }
}
