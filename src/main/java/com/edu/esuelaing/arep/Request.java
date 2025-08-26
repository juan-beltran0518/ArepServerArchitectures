package com.edu.esuelaing.arep;

import java.util.Map;
import java.util.HashMap;

public class Request {
    private String method;
    private String path;
    private Map<String, String> queryParams;
    private Map<String, String> headers;

    public Request(String method, String path, Map<String, String> queryParams, Map<String, String> headers) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams != null ? queryParams : new HashMap<>();
        this.headers = headers != null ? headers : new HashMap<>();
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getQueryParam(String key) {
        return queryParams.get(key);
    }

    public java.util.List<String> getValues(String key) {
        String value = queryParams.get(key);
        if (value == null || value.isEmpty())
            return java.util.Collections.emptyList();
        return java.util.Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }
}
