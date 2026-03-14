package model;

public class LoginCredential {
    private final String employeeId;
    private final String email;
    private final String password;
    private final String role;

    public LoginCredential(String employeeId, String email, String password, String role) {
        this.employeeId = employeeId == null ? "" : employeeId.trim();
        this.email = email == null ? "" : email.trim();
        this.password = password == null ? "" : password;
        this.role = role == null ? "" : role.trim();
    }

    public String getEmployeeId() { return employeeId; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}
