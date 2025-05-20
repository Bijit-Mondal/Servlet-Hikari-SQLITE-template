package xyz.bijit.admission.server;

import java.util.List;

public interface WebServer {
    void start() throws Exception;
    void stop() throws Exception;
    void addApi(WebServerAPI api);
    void addApis(List<WebServerAPI> apis);
} 