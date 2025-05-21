package xyz.bijit.admission.di;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.bijit.admission.notes.service.INoteService;
import xyz.bijit.admission.notes.service.NoteService;
import xyz.bijit.admission.db.DatabaseManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Container {
    private static final Logger logger = LoggerFactory.getLogger(Container.class);
    private static final Container instance = new Container();
    private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();
    private volatile boolean initialized = false;

    private Container() {
        // Private constructor for singleton
    }

    public static Container getInstance() {
        return instance;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public synchronized void initialize() {
        if (initialized) {
            logger.warn("Container already initialized, skipping initialization");
            return;
        }

        logger.info("Initializing DI container");
        try {
            // Initialize core services
            logger.debug("Initializing core services");
            DatabaseManager dbManager = new DatabaseManager();
            services.put(DatabaseManager.class, dbManager);

            // Initialize business services
            logger.debug("Initializing business services");
            NoteService noteService = new NoteService(dbManager);
            services.put(INoteService.class, noteService);

            initialized = true;
            logger.info("DI container initialized successfully with {} services", services.size());
        } catch (Exception e) {
            logger.error("Failed to initialize DI container", e);
            // Cleanup any partially initialized services
            shutdown();
            throw new RuntimeException("Failed to initialize DI container", e);
        }
    }

    public synchronized void shutdown() {
        if (!initialized) {
            logger.warn("Container not initialized, nothing to shutdown");
            return;
        }

        logger.info("Shutting down DI container");
        // Shutdown services in reverse order
        services.values().forEach(service -> {
            if (service instanceof AutoCloseable) {
                try {
                    logger.debug("Closing service: {}", service.getClass().getName());
                    ((AutoCloseable) service).close();
                } catch (Exception e) {
                    logger.error("Error closing service: {}", service.getClass().getName(), e);
                }
            }
        });

        services.clear();
        initialized = false;
        logger.info("DI container shut down successfully");
    }

    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceClass) {
        if (!initialized) {
            logger.error("Container not initialized");
            throw new IllegalStateException("Container not initialized");
        }
        T service = (T) services.get(serviceClass);
        if (service == null) {
            logger.error("Service not found: {}", serviceClass.getName());
            throw new IllegalStateException("Service not found: " + serviceClass.getName());
        }
        return service;
    }

    // For testing purposes
    public <T> void registerService(Class<T> serviceClass, T instance) {
        logger.debug("Registering service: {}", serviceClass.getName());
        services.put(serviceClass, instance);
    }
} 