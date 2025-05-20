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
        
        // Get the application's base directory
        String docBase = new File(System.getProperty("user.dir")).getAbsolutePath();
        Context ctx = tomcat.addContext("", docBase);
        
        // Set up the class loader to find our classes
        ctx.setParentClassLoader(Thread.currentThread().getContextClassLoader());
        
        // Remove default servlet mapping as we're building an API-only application
        // If you need to serve static files later, we can add specific mappings for those
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
        String servletName = api.getApiPath().replace("/", "_");
        Tomcat.addServlet(ctx, servletName, servlet);
        
        // Map servlet to path with wildcard to handle all HTTP methods
        ctx.addServletMappingDecoded(api.getApiPath() + "/*", servletName);
        
        // Add CORS filter
        FilterDef corsFilter = new FilterDef();
        corsFilter.setFilterName("CORSFilter");
        corsFilter.setFilter(new xyz.bijit.admission.filter.CORSFilter());
        ctx.addFilterDef(corsFilter);
        
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("CORSFilter");
        filterMap.addURLPattern(api.getApiPath() + "/*");
        ctx.addFilterMap(filterMap);
    }
} 