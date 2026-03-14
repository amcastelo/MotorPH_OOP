package dao;

import model.LoginCredential;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Loads login credentials from a CSV on the classpath.
 * Expected headers: Employee #, email, password, role
 */
public class CredentialsRepository {

    private final String resourceName;
    private final Path writablePath;
    private final Path targetClassesPath;

    public CredentialsRepository() {
        this("credentials.csv");
    }

    public CredentialsRepository(String resourceName) {
        this.resourceName = (resourceName == null || resourceName.isBlank()) ? "credentials.csv" : resourceName;
        this.writablePath = Path.of("src", "main", "resources", this.resourceName);
        this.targetClassesPath = Path.of("target", "classes", this.resourceName);
    }

    public List<LoginCredential> loadAll() {
        List<LoginCredential> out = new ArrayList<>();

        if (Files.exists(writablePath)) {
            try (BufferedReader br = Files.newBufferedReader(writablePath, StandardCharsets.UTF_8)) {
                parseCsv(br, out);
                return out;
            } catch (IOException ignored) {
                // fall back to classpath
            }
        }

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        if (is == null) {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("data/" + resourceName);
        }
        if (is == null) {
            return out;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            parseCsv(br, out);
        } catch (Exception ignored) {
            // return what we have
        }
        return out;
    }

    public Result saveCredential(String employeeId, String email, String password, String role) {
        String empId = safe(employeeId);
        String normalizedEmail = safe(email).toLowerCase(Locale.ROOT);
        String normalizedRole = safe(role).toUpperCase(Locale.ROOT);
        String pwd = password == null ? "" : password;

        if (empId.isEmpty() || normalizedEmail.isEmpty() || pwd.isBlank() || normalizedRole.isEmpty()) {
            return Result.fail("All credential fields are required.");
        }
        if (!isValidEmail(normalizedEmail)) {
            return Result.fail("Invalid email format.");
        }

        for (LoginCredential credential : loadAll()) {
            if (credential == null) continue;
            if (empId.equalsIgnoreCase(safe(credential.getEmployeeId()))) {
                return Result.fail("Credentials already exist for this employee number.");
            }
            if (normalizedEmail.equalsIgnoreCase(safe(credential.getEmail()))) {
                return Result.fail("Email already exists.");
            }
        }

        try {
            ensureFileWithHeader(writablePath);
            try (BufferedWriter writer = Files.newBufferedWriter(
                    writablePath,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND)) {
                writer.write(String.join(",", empId, normalizedEmail, pwd, normalizedRole));
                writer.newLine();
            }

            if (Files.exists(targetClassesPath)) {
                Files.copy(writablePath, targetClassesPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            return Result.ok("Credentials added successfully.");
        } catch (IOException e) {
            return Result.fail("Unable to save credentials: " + e.getMessage());
        }
    }

    public boolean hasCredentials(String employeeId) {
        String empId = safe(employeeId);
        if (empId.isEmpty()) return false;

        for (LoginCredential credential : loadAll()) {
            if (credential == null) continue;
            if (empId.equalsIgnoreCase(safe(credential.getEmployeeId()))) {
                return true;
            }
        }
        return false;
    }

    private void ensureFileWithHeader(Path path) throws IOException {
        if (path.getParent() != null && Files.notExists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }
        if (Files.notExists(path)) {
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                writer.write("Employee #,email,password,role");
                writer.newLine();
            }
        }
    }

    private void parseCsv(BufferedReader br, List<LoginCredential> out) throws IOException {
        String header = br.readLine();
        if (header == null) return;

        String line;
        while ((line = br.readLine()) != null) {
            if (line.isBlank()) continue;
            String[] parts = line.split(",", -1);
            if (parts.length < 4) continue;

            String empId = parts[0].trim();
            String email = parts[1].trim();
            String password = parts[2];
            String role = parts[3].trim();

            if (email.isBlank() || password == null || password.isBlank() || empId.isBlank()) continue;
            out.add(new LoginCredential(empId, email, password, role));
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static final class Result {
        private final boolean ok;
        private final String message;

        private Result(boolean ok, String message) {
            this.ok = ok;
            this.message = message;
        }

        public static Result ok(String message) {
            return new Result(true, message);
        }

        public static Result fail(String message) {
            return new Result(false, message);
        }

        public boolean isOk() {
            return ok;
        }

        public String getMessage() {
            return message;
        }
    }
}
