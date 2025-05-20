package xyz.bijit.admission;

import xyz.bijit.admission.notes.api.NoteAPI;
import xyz.bijit.admission.server.TomcatServer;
import xyz.bijit.admission.server.WebServer;

public class App {
    public static void main(String[] args) {
        try {
            // Initialize web server
            WebServer server = new TomcatServer();
            
            // Add APIs
            server.addApi(new NoteAPI());
            
            // Start server
            server.start();
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
} 