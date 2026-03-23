package swingui;

import com.toedter.calendar.JDateChooser;
import dao.PayrollLedgerRepository;
import model.Employee;
import model.PayrollRecord;
import model.Salary;
import service.PayrollRangeService;
import service.AttendanceService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Represents the payroll processing page component used in the swingui layer.
 */
public class PayrollProcessingPage extends JPanel {

    private final PayrollRangeService payrollService;
    private final List<Employee> employees;
    private final PayrollLedgerRepository ledgerRepository;
    private final Runnable onPayrollSaved;


    private final JComboBox<EmployeeItem> employeeCombo = new JComboBox<>();


    private final JLabel nameLabel = new JLabel();
    private final JLabel empNoLabel = new JLabel();
    private final JLabel positionLabel = new JLabel();
    private final JLabel hourlyLabel = new JLabel();
    private final JLabel basicSalaryLabel = new JLabel();
    private final JLabel grossSemiLabel = new JLabel();
    private final JLabel sssLabel = new JLabel();
    private final JLabel philLabel = new JLabel();
    private final JLabel pagibigLabel = new JLabel();
    private final JLabel tinLabel = new JLabel();


    private final JDateChooser startChooser = new JDateChooser();
    private final JDateChooser endChooser = new JDateChooser();
    private final JButton computeBtn = new JButton("Compute Gross & Net");
    private final JButton printBtn = new JButton("Print (Save to Ledger)");

    private PayrollRangeService.PayrollResult lastResult;


    private final JLabel periodLabel = new JLabel("Pay Period: -");
    private final JLabel hoursLabel = new JLabel("Total Hours: -");
    private final JLabel grossLabel = new JLabel("Gross: -");
    private final JLabel sssDedLabel = new JLabel("SSS: -");
    private final JLabel philDedLabel = new JLabel("PhilHealth: -");
    private final JLabel pagibigDedLabel = new JLabel("Pag-IBIG: -");
    private final JLabel lateLabel = new JLabel("Late Penalty: -");
    private final JLabel taxableLabel = new JLabel("Taxable Income: -");
    private final JLabel taxLabel = new JLabel("Withholding Tax: -");
    private final JLabel netLabel = new JLabel("Net: -");

/**
 * Handles default table model.
 * @param Object{"Date" input value needed by this method.
 * @param In" input value needed by this method.
 * @param Out" input value needed by this method.
 * @param "Hours" input value needed by this method.
 * @param (mins input value needed by this method.
 * @return resulting value produced by this method.
 */
    private final DefaultTableModel attendanceModel = new DefaultTableModel(
            new Object[]{"Date", "Time In", "Time Out", "Hours", "Late (mins)"}, 0
    ) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

/**
 * Creates a new PayrollProcessingPage instance.
 * @param attendanceService input value needed by this method.
 * @param employees input value needed by this method.
 * @param ledgerRepository input value needed by this method.
 * @param onPayrollSaved input value needed by this method.
 */
    public PayrollProcessingPage(AttendanceService attendanceService,
                                List<Employee> employees,
                                PayrollLedgerRepository ledgerRepository,
                                Runnable onPayrollSaved) {
        this.payrollService = new PayrollRangeService(attendanceService);
        this.employees = (employees == null) ? List.of() : employees;
        this.ledgerRepository = (ledgerRepository == null) ? new PayrollLedgerRepository() : ledgerRepository;
        this.onPayrollSaved = onPayrollSaved;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        add(buildTopCard());
        add(Box.createVerticalStrut(16));
        add(buildBottomCard());
        add(Box.createVerticalGlue());

        wireActions();
        loadEmployees();
    }

/**
 * Builds top card.
 * @return resulting value produced by this method.
 */
    private JComponent buildTopCard() {
        JPanel topCard = new JPanel(new BorderLayout(16, 0));
        topCard.setBorder(new EmptyBorder(18, 18, 18, 18));
        topCard.putClientProperty("FlatLaf.style",
                "background: @background; arc: 18; border: 1,1,1,1, fade(@foreground,10%);");


        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Payroll Processing");
        title.putClientProperty("FlatLaf.style", "font: bold 18;");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel selectorRow = new JPanel(new BorderLayout(10, 0));
        selectorRow.setOpaque(false);
        selectorRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel selLbl = new JLabel("Select Employee");
        selLbl.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,70%);");
        employeeCombo.putClientProperty("FlatLaf.style", "arc: 12;");
        selectorRow.add(selLbl, BorderLayout.WEST);
        selectorRow.add(employeeCombo, BorderLayout.CENTER);

        nameLabel.putClientProperty("FlatLaf.style", "font: bold 20;");
        positionLabel.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,75%);");
        empNoLabel.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,75%);");
        hourlyLabel.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,75%);");
        basicSalaryLabel.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,75%);");
        grossSemiLabel.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,75%);");

        JPanel detailsAndGov = new JPanel(new BorderLayout(20, 0));
        detailsAndGov.setOpaque(false);
        detailsAndGov.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsAndGov.setBorder(new EmptyBorder(14, 0, 0, 0));



        JPanel detailsWrapper = new JPanel(new BorderLayout(20, 0));
        detailsWrapper.setOpaque(false);
        detailsWrapper.setLayout(new BoxLayout(detailsWrapper, BoxLayout.Y_AXIS));
        detailsWrapper.add(nameLabel);
        detailsWrapper.add(Box.createVerticalStrut(8));

        JPanel detailsRow = new JPanel(new BorderLayout(20, 0));
        detailsRow.setOpaque(false);

        JPanel detailsCol = new JPanel();
        detailsCol.setOpaque(false);
        detailsCol.setLayout(new BoxLayout(detailsCol, BoxLayout.Y_AXIS));
        detailsCol.add(positionLabel);
        detailsCol.add(empNoLabel);
        detailsCol.add(hourlyLabel);
        detailsCol.add(basicSalaryLabel);
        detailsCol.add(grossSemiLabel);

        JPanel gov = new JPanel();
        gov.setOpaque(false);
        gov.setLayout(new BoxLayout(gov, BoxLayout.Y_AXIS));

        gov.setBorder(new EmptyBorder(0, 0, 0, 0));
        styleMeta(sssLabel);
        styleMeta(philLabel);
        styleMeta(pagibigLabel);
        styleMeta(tinLabel);
        gov.add(sssLabel);
        gov.add(Box.createVerticalStrut(6));
        gov.add(philLabel);
        gov.add(Box.createVerticalStrut(6));
        gov.add(pagibigLabel);
        gov.add(Box.createVerticalStrut(6));
        gov.add(tinLabel);

        detailsRow.add(detailsCol, BorderLayout.CENTER);
        detailsRow.add(gov, BorderLayout.EAST);
        detailsWrapper.add(detailsRow, BorderLayout.WEST);

        detailsAndGov.add(detailsWrapper, BorderLayout.WEST);

        left.add(title);
        left.add(Box.createVerticalStrut(10));
        left.add(selectorRow);
        left.add(detailsAndGov);


        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JLabel rangeTitle = new JLabel("Select Date Range");
        rangeTitle.putClientProperty("FlatLaf.style", "font: bold 14;");
        rangeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel startRow = row("Start", startChooser);
        JPanel endRow = row("End", endChooser);

        computeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        computeBtn.putClientProperty("FlatLaf.style",
                "arc: 12; margin: 10,14,10,14; font: bold 13; background: fade(@accentColor,18%);");
        computeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        printBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        printBtn.putClientProperty("FlatLaf.style", "arc: 12; margin: 10,14,10,14; font: 13;");
        printBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        printBtn.setEnabled(false);

        right.add(rangeTitle);
        right.add(Box.createVerticalStrut(10));
        right.add(startRow);
        right.add(Box.createVerticalStrut(8));
        right.add(endRow);
        right.add(Box.createVerticalStrut(12));
        right.add(computeBtn);
        right.add(Box.createVerticalStrut(8));
        right.add(printBtn);

        topCard.add(left, BorderLayout.CENTER);
        topCard.add(right, BorderLayout.EAST);
        return topCard;
    }

/**
 * Handles row.
 * @param label input value needed by this method.
 * @param chooser input value needed by this method.
 * @return resulting value produced by this method.
 */
    private JPanel row(String label, JDateChooser chooser) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setPreferredSize(new Dimension(44, 22));
        l.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,70%);");
        chooser.setDateFormatString("MM/dd/yyyy");
        chooser.getCalendarButton().putClientProperty("FlatLaf.style", "arc: 12;");
        chooser.getDateEditor().getUiComponent().putClientProperty("FlatLaf.style", "arc: 12;");
        p.add(l, BorderLayout.WEST);
        p.add(chooser, BorderLayout.CENTER);
        return p;
    }

/**
 * Builds bottom card.
 * @return resulting value produced by this method.
 */
    private JComponent buildBottomCard() {
        JPanel bottomCard = new JPanel(new BorderLayout(0, 12));
        bottomCard.setBorder(new EmptyBorder(18, 18, 18, 18));
        bottomCard.putClientProperty("FlatLaf.style",
                "background: @background; arc: 18; border: 1,1,1,1, fade(@foreground,10%);");

        JLabel previewTitle = new JLabel("Preview & Breakdown");
        previewTitle.putClientProperty("FlatLaf.style", "font: bold 16;");
        bottomCard.add(previewTitle, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(16, 0));
        center.setOpaque(false);


        JPanel preview = new JPanel();
        preview.setOpaque(false);
        preview.setLayout(new GridLayout(5, 2, 12, 8));
        preview.setBorder(new EmptyBorder(4, 0, 0, 0));

        styleMeta(periodLabel);
        styleMeta(hoursLabel);
        styleMeta(grossLabel);
        styleMeta(netLabel);
        styleMeta(sssDedLabel);
        styleMeta(philDedLabel);
        styleMeta(pagibigDedLabel);
        styleMeta(lateLabel);
        styleMeta(taxableLabel);
        styleMeta(taxLabel);

        preview.add(periodLabel);
        preview.add(hoursLabel);
        preview.add(grossLabel);
        preview.add(netLabel);
        preview.add(sssDedLabel);
        preview.add(philDedLabel);
        preview.add(pagibigDedLabel);
        preview.add(lateLabel);
        preview.add(taxableLabel);
        preview.add(taxLabel);

        JTable table = new JTable(attendanceModel);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);

        center.add(preview, BorderLayout.WEST);
        center.add(sp, BorderLayout.CENTER);
        bottomCard.add(center, BorderLayout.CENTER);
        return bottomCard;
    }

/**
 * Wires actions.
 */
    private void wireActions() {
        employeeCombo.addActionListener(e -> refreshSelectedEmployee());
        computeBtn.addActionListener(e -> compute());
        printBtn.addActionListener(e -> printToLedger());
    }

/**
 * Loads employees.
 */
    private void loadEmployees() {
        employeeCombo.removeAllItems();

        for (Employee e : employees) {
            if (e == null) continue;
            if ("Employee #".equalsIgnoreCase(safe(e.getEmployeeNumber()))) continue;
            employeeCombo.addItem(new EmployeeItem(e));
        }

        if (employeeCombo.getItemCount() > 0) employeeCombo.setSelectedIndex(0);
        refreshSelectedEmployee();
    }

/**
 * Refreshes selected employee.
 */
    private void refreshSelectedEmployee() {
        Employee emp = getSelectedEmployee();
        if (emp == null) {
            nameLabel.setText("");
            positionLabel.setText("");
            empNoLabel.setText("");
            hourlyLabel.setText("");
            basicSalaryLabel.setText("");
            grossSemiLabel.setText("");
            sssLabel.setText("");
            philLabel.setText("");
            pagibigLabel.setText("");
            tinLabel.setText("");
            clearPreview();
            return;
        }

        nameLabel.setText(emp.getFirstName() + " " + emp.getLastName());
        positionLabel.setText("Position: " + safe(emp.getPosition()));
        empNoLabel.setText("Employee #: " + safe(emp.getEmployeeNumber()));

        Salary s = emp.getSalary();
        double hourly = (s == null) ? 0 : s.getHourlyRate();
        hourlyLabel.setText("Hourly Rate: " + money(hourly));
        double basic = (s == null) ? 0 : s.getBasicSalary();
        double grossSemi = (s == null) ? 0 : s.getGrossSemiMonthlyRate();
        basicSalaryLabel.setText("Basic Salary: " + money(basic));
        grossSemiLabel.setText("Gross Semi-Monthly: " + money(grossSemi));

        sssLabel.setText("SSS: " + safe(emp.getSssNumber()));
        philLabel.setText("PhilHealth: " + safe(emp.getPhilhealthNumber()));
        pagibigLabel.setText("Pag-IBIG: " + safe(emp.getPagIbigNumber()));
        tinLabel.setText("TIN: " + safe(emp.getTinNumber()));

        clearPreview();
    }

/**
 * Computes the required data.
 */
    private void compute() {
        Employee emp = getSelectedEmployee();
        if (emp == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee.");
            return;
        }

        LocalDate start = toLocalDate(startChooser.getDate());
        LocalDate end = toLocalDate(endChooser.getDate());
        if (start == null || end == null) {
            JOptionPane.showMessageDialog(this, "Please select both start and end dates.");
            return;
        }
        if (end.isBefore(start)) {
            JOptionPane.showMessageDialog(this, "End date must not be earlier than start date.");
            return;
        }


        if (start.getYear() != end.getYear() || start.getMonth() != end.getMonth()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a date range within the same month for a single pay period (e.g., Feb2026).",
                    "Invalid Date Range",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        PayrollRangeService.PayrollResult r;
        try {
            r = payrollService.compute(emp, start, end);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to compute payroll: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        lastResult = r;

        periodLabel.setText("Pay Period: " + r.monthYear());
        hoursLabel.setText("Total Hours: " + String.format(Locale.US, "%.2f", r.totalHours()));
        grossLabel.setText("Gross: " + money(r.gross()));
        sssDedLabel.setText("SSS: " + money(r.sss()));
        philDedLabel.setText("PhilHealth: " + money(r.philhealth()));
        pagibigDedLabel.setText("Pag-IBIG: " + money(r.pagibig()));
        lateLabel.setText("Late Penalty: " + money(r.latePenalty()));
        taxableLabel.setText("Taxable Income: " + money(r.taxableIncome()));
        taxLabel.setText("Withholding Tax: " + money(r.withholdingTax()));
        netLabel.setText("Net: " + money(r.net()));

        attendanceModel.setRowCount(0);
        for (PayrollRangeService.PayrollRow row : r.rows()) {
            attendanceModel.addRow(new Object[]{
                    row.date(),
                    safe(row.timeIn()),
                    safe(row.timeOut()),
                    String.format(Locale.US, "%.2f", row.hours()),
                    row.lateMinutes()
            });
        }

        printBtn.setEnabled(true);
    }

/**
 * Handles clear preview.
 */
    private void clearPreview() {
        lastResult = null;
        printBtn.setEnabled(false);
        periodLabel.setText("Pay Period: -");
        hoursLabel.setText("Total Hours: -");
        grossLabel.setText("Gross: -");
        sssDedLabel.setText("SSS: -");
        philDedLabel.setText("PhilHealth: -");
        pagibigDedLabel.setText("Pag-IBIG: -");
        lateLabel.setText("Late Penalty: -");
        taxableLabel.setText("Taxable Income: -");
        taxLabel.setText("Withholding Tax: -");
        netLabel.setText("Net: -");
        attendanceModel.setRowCount(0);
    }
    
/**
 * Handles print to ledger.
 */
    private void printToLedger() {
        Employee emp = getSelectedEmployee();
        if (emp == null || lastResult == null) {
            JOptionPane.showMessageDialog(this, "Compute payroll first.");
            return;
        }

        Salary s = emp.getSalary();
        double hourly = (s == null) ? 0 : s.getHourlyRate();

        PayrollRecord record = new PayrollRecord(
                emp.getEmployeeNumber(),
                emp.getLastName(),
                emp.getFirstName(),
                emp.getPosition(),
                hourly,
                lastResult.monthYear(),
                lastResult.totalHours(),
                lastResult.gross(),
                lastResult.net(),
                lastResult.sss(),
                lastResult.philhealth(),
                lastResult.pagibig(),
                lastResult.latePenalty(),
                lastResult.taxableIncome(),
                lastResult.withholdingTax(),
                java.time.OffsetDateTime.now()
        );

        try {
            ledgerRepository.append(record);
            JOptionPane.showMessageDialog(
                    this,
                    "Saved to master payroll ledger:\n" + ledgerRepository.getLedgerPath(),
                    "Printed (Saved)",
                    JOptionPane.INFORMATION_MESSAGE
            );
            if (onPayrollSaved != null) {
                SwingUtilities.invokeLater(onPayrollSaved);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to save payroll record: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

/**
 * Returns selected employee.
 * @return resulting value produced by this method.
 */
    private Employee getSelectedEmployee() {
        Object item = employeeCombo.getSelectedItem();
        if (item instanceof EmployeeItem ei) return ei.employee;
        return null;
    }

/**
 * Handles style meta.
 * @param l input value needed by this method.
 */
    private static void styleMeta(JLabel l) {
        l.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,75%);");
    }

/**
 * Handles safe.
 * @param s input value needed by this method.
 * @return resulting value produced by this method.
 */
    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

/**
 * Handles money.
 * @param v input value needed by this method.
 * @return resulting value produced by this method.
 */
    private static String money(double v) {
        if (Double.isNaN(v)) return "-";
        return String.format(Locale.US, "₱%,.2f", v);
    }

/**
 * Handles to local date.
 * @param d input value needed by this method.
 * @return resulting value produced by this method.
 */
    private static LocalDate toLocalDate(java.util.Date d) {
        if (d == null) return null;
        return Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

/**
 * Handles employee item.
 * @param employee input value needed by this method.
 * @return resulting value produced by this method.
 */
    private record EmployeeItem(Employee employee) {
        @Override public String toString() {
            return employee.getEmployeeNumber() + "  •  " + employee.getFirstName() + " " + employee.getLastName();
        }
    }
}
