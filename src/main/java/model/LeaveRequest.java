package model;

import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.Locale;

/**
 * Represents the leave request component used in the model layer.
 */
public class LeaveRequest {

    public static final String CSV_HEADER =
            "requestId,employeeId,lastName,firstName,position,hourlyRate,payPeriod,startDate,endDate,leaveType,reason,status,hrComments,createdAt,updatedAt";

    private final String requestId;
    private final String employeeId;
    private final String lastName;
    private final String firstName;
    private final String position;
    private final double hourlyRate;

    private final String payPeriod;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String leaveType;
    private final String reason;

    private LeaveStatus status;
    private String hrComments;

    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    /**
     * Creates a new LeaveRequest instance.
     * @param requestId input value needed by this method.
     * @param employeeId input value needed by this method.
     * @param lastName input value needed by this method.
     * @param firstName input value needed by this method.
     * @param position input value needed by this method.
     * @param hourlyRate input value needed by this method.
     * @param payPeriod input value needed by this method.
     * @param startDate input value needed by this method.
     * @param endDate input value needed by this method.
     * @param leaveType input value needed by this method.
     * @param reason input value needed by this method.
     * @param status input value needed by this method.
     * @param hrComments input value needed by this method.
     * @param createdAt input value needed by this method.
     * @param updatedAt input value needed by this method.
     */
    public LeaveRequest(String requestId,
                        String employeeId,
                        String lastName,
                        String firstName,
                        String position,
                        double hourlyRate,
                        String payPeriod,
                        LocalDate startDate,
                        LocalDate endDate,
                        String leaveType,
                        String reason,
                        LeaveStatus status,
                        String hrComments,
                        OffsetDateTime createdAt,
                        OffsetDateTime updatedAt) {
        this.requestId = safe(requestId);
        this.employeeId = safe(employeeId);
        this.lastName = safe(lastName);
        this.firstName = safe(firstName);
        this.position = safe(position);
        this.hourlyRate = hourlyRate;
        this.payPeriod = safe(payPeriod);
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveType = safe(leaveType);
        this.reason = safe(reason);
        this.status = (status == null) ? LeaveStatus.PENDING : status;
        this.hrComments = safe(hrComments);
        this.createdAt = (createdAt == null) ? OffsetDateTime.now() : createdAt;
        this.updatedAt = (updatedAt == null) ? this.createdAt : updatedAt;
    }

    /**
     * Handles new pending.
     * @param e input value needed by this method.
     * @param start input value needed by this method.
     * @param end input value needed by this method.
     * @param leaveType input value needed by this method.
     * @param reason input value needed by this method.
     * @return resulting value produced by this method.
     */
    public static LeaveRequest newPending(Employee e, LocalDate start, LocalDate end, String leaveType, String reason) {
        String id = java.util.UUID.randomUUID().toString();
        String payPeriod = monthYearLabel(start);
        double hourly = (e != null && e.getSalary() != null) ? e.getSalary().getHourlyRate() : 0.0;
        OffsetDateTime now = OffsetDateTime.now();
        return new LeaveRequest(
                id,
                e == null ? "" : safe(e.getEmployeeNumber()),
                e == null ? "" : safe(e.getLastName()),
                e == null ? "" : safe(e.getFirstName()),
                e == null ? "" : safe(e.getPosition()),
                hourly,
                payPeriod,
                start,
                end,
                leaveType,
                reason,
                LeaveStatus.PENDING,
                "",
                now,
                now
        );
    }

    /**
     * Returns request id.
     * @return resulting value produced by this method.
     */
    public String getRequestId() { return requestId; }
    
    /**
     * Returns employee id.
     * @return resulting value produced by this method.
     */
    public String getEmployeeId() { return employeeId; }
    
    /**
     * Returns last name.
     * @return resulting value produced by this method.
     */
    public String getLastName() { return lastName; }
    
    /**
     * Returns first name.
     * @return resulting value produced by this method.
     */
    public String getFirstName() { return firstName; }
    
    /**
     * Returns position.
     * @return resulting value produced by this method.
     */
    public String getPosition() { return position; }
    
    /**
     * Returns hourly rate.
     * @return resulting value produced by this method.
     */
    public double getHourlyRate() { return hourlyRate; }
    
    /**
     * Returns pay period.
     * @return resulting value produced by this method.
     */
    public String getPayPeriod() { return payPeriod; }
    
    /**
     * Returns start date.
     * @return resulting value produced by this method.
     */
    public LocalDate getStartDate() { return startDate; }
    
    /**
     * Returns end date.
     * @return resulting value produced by this method.
     */
    public LocalDate getEndDate() { return endDate; }
    
    /**
     * Returns leave type.
     * @return resulting value produced by this method.
     */
    public String getLeaveType() { return leaveType; }
    
    /**
     * Returns reason.
     * @return resulting value produced by this method.
     */
    public String getReason() { return reason; }
    
    /**
     * Returns status.
     * @return resulting value produced by this method.
     */
    public LeaveStatus getStatus() { return status; }
    
    /**
     * Returns hr comments.
     * @return resulting value produced by this method.
     */
    public String getHrComments() { return hrComments; }
    
    /**
     * Returns created at.
     * @return resulting value produced by this method.
     */
    public OffsetDateTime getCreatedAt() { return createdAt; }
    
    /**
     * Returns updated at.
     * @return resulting value produced by this method.
     */
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    /**
     * Approves the required data.
     * @param hrComments input value needed by this method.
     */
    public void approve(String hrComments) {
        this.status = LeaveStatus.APPROVED;
        this.hrComments = safe(hrComments);
        this.updatedAt = OffsetDateTime.now();
    }

    /**
     * Denies the required data.
     * @param hrComments input value needed by this method.
     */
    public void deny(String hrComments) {
        this.status = LeaveStatus.DENIED;
        this.hrComments = safe(hrComments);
        this.updatedAt = OffsetDateTime.now();
    }

    /**
     * Handles to csv row.
     * @return resulting value produced by this method.
     */
    public String toCsvRow() {
        return String.join(",",
                csv(requestId),
                csv(employeeId),
                csv(lastName),
                csv(firstName),
                csv(position),
                csv(String.format(Locale.US, "%.2f", hourlyRate)),
                csv(payPeriod),
                csv(startDate == null ? "" : startDate.toString()),
                csv(endDate == null ? "" : endDate.toString()),
                csv(leaveType),
                csv(reason),
                csv(status == null ? LeaveStatus.PENDING.name() : status.name()),
                csv(hrComments),
                csv(createdAt == null ? "" : createdAt.toString()),
                csv(updatedAt == null ? "" : updatedAt.toString())
        );
    }

    /**
     * Handles from csv row.
     * @param row input value needed by this method.
     * @return resulting value produced by this method.
     */
    public static LeaveRequest fromCsvRow(String row) {
        if (row == null || row.isBlank()) return null;
        String[] parts = parseCsv(row);
        if (parts.length < 15) return null;
        try {
            String requestId = parts[0];
            String employeeId = parts[1];
            String lastName = parts[2];
            String firstName = parts[3];
            String position = parts[4];
            double hourlyRate = parseDouble(parts[5]);
            String payPeriod = parts[6];
            LocalDate start = parseDate(parts[7]);
            LocalDate end = parseDate(parts[8]);
            String leaveType = parts[9];
            String reason = parts[10];
            LeaveStatus status = LeaveStatus.fromString(parts[11]);
            String hrComments = parts[12];
            OffsetDateTime createdAt = parseOdt(parts[13]);
            OffsetDateTime updatedAt = parseOdt(parts[14]);
            return new LeaveRequest(requestId, employeeId, lastName, firstName, position, hourlyRate, payPeriod,
                    start, end, leaveType, reason, status, hrComments, createdAt, updatedAt);
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Handles month year label.
     * @param d input value needed by this method.
     * @return resulting value produced by this method.
     */
    public static String monthYearLabel(LocalDate d) {
        if (d == null) return "";
        String mon = d.getMonth().getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH);
        mon = mon.substring(0, 1).toUpperCase(Locale.ENGLISH) + mon.substring(1).toLowerCase(Locale.ENGLISH);
        return mon + d.getYear();
    }

    /**
     * Parses date.
     * @param s input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s.trim()); } catch (Exception ignored) { return null; }
    }

    /**
     * Parses odt.
     * @param s input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static OffsetDateTime parseOdt(String s) {
        if (s == null || s.isBlank()) return null;
        try { return OffsetDateTime.parse(s.trim()); } catch (Exception ignored) { return null; }
    }

    /**
     * Parses double.
     * @param s input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static double parseDouble(String s) {
        if (s == null || s.isBlank()) return 0;
        try { return Double.parseDouble(s.trim()); } catch (Exception ignored) { return 0; }
    }

    /**
     * Handles safe.
     * @param s.trim( input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String safe(String s) { return s == null ? "" : s.trim(); }

    /**
     * Handles csv.
     * @param s input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String csv(String s) {
        if (s == null) return "";
        boolean needs = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        if (!needs) return s;
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    /**
     * Parses csv.
     * @param line input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String[] parseCsv(String line) {
        java.util.List<String> out = new java.util.ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    cur.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    out.add(cur.toString());
                    cur.setLength(0);
                } else {
                    cur.append(c);
                }
            }
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }
}
