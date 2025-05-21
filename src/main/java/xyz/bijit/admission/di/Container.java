package xyz.bijit.admission.di;

import xyz.bijit.admission.notes.service.INoteService;
import xyz.bijit.admission.notes.service.NoteService;
import xyz.bijit.admission.db.DatabaseManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Container {
    private static final Container instance = new Container();
    private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();
    private boolean initialized = false;

    private Container() {
        // Private constructor for singleton
    }

    public static Container getInstance() {
        return instance;
    }

    public synchronized void initialize() {
        if(initialized){
            return;
        }

        // Initialize core services
        DatabaseManager dbManager = new DatabaseManager();
        services.put(DatabaseManager.class, dbManager);

        // Initialize business services
        NoteService noteService = new NoteService(dbManager);
        services.put(INoteService.class, noteService);

        initialized = true;
    }

    public synchronized void shutdown() {
        if (!initialized) {
            return;
        }

        // Shutdown services in reverse order
        services.values().forEach(service -> {
            if (service instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) service).close();
                } catch (Exception e) {
                    // Log error but continue shutdown
                    e.printStackTrace();
                }
            }
        });

        services.clear();
        initialized = false;
    }

    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceClass) {
        if (!initialized) {
            throw new IllegalStateException("Container not initialized");
        }
        return (T) services.get(serviceClass);
    }

    // For testing purposes
    public <T> void registerService(Class<T> serviceClass, T instance) {
        services.put(serviceClass, instance);
    }
} 