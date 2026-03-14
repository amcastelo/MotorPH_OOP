package dao;

import model.*;

/**
 * Represents the employee factory component used in the dao layer.
 */
public final class EmployeeFactory {

    /**
     * Creates the required data.
     * @param data input value needed by this method.
     * @return resulting value produced by this method.
     */
    public Employee create(String[] employeeData, String[] credentialData) {
        String status = (employeeData.length > 10) ? safe(employeeData[10]) : "";
        String role = (credentialData != null && credentialData.length > 3)
                ? safe(credentialData[3])
                : "";

        if (status.equalsIgnoreCase("Probationary")) {
            return new ProbationaryEmployee(employeeData);
        }

        String r = role.toLowerCase();

        if (r.contains("payroll")) return new PayrollEmployee(employeeData);
        if (r.contains("hr")) return new HREmployee(employeeData);
        if (r.contains("it")) return new ITEmployee(employeeData);
        if (r.contains("accounting") || r.contains("finance") || r.contains("chief finance")) {
            return new FinanceEmployee(employeeData);
        }
        if (r.contains("admin") || r.contains("chief") || r.contains("team leader") || r.contains("manager")) {
            return new AdminEmployee(employeeData);
        }

        return new RegularEmployee(employeeData);
    }

    /**
     * Handles safe.
     * @param s input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
