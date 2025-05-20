package xyz.bijit.admission.server;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import javax.servlet.ServletException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TomcatServer implements WebServer {
    private final Tomcat tomcat;
    private final List<WebServerAPI> apis;
    private static final int PORT = 8080;

    public TomcatServer() {
        this.tomcat = new Tomcat();
        this.apis = new ArrayList<>();
        setupTomcat();
    }

    private void setupTomcat() {
        tomcat.setPort(PORT);
        tomcat.getConnector();
        
        String docBase = new File(".").getAbsolutePath();
        Context ctx = tomcat.addContext("", docBase);
        
        // Add default servlet
        Tomcat.addServlet(ctx, "default", new DefaultServlet());
        ctx.addServletMappingDecoded("/", "default");
    }

    @Override
    public void start() throws Exception {
        for (WebServerAPI api : apis) {
            addApiToTomcat(api);
        }
        
        tomcat.start();
        System.out.println("Server started on port " + PORT);
        tomcat.getServer().await();
    }

    @Override
    public void stop() throws LifecycleException {
        tomcat.stop();
        tomcat.destroy();
    }

    @Override
    public void addApi(WebServerAPI api) {
        apis.add(api);
    }

    @Override
    public void addApis(List<WebServerAPI> apis) {
        this.apis.addAll(apis);
    }

    private void addApiToTomcat(WebServerAPI api) throws ServletException {
        Context ctx = (Context) tomcat.getHost().findChild("");
        
        // Create servlet
        ApiServlet servlet = new ApiServlet(api);
        Tomcat.addServlet(ctx, api.getApiPath(), servlet);
        
        // Map servlet to path
        ctx.addServletMappingDecoded(api.getApiPath(), api.getApiPath());
        
        // Add CORS filter
        FilterDef corsFilter = new FilterDef();
        corsFilter.setFilterName("CORSFilter");
        corsFilter.setFilterClass("xyz.bijit.admission.filter.CORSFilter");
        ctx.addFilterDef(corsFilter);
        
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("CORSFilter");
        filterMap.addURLPattern(api.getApiPath());
        ctx.addFilterMap(filterMap);
    }
} 