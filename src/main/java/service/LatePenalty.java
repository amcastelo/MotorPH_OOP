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
 * Represents the late penalty component used in the service layer.
 */
public class LatePenalty {

    private final AttendanceService attendanceService;


    private final LocalTime expectedStartTime;


    private String employeeID;
    private String employeeName;

    private double hourlyRate;
    private long totalLateMinutes;
    private double totalPenalty;

    /**
     * Creates a new LatePenalty instance.
     * @param attendanceService input value needed by this method.
     */
    public LatePenalty(AttendanceService attendanceService) {
        this(attendanceService, LocalTime.of(8, 0));
    }

    /**
     * Creates a new LatePenalty instance.
     * @param attendanceService input value needed by this method.
     * @param expectedStartTime input value needed by this method.
     */
    public LatePenalty(AttendanceService attendanceService, LocalTime expectedStartTime) {
        this.attendanceService = attendanceService;
        this.expectedStartTime = expectedStartTime == null ? LocalTime.of(8, 0) : expectedStartTime;
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

        this.hourlyRate = salary.getHourlyRate();

        List<AttendanceEntry> entries = attendanceService.getAllFor(employee);

        long lateMinutes = 0;

        for (AttendanceEntry a : entries) {
            if (!isInMonth(a.getDate(), month)) continue;

            long mins = computeLateMinutes(a.getTimeIn(), expectedStartTime);
            lateMinutes += mins;
        }

        this.totalLateMinutes = lateMinutes;

        this.totalPenalty = hourlyRate * (lateMinutes / 60.0);

        return this.totalPenalty;
    }


/**
 * Calculates late penalty using an already-computed total late minutes value.
 *
 * <p>This overload is intended for scenarios where attendance is filtered elsewhere
 * (e.g., date-range payroll computation). It retains the same logic
 * (hourlyRate × lateMinutes/60) while avoiding duplicated parsing.</p>
 *
 * @param employee employee object
 * @param lateMinutes total late minutes for the target period
 * @return late penalty amount
 */
public double calculate(Employee employee, long lateMinutes) {
    reset();

    if (employee == null) return 0;

    this.employeeID = employee.getEmployeeNumber();
    this.employeeName = employee.getLastName() + ", " + employee.getFirstName();

    Salary salary = employee.getSalary();
    if (salary == null) return 0;

    this.hourlyRate = salary.getHourlyRate();

    this.totalLateMinutes = Math.max(0, lateMinutes);
    this.totalPenalty = hourlyRate * (this.totalLateMinutes / 60.0);

    return this.totalPenalty;
}

/**
 * Reusable late-minutes computation used across the payroll services.
 * Kept public to avoid duplicating parsing logic in multiple classes.
 */
public static long computeLateMinutesPublic(String timeIn, LocalTime expectedStart) {
    return computeLateMinutes(timeIn, expectedStart);
}


    /**
     * Handles reset.
     */
    private void reset() {
        employeeID = null;
        employeeName = null;
        hourlyRate = 0;
        totalLateMinutes = 0;
        totalPenalty = 0;
    }

    /**
     * Finds employee.
     * @param employees input value needed by this method.
     * @param empNo input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static Employee findEmployee(List<Employee> employees, String empNo) {
        String needle = safeTrim(empNo);
        for (Employee e : employees) {
            if (e == null) continue;
            if (Objects.equals(safeTrim(e.getEmployeeNumber()), needle)) return e;
        }
        return null;
    }

    /**
     * Computes late minutes.
     * @param timeIn input value needed by this method.
     * @param expectedStart input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static long computeLateMinutes(String timeIn, LocalTime expectedStart) {
        LocalTime in = parseTime(timeIn);
        if (in == null) return 0;

        if (in.isAfter(expectedStart)) {
            return Duration.between(expectedStart, in).toMinutes();
        }
        return 0;
    }

    /**
     * Parses time.
     * @param s input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static LocalTime parseTime(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;


        try { return LocalTime.parse(t); } catch (Exception ignored) {}
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
            int m = Integer.parseInt(date.substring(0, 2));
            return m == targetMonth;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Handles safe trim.
     * @param s input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    /**
     * Returns employee id.
     * @return resulting value produced by this method.
     */
    public String getEmployeeID() { return employeeID; }
    
    /**
     * Returns employee name.
     * @return resulting value produced by this method.
     */
    public String getEmployeeName() { return employeeName; }

    /**
     * Returns hourly rate.
     * @return resulting value produced by this method.
     */
    public double getHourlyRate() { return hourlyRate; }

    /**
     * Returns total late minutes.
     * @return resulting value produced by this method.
     */
    public long getTotalLateMinutes() { return totalLateMinutes; }
    
    /**
     * Returns total penalty.
     * @return resulting value produced by this method.
     */
    public double getTotalPenalty() { return totalPenalty; }
}
