# Admission Project

A minimal Java backend based on embedded Tomcat and SQLite.

---

## 🚀 Development

### Prerequisites
- Java 11+
- Maven
- (Optional) For live reload: [entr](https://archlinux.org/packages/community/x86_64/entr/)

### Run in Development Mode

Compile and launch your server:
```bash
mvn compile exec:java
```

#### Hot Reload (Auto-restart on changes)

1. Install entr (if you haven't):
   ```bash
   yay -S entr
   ```
2. Start the watch/reload server:
   ```bash
   find src/ | entr -r mvn compile exec:java
   ```

---

## 📦 Deployment

To produce a runnable jar for deployment:
```bash
mvn clean package
```
The fat-jar will be located at:
```
target/admission-1.0-SNAPSHOT.jar
```

Run the server:
```bash
java -jar target/admission-1.0-SNAPSHOT.jar
```

---

### Notes

- **Development (`exec:java`)** runs your Main class directly, perfect for iterative changes and debugging.
- **Deployment (`package` + `java -jar`)** creates a self-contained jar for production.
- You can configure or change the main class entry point in `pom.xml` if your application structure changes.