package dao;

import model.LeaveRequest;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the leave ledger repository component used in the dao layer.
 */
public class LeaveLedgerRepository {

    private final Path ledgerPath;

    /**
     * Creates a new LeaveLedgerRepository instance.
     */
    public LeaveLedgerRepository() {
        this(getDefaultLedgerPath());
    }

    /**
     * Creates a new LeaveLedgerRepository instance.
     * @param ledgerPath input value needed by this method.
     */
    public LeaveLedgerRepository(Path ledgerPath) {
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
     * Handles upsert.
     * @param request input value needed by this method.
     */
    public void upsert(LeaveRequest request) throws Exception {
        if (request == null) return;

        Files.createDirectories(ledgerPath.getParent());

        if (!Files.exists(ledgerPath)) {
            Files.write(
                    ledgerPath,
                    List.of(LeaveRequest.CSV_HEADER),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        }

        replaceOrAppend(request);
    }

    /**
     * Handles list for employee.
     * @param employeeId input value needed by this method.
     * @return resulting value produced by this method.
     */
    public List<LeaveRequest> listForEmployee(String employeeId) {
        List<LeaveRequest> out = new ArrayList<>();
        if (employeeId == null || employeeId.isBlank()) return out;
        if (!Files.exists(ledgerPath)) return out;

        try {
            List<String> lines = Files.readAllLines(ledgerPath, StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {
                LeaveRequest r = LeaveRequest.fromCsvRow(lines.get(i));
                if (r == null) continue;
                if (employeeId.trim().equalsIgnoreCase(r.getEmployeeId())) {
                    out.add(r);
                }
            }
        } catch (Exception ignored) {}
        return out;
    }

    /**
     * Handles list all.
     * @return resulting value produced by this method.
     */
    public List<LeaveRequest> listAll() {
        List<LeaveRequest> out = new ArrayList<>();
        if (!Files.exists(ledgerPath)) return out;

        try {
            List<String> lines = Files.readAllLines(ledgerPath, StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {
                LeaveRequest r = LeaveRequest.fromCsvRow(lines.get(i));
                if (r != null) out.add(r);
            }
        } catch (Exception ignored) {}
        return out;
    }

    /**
     * Finds by employee and period.
     * @param employeeId input value needed by this method.
     * @param payPeriod input value needed by this method.
     * @return resulting value produced by this method.
     */
    public LeaveRequest findByEmployeeAndPeriod(String employeeId, String payPeriod) {
        if (employeeId == null || employeeId.isBlank()) return null;
        if (payPeriod == null || payPeriod.isBlank()) return null;
        if (!Files.exists(ledgerPath)) return null;

        try {
            List<String> lines = Files.readAllLines(ledgerPath, StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {
                LeaveRequest r = LeaveRequest.fromCsvRow(lines.get(i));
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
     * @param request input value needed by this method.
     */
    private void replaceOrAppend(LeaveRequest request) throws Exception {
        List<String> lines = Files.readAllLines(ledgerPath, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            lines = new ArrayList<>();
            lines.add(LeaveRequest.CSV_HEADER);
        }

        String keyId = request.getRequestId().trim();
        boolean replaced = false;

        for (int i = 1; i < lines.size(); i++) {
            LeaveRequest existing = LeaveRequest.fromCsvRow(lines.get(i));
            if (existing == null) continue;
            if (keyId.equalsIgnoreCase(existing.getRequestId().trim())) {
                lines.set(i, request.toCsvRow());
                replaced = true;
                break;
            }
        }

        if (!replaced) {
            lines.add(request.toCsvRow());
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
        return Paths.get(System.getProperty("user.home"), "MotorPH", "leave_ledger.csv");
    }
}
