package service;

import model.Employee;

/**
 * Represents the login result component used in the service layer.
 */
public final class LoginResult {

    private final boolean success;
    private final String message;
    private final Employee employee;
    
    /**
     * Creates a new LoginResult instance.
     * @param success input value needed by this method.
     * @param message input value needed by this method.
     * @param employee input value needed by this method.
     */
    private LoginResult(boolean success, String message, Employee employee) {
        this.success = success;
        this.message = message;
        this.employee = employee;
    }

    /**
     * Handles ok.
     * @param employee input value needed by this method.
     * @return resulting value produced by this method.
     */
    public static LoginResult ok(Employee employee) {
        return new LoginResult(true, "Login successful", employee);
    }

    /**
     * Handles fail.
     * @param message input value needed by this method.
     * @return resulting value produced by this method.
     */
    public static LoginResult fail(String message) {
        return new LoginResult(false, message == null ? "Login failed" : message, null);
    }

    /**
     * Checks whether success is true.
     * @return resulting value produced by this method.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns message.
     * @return resulting value produced by this method.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns employee.
     * @return resulting value produced by this method.
     */
    public Employee getEmployee() {
        return employee;
    }
}
