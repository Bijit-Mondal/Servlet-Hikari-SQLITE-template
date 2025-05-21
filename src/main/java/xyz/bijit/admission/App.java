package xyz.bijit.admission;

import xyz.bijit.admission.notes.api.NoteAPI;
import xyz.bijit.admission.server.TomcatServer;
import xyz.bijit.admission.server.WebServer;
import xyz.bijit.admission.di.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static Container container;
    private static WebServer server;

    public static void main(String[] args) {
        try {
            // Initialize DI container only once
            container = Container.getInstance();
            if (!container.isInitialized()) {
                container.initialize();
            }

            // Initialize web server
            server = new TomcatServer();
            
            // Add APIs
            server.addApi(new NoteAPI());
            
            // Start server
            server.start();
            logger.info("Application started successfully");

            // Add shutdown hook to clean up resources
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    logger.info("Shutting down application...");
                    if (server != null) {
                        server.stop();
                    }
                    if (container != null) {
                        container.shutdown();
                    }
                    logger.info("Application shutdown complete");
                } catch (Exception e) {
                    logger.error("Error during shutdown", e);
                }
            }));
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            // Ensure cleanup on startup failure
            if (container != null && container.isInitialized()) {
                container.shutdown();
            }
            System.exit(1);
        }
    }
} 