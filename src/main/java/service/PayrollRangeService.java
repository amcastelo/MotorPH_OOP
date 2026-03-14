package service;

import model.AttendanceEntry;
import model.Employee;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Represents the payroll range service component used in the service layer.
 */
public class PayrollRangeService {

    private final AttendanceService attendanceService;

    /**
     * Creates a new PayrollRangeService instance.
     * @param attendanceService input value needed by this method.
     */
    public PayrollRangeService(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * Computes the required data.
     * @param employee input value needed by this method.
     * @param start input value needed by this method.
     * @param end input value needed by this method.
     * @return resulting value produced by this method.
     */
    public PayrollResult compute(Employee employee, LocalDate start, LocalDate end) {
        if (employee == null) throw new IllegalArgumentException("employee is required");
        if (start == null || end == null) throw new IllegalArgumentException("start/end are required");
        if (end.isBefore(start)) throw new IllegalArgumentException("end must not be earlier than start");

        // Attendance breakdown filtered to date range (retain existing logic)
        List<AttendanceEntry> all = attendanceService.getAllFor(employee);
        List<PayrollRow> rows = new ArrayList<>();

        double totalHours = 0;
        long totalLateMins = 0;

        for (AttendanceEntry a : all) {
            LocalDate d = parseAttendanceDate(a.getDate());
            if (d == null) continue;
            if (d.isBefore(start) || d.isAfter(end)) continue;

            // Reuse shared helpers from existing services to avoid duplicate parsing logic
            double hrs = Grosswage.computeHoursPublic(a.getTimeIn(), a.getTimeOut());
            long lateMins = LatePenalty.computeLateMinutesPublic(a.getTimeIn(), LocalTime.of(8, 0));

            rows.add(new PayrollRow(a.getDate(), a.getTimeIn(), a.getTimeOut(), hrs, lateMins));
            totalHours += hrs;
            totalLateMins += lateMins;
        }

        // Compute gross + hourly rate using the existing Grosswage service
        Grosswage grosswage = new Grosswage(attendanceService);
        double gross = grosswage.calculate(employee, totalHours);
        double hourly = grosswage.getHourly();

        // Compute late penalty using the existing LatePenalty service
        LatePenalty latePenaltyService = new LatePenalty(attendanceService);
        double latePenalty = latePenaltyService.calculate(employee, totalLateMins);

        // Government contributions (existing deduction classes)
        DeductionCalculation sssCalc = new SSS();
        DeductionCalculation philCalc = new Philhealth();
        DeductionCalculation pagibigCalc = new Pagibig();

        double sss = sssCalc.calculate(gross);
        double phil = philCalc.calculate(gross);
        double pagibig = pagibigCalc.calculate(gross);

        // Compute taxable income / withholding tax / net using the existing Netwage + WithholdingTax rules
        Netwage netwage = new Netwage(grosswage, latePenaltyService);
        double net = netwage.calculateFromAmounts(employee, gross, hourly, sss, phil, pagibig, latePenalty);

        double taxableIncome = netwage.getTaxableIncome();
        double withholdingTax = netwage.getTax();

        return new PayrollResult(
                employee.getEmployeeNumber(),
                monthYearLabel(start),
                start,
                end,
                hourly,
                totalHours,
                gross,
                sss,
                phil,
                pagibig,
                latePenalty,
                taxableIncome,
                withholdingTax,
                net,
                rows
        );
    }

    /**
     * Parses attendance date.
     * @param date input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static LocalDate parseAttendanceDate(String date) {
        if (date == null || date.isBlank()) return null;
        try {
            return LocalDate.parse(date.trim(), DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Handles month year label.
     * @param d input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String monthYearLabel(LocalDate d) {
        String mon = d.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        mon = mon.substring(0, 1).toUpperCase(Locale.ENGLISH) + mon.substring(1).toLowerCase(Locale.ENGLISH);
        return mon + d.getYear();
    }

    /**
     * Handles payroll row.
     * @param date input value needed by this method.
     * @param timeIn input value needed by this method.
     * @param timeOut input value needed by this method.
     * @param hours input value needed by this method.
     * @param lateMinutes input value needed by this method.
     * @return resulting value produced by this method.
     */
    public record PayrollRow(String date, String timeIn, String timeOut, double hours, long lateMinutes) {}

    /**
     * Handles payroll result.
     * @param employeeId input value needed by this method.
     * @param monthYear input value needed by this method.
     * @param start input value needed by this method.
     * @param end input value needed by this method.
     * @param hourlyRate input value needed by this method.
     * @param totalHours input value needed by this method.
     * @param gross input value needed by this method.
     * @param sss input value needed by this method.
     * @param philhealth input value needed by this method.
     * @param pagibig input value needed by this method.
     * @param latePenalty input value needed by this method.
     * @param taxableIncome input value needed by this method.
     * @param withholdingTax input value needed by this method.
     * @param net input value needed by this method.
     * @param rows input value needed by this method.
     * @return resulting value produced by this method.
     */
    public record PayrollResult(
            String employeeId,
            String monthYear,
            LocalDate start,
            LocalDate end,
            double hourlyRate,
            double totalHours,
            double gross,
            double sss,
            double philhealth,
            double pagibig,
            double latePenalty,
            double taxableIncome,
            double withholdingTax,
            double net,
            List<PayrollRow> rows
    ) {}
}
