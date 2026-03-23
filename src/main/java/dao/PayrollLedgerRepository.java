package dao;

import model.PayrollRecord;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the payroll ledger repository component used in the dao layer.
 */
public class PayrollLedgerRepository {

    private final Path ledgerPath;

    /**
     * Creates a new PayrollLedgerRepository instance.
     */
    public PayrollLedgerRepository() {
        this(getDefaultLedgerPath());
    }

    /**
     * Creates a new PayrollLedgerRepository instance.
     * @param ledgerPath input value needed by this method.
     */
    public PayrollLedgerRepository(Path ledgerPath) {
        this.ledgerPath = ledgerPath;
    }

    /**
     * Returns ledger path.
     * @return resulting value produced by this method.
     */
    public Path getLedgerPath() {
        return ledgerPath;
    }

    /**
     * Handles append.
     * @param record input value needed by this method.
     */
    public void append(PayrollRecord record) throws Exception {
        if (record == null) return;

        Files.createDirectories(ledgerPath.getParent());

        boolean exists = Files.exists(ledgerPath);
        if (!exists) {
            Files.write(
                    ledgerPath,
                    List.of(PayrollRecord.CSV_HEADER),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        }
        replaceOrAppend(record);
    }

    /**
     * Handles list for employee.
     * @param employeeId input value needed by this method.
     * @return resulting value produced by this method.
     */
    public List<PayrollRecord> listForEmployee(String employeeId) {
        List<PayrollRecord> out = new ArrayList<>();
        if (employeeId == null || employeeId.isBlank()) return out;
        if (!Files.exists(ledgerPath)) return out;

        try {
            List<String> lines = Files.readAllLines(ledgerPath, StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {
                PayrollRecord r = PayrollRecord.fromCsvRow(lines.get(i));
                if (r == null) continue;
                if (employeeId.trim().equalsIgnoreCase(r.getEmployeeId())) {
                    out.add(r);
                }
            }
        } catch (Exception ignored) {
        }
        return out;
    }    

    /**
     * Finds by employee and period.
     * @param employeeId input value needed by this method.
     * @param payPeriod input value needed by this method.
     * @return resulting value produced by this method.
     */
    public PayrollRecord findByEmployeeAndPeriod(String employeeId, String payPeriod) {
        if (employeeId == null || employeeId.isBlank()) return null;
        if (payPeriod == null || payPeriod.isBlank()) return null;
        if (!Files.exists(ledgerPath)) return null;

        try {
            List<String> lines = Files.readAllLines(ledgerPath, StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {
                PayrollRecord r = PayrollRecord.fromCsvRow(lines.get(i));
                if (r == null) continue;
                if (employeeId.trim().equalsIgnoreCase(r.getEmployeeId())
                        && payPeriod.trim().equalsIgnoreCase(r.getPayPeriod())) {
                    return r;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * Handles replace or append.
     * @param record input value needed by this method.
     */
    private void replaceOrAppend(PayrollRecord record) throws Exception {
        List<String> lines = Files.readAllLines(ledgerPath, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            lines = new ArrayList<>();
            lines.add(PayrollRecord.CSV_HEADER);
        }

        String keyEmp = record.getEmployeeId().trim();
        String keyPeriod = record.getPayPeriod().trim();

        boolean replaced = false;
        for (int i = 1; i < lines.size(); i++) {
            PayrollRecord existing = PayrollRecord.fromCsvRow(lines.get(i));
            if (existing == null) continue;
            if (keyEmp.equalsIgnoreCase(existing.getEmployeeId())
                    && keyPeriod.equalsIgnoreCase(existing.getPayPeriod())) {
                lines.set(i, record.toCsvRow());
                replaced = true;
                break;
            }
        }

        if (!replaced) {
            lines.add(record.toCsvRow());
        }

        Files.write(
                ledgerPath,
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE
        );
    }

    /**
     * Returns default ledger path.
     * @return resulting value produced by this method.
     */
    private static Path getDefaultLedgerPath() {
        return Paths.get("data", "payroll_ledger.csv");
    }
}
