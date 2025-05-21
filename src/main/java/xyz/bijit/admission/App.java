package xyz.bijit.admission;

import xyz.bijit.admission.notes.api.NoteAPI;
import xyz.bijit.admission.server.TomcatServer;
import xyz.bijit.admission.server.WebServer;
import xyz.bijit.admission.di.Container;

public class App {
    public static void main(String[] args) {
        try {
            // Initialize DI container
            Container container = Container.getInstance();
            container.initialize();

            // Initialize web server
            WebServer server = new TomcatServer();
            
            // Add APIs
            server.addApi(new NoteAPI());
            
            // Start server
            server.start();

            // Add shutdown hook to clean up resources
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    server.stop();
                    container.shutdown();
                } catch (Exception e) {
                    System.err.println("Error during shutdown: " + e.getMessage());
                    e.printStackTrace();
                }
            }));
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
} 