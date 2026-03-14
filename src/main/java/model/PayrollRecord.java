package model;

import java.time.OffsetDateTime;
import java.util.Locale;

/**
 * Represents the payroll record component used in the model layer.
 */
public class PayrollRecord {

    /**
     * Handles join.
     * @param " input value needed by this method.
     * @param " input value needed by this method.
     * @param "employeeId" input value needed by this method.
     * @param "lastName" input value needed by this method.
     * @param "firstName" input value needed by this method.
     * @param "position" input value needed by this method.
     * @param "hourlyRate" input value needed by this method.
     * @param "payPeriod" input value needed by this method.
     * @param "totalHours" input value needed by this method.
     * @param "gross" input value needed by this method.
     * @param "net" input value needed by this method.
     * @param "sss" input value needed by this method.
     * @param "philhealth" input value needed by this method.
     * @param "pagibig" input value needed by this method.
     * @param "latePenalty" input value needed by this method.
     * @param "taxableIncome" input value needed by this method.
     * @param "withholdingTax" input value needed by this method.
     * @param employeeId input value needed by this method.
     * @param lastName input value needed by this method.
     * @param firstName input value needed by this method.
     * @param position input value needed by this method.
     * @param hourlyRate input value needed by this method.
     * @param payPeriod input value needed by this method.
     * @param totalHours input value needed by this method.
     * @param gross input value needed by this method.
     * @param net input value needed by this method.
     * @param sss input value needed by this method.
     * @param philhealth input value needed by this method.
     * @param pagibig input value needed by this method.
     * @param latePenalty input value needed by this method.
     * @param taxableIncome input value needed by this method.
     * @param withholdingTax input value needed by this method.
     * @param createdAt input value needed by this method.
     * @return resulting value produced by this method.
     */
    public static final String CSV_HEADER = String.join(",",
            "employeeId",
            "lastName",
            "firstName",
            "position",
            "hourlyRate",
            "payPeriod",
            "totalHours",
            "gross",
            "net",
            "sss",
            "philhealth",
            "pagibig",
            "latePenalty",
            "taxableIncome",
            "withholdingTax",
            "createdAt"
    );

    private final String employeeId;
    private final String lastName;
    private final String firstName;
    private final String position;
    private final double hourlyRate;

    private final String payPeriod;
    private final double totalHours;
    private final double gross;
    private final double net;
    private final double sss;
    private final double philhealth;
    private final double pagibig;
    private final double latePenalty;
    private final double taxableIncome;
    private final double withholdingTax;
    private final OffsetDateTime createdAt;

    public PayrollRecord(
            String employeeId,
            String lastName,
            String firstName,
            String position,
            double hourlyRate,
            String payPeriod,
            double totalHours,
            double gross,
            double net,
            double sss,
            double philhealth,
            double pagibig,
            double latePenalty,
            double taxableIncome,
            double withholdingTax,
            OffsetDateTime createdAt
    ) {
        this.employeeId = safe(employeeId);
        this.lastName = safe(lastName);
        this.firstName = safe(firstName);
        this.position = safe(position);
        this.hourlyRate = hourlyRate;
        this.payPeriod = safe(payPeriod);
        this.totalHours = totalHours;
        this.gross = gross;
        this.net = net;
        this.sss = sss;
        this.philhealth = philhealth;
        this.pagibig = pagibig;
        this.latePenalty = latePenalty;
        this.taxableIncome = taxableIncome;
        this.withholdingTax = withholdingTax;
        this.createdAt = (createdAt == null) ? OffsetDateTime.now() : createdAt;
    }

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
     * Returns total hours.
     * @return resulting value produced by this method.
     */
    public double getTotalHours() { return totalHours; }
    
    /**
     * Returns gross.
     * @return resulting value produced by this method.
     */
    public double getGross() { return gross; }
    
    /**
     * Returns net.
     * @return resulting value produced by this method.
     */
    public double getNet() { return net; }
    
    /**
     * Returns sss.
     * @return resulting value produced by this method.
     */
    public double getSss() { return sss; }
    
    /**
     * Returns philhealth.
     * @return resulting value produced by this method.
     */
    public double getPhilhealth() { return philhealth; }
    
    /**
     * Returns pagibig.
     * @return resulting value produced by this method.
     */
    public double getPagibig() { return pagibig; }
    
    /**
     * Returns late penalty.
     * @return resulting value produced by this method.
     */
    public double getLatePenalty() { return latePenalty; }
    
    /**
     * Returns taxable income.
     * @return resulting value produced by this method.
     */
    public double getTaxableIncome() { return taxableIncome; }
    
    /**
     * Returns withholding tax.
     * @return resulting value produced by this method.
     */
    public double getWithholdingTax() { return withholdingTax; }
    
    /**
     * Returns created at.
     * @return resulting value produced by this method.
     */
    public OffsetDateTime getCreatedAt() { return createdAt; }
    

    /**
     * Handles to csv row.
     * @return resulting value produced by this method.
     */
    public String toCsvRow() {
        return String.join(",",
                csv(employeeId),
                csv(lastName),
                csv(firstName),
                csv(position),
                csv(String.format(Locale.US, "%.2f", hourlyRate)),
                csv(payPeriod),
                csv(String.format(Locale.US, "%.2f", totalHours)),
                csv(String.format(Locale.US, "%.2f", gross)),
                csv(String.format(Locale.US, "%.2f", net)),
                csv(String.format(Locale.US, "%.2f", sss)),
                csv(String.format(Locale.US, "%.2f", philhealth)),
                csv(String.format(Locale.US, "%.2f", pagibig)),
                csv(String.format(Locale.US, "%.2f", latePenalty)),
                csv(String.format(Locale.US, "%.2f", taxableIncome)),
                csv(String.format(Locale.US, "%.2f", withholdingTax)),
                csv(createdAt.toString())
        );
    }

    /**
     * Handles from csv row.
     * @param line input value needed by this method.
     * @return resulting value produced by this method.
     */
    public static PayrollRecord fromCsvRow(String line) {
        if (line == null || line.isBlank()) return null;
        String[] parts = splitCsv(line);
        if (parts.length < 16) return null;

        return new PayrollRecord(
                parts[0],
                parts[1],
                parts[2],
                parts[3],
                parseDouble(parts[4]),
                parts[5],
                parseDouble(parts[6]),
                parseDouble(parts[7]),
                parseDouble(parts[8]),
                parseDouble(parts[9]),
                parseDouble(parts[10]),
                parseDouble(parts[11]),
                parseDouble(parts[12]),
                parseDouble(parts[13]),
                parseDouble(parts[14]),
                parseDate(parts[15])
        );
    }

    /**
     * Handles safe.
     * @param s.trim( input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String safe(String s) { return s == null ? "" : s.trim(); }

    /**
     * Parses double.
     * @param s input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static double parseDouble(String s) {
        try { return Double.parseDouble(safe(s)); } catch (Exception e) { return 0; }
    }

    /**
     * Parses date.
     * @param s input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static OffsetDateTime parseDate(String s) {
        try { return OffsetDateTime.parse(safe(s)); } catch (Exception e) { return OffsetDateTime.now(); }
    }

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
     * Handles split csv.
     * @param line input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String[] splitCsv(String line) {
        StringBuilder cur = new StringBuilder();
        java.util.List<String> out = new java.util.ArrayList<>();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cur.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }
}
