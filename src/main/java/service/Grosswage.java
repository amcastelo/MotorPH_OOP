package service;

import dao.EmployeeFileManager;
import model.AttendanceEntry;
import model.Employee;
import model.Salary;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

/**
 * Represents the grosswage component used in the service layer.
 */
public class Grosswage {

    private final AttendanceService attendanceService;

    private String employeeID;
    private String employeeName;
    private double hourly;
    private double hours;
    private double gross;

    /**
     * Creates a new Grosswage instance.
     * @param attendanceService input value needed by this method.
     */
    public Grosswage(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * Calculates the required data.
     * @param employeeNumber input value needed by this method.
     * @param month input value needed by this method.
     * @return resulting value produced by this method.
     */
    public double calculate(String employeeNumber, int month) {

        reset();

        List<Employee> employees = EmployeeFileManager.getEmployeeModelList();
        if (employees == null || employees.isEmpty()) return 0;

        Employee employee = findEmployee(employees, employeeNumber);
        if (employee == null) return 0;

        this.employeeID = employee.getEmployeeNumber();
        this.employeeName = employee.getLastName() + ", " + employee.getFirstName();

        Salary salary = employee.getSalary();
        if (salary == null) return 0;

        this.hourly = salary.getHourlyRate();

        List<AttendanceEntry> entries = attendanceService.getAllFor(employee);

        double totalHours = 0;

        for (AttendanceEntry e : entries) {
            if (!isInMonth(e.getDate(), month)) continue;

            double h = computeHours(e.getTimeIn(), e.getTimeOut());
            totalHours += h;
        }
        this.hours = totalHours;
        this.gross = this.hourly * totalHours;

        return this.gross;
    }


    /**
     * Calculates gross pay using an already-computed total hours value.
     *
     * <p>This overload is intended for scenarios where attendance is filtered elsewhere
     * (e.g., date-range payroll computation). It retains the same gross logic
     * (hourlyRate × totalHours) while avoiding duplicated parsing.</p>
     *
     * @param employee employee object
     * @param totalHours total work hours for the target period
     * @return gross pay
     */
    public double calculate(Employee employee, double totalHours) {
        reset();

        if (employee == null) return 0;

        this.employeeID = employee.getEmployeeNumber();
        this.employeeName = employee.getLastName() + ", " + employee.getFirstName();

        Salary salary = employee.getSalary();
        if (salary == null) return 0;

        this.hourly = salary.getHourlyRate();
        this.hours = Math.max(0, totalHours);
        this.gross = this.hourly * this.hours;

        return this.gross;
    }

    /**
     * Reusable hour computation used across the payroll services.
     * Kept public to avoid duplicating parsing logic in multiple classes.
     */
    public static double computeHoursPublic(String timeIn, String timeOut) {
        return computeHours(timeIn, timeOut);
    }




    /**
     * Handles reset.
     */
    private void reset() {
        employeeID = null;
        employeeName = null;
        hourly = 0;
        hours = 0;
        gross = 0;
    }

    /**
     * Finds employee.
     * @param employees input value needed by this method.
     * @param empNo input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static Employee findEmployee(List<Employee> employees, String empNo) {
        for (Employee e : employees) {
            if (e == null) continue;
            if (Objects.equals(e.getEmployeeNumber().trim(), empNo.trim())) {
                return e;
            }
        }
        return null;
    }

    /**
     * Checks whether in month is true.
     * @param date input value needed by this method.
     * @param targetMonth input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static boolean isInMonth(String date, int targetMonth) {
        if (date == null || date.length() < 2) return false;

        try {
            int month = Integer.parseInt(date.substring(0, 2));
            return month == targetMonth;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Computes hours.
     * @param tin input value needed by this method.
     * @param tout input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static double computeHours(String tin, String tout) {
        if (tin == null || tout == null || tin.isBlank() || tout.isBlank()) return 0;

        try {
            LocalTime in = LocalTime.parse(tin);
            LocalTime out = LocalTime.parse(tout);

            long minutes = Duration.between(in, out).toMinutes();
            if (minutes < 0) return 0;

            return minutes / 60.0;
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * Returns employee id.
     * @return resulting value produced by this method.
     */
    public String getEmployeeID() {
        return employeeID;
    }

    /**
     * Returns employee name.
     * @return resulting value produced by this method.
     */
    public String getEmployeeName() {
        return employeeName;
    }

    /**
     * Returns hourly.
     * @return resulting value produced by this method.
     */
    public double getHourly() {
        return hourly;
    }

    /**
     * Returns hours.
     * @return resulting value produced by this method.
     */
    public double getHours() {
        return hours;
    }

    /**
     * Returns gross.
     * @return resulting value produced by this method.
     */
    public double getGross() {
        return gross;
    }
}
