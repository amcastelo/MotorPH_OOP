package service;

import dao.CredentialsRepository;
import dao.EmployeeFileManager;
import model.Employee;
import model.LoginCredential;

import java.util.List;
import java.util.Locale;

/**
 * Auth service.
 * Authenticates using credentials.csv (Employee #, email, password, role),
 * then loads the matching Employee record from Data.txt via EmployeeFileManager.
 */
public class AuthService {

    private final EmployeeFileManager employeeFileManager;
    private final CredentialsRepository credentialsRepository;

    private volatile List<Employee> cachedEmployees;
    private volatile List<LoginCredential> cachedCredentials;

    public AuthService(EmployeeFileManager employeeFileManager) {
        this(employeeFileManager, new CredentialsRepository());
    }

    public AuthService(EmployeeFileManager employeeFileManager, CredentialsRepository credentialsRepository) {
        this.employeeFileManager = employeeFileManager;
        this.credentialsRepository = credentialsRepository == null ? new CredentialsRepository() : credentialsRepository;
    }

    public LoginResult authenticate(String email, String password) {
        String u = email == null ? "" : email.trim();
        String p = password == null ? "" : password;

        if (u.isEmpty() || p.trim().isEmpty()) {
            return LoginResult.fail("Please enter both email and password.");
        }

        LoginCredential cred = findCredentialByEmail(u);
        if (cred == null) {
            return LoginResult.fail("Email not found.");
        }

        if (!safeLower(p).equals(safeLower(cred.getPassword()))) {
            return LoginResult.fail("Invalid password.");
        }

        Employee employee = findEmployeeById(cred.getEmployeeId());
        if (employee == null) {
            return LoginResult.fail("Employee record not found for this account.");
        }

        // Role is stored in credentials.csv for future permission mapping; current app uses Employee subclass permissions.
        return LoginResult.ok(employee);
    }

    private LoginCredential findCredentialByEmail(String email) {
        List<LoginCredential> creds = getCredentials();
        String needle = safeLower(email);
        for (LoginCredential c : creds) {
            if (c == null) continue;
            if (needle.equals(safeLower(c.getEmail()))) return c;
        }
        return null;
    }

    private Employee findEmployeeById(String employeeId) {
        List<Employee> employees = getEmployees();
        String needle = employeeId == null ? "" : employeeId.trim();
        for (Employee e : employees) {
            if (e == null) continue;
            if (needle.equals(safe(e.getEmployeeNumber()))) {
                return e;
            }
        }
        return null;
    }

    private List<Employee> getEmployees() {
        List<Employee> local = cachedEmployees;
        if (local == null) {
            local = employeeFileManager.loadFile();
            cachedEmployees = local;
        }
        return local;
    }

    private List<LoginCredential> getCredentials() {
        List<LoginCredential> local = cachedCredentials;
        if (local == null) {
            local = credentialsRepository.loadAll();
            cachedCredentials = local;
        }
        return local;
    }

    private static String safeLower(String s) {
        return s == null ? "" : s.trim().toLowerCase(Locale.ROOT);
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
