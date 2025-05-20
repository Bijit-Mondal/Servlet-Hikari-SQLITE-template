package xyz.bijit.admission.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface WebServerAPI {
    void handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
    String getApiPath();
} 