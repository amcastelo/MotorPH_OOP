package swingui;

import com.toedter.calendar.JDateChooser;
import dao.EmployeeFileManager.Result;
import dao.LeaveLedgerRepository;
import model.Employee;
import model.LeaveRequest;
import model.LeaveStatus;
import service.LeaveService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;







/**
 * Represents the leave request page component used in the swingui layer.
 */
public class LeaveRequestPage extends JPanel {

    private final LeaveLedgerRepository repository;
    private final Employee employee;


    private final JDateChooser startChooser = new JDateChooser();
    private final JDateChooser endChooser = new JDateChooser();
    private final JComboBox<String> typeCombo = new JComboBox<>(new String[]{
            "Planned Leave", "Unplanned Leave", "Sick Leave", "Other"
    });
    private final JTextArea reasonArea = new JTextArea(4, 28);
    private final JButton submitBtn = new JButton("Submit Request");


    private final JPanel cardsPanel = new JPanel();
    private final JLabel emptyLabel = new JLabel("No leave requests submitted yet.");
    private final JButton refreshBtn = new JButton("Refresh");

/**
 * Creates a new LeaveRequestPage instance.
 * @param repository input value needed by this method.
 * @param employee input value needed by this method.
 */
    public LeaveRequestPage(LeaveLedgerRepository repository, Employee employee) {
        this.repository = (repository == null) ? new LeaveLedgerRepository() : repository;
        this.employee = employee;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        add(buildTopCard());
        add(Box.createVerticalStrut(16));
        add(buildBottomCard());
        add(Box.createVerticalGlue());

        wireActions();
        reload();
    }

/**
 * Builds top card.
 * @return resulting value produced by this method.
 */
    private JComponent buildTopCard() {
        JPanel card = new JPanel(new BorderLayout(16, 0));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.putClientProperty("FlatLaf.style",
                "background: @background; arc: 18; border: 1,1,1,1, fade(@foreground,10%);");

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Leave Request Form");
        title.putClientProperty("FlatLaf.style", "font: bold 16;");
        left.add(title);
        left.add(Box.createVerticalStrut(12));

        JPanel dates = new JPanel(new GridLayout(2, 2, 12, 10));
        dates.setOpaque(false);

        dates.add(metaLabel("Start Date"));
        configureChooser(startChooser);
        dates.add(startChooser);

        dates.add(metaLabel("End Date"));
        configureChooser(endChooser);
        dates.add(endChooser);

        JPanel typeRow = new JPanel(new GridLayout(1, 2, 12, 10));
        typeRow.setOpaque(false);

        typeRow.add(metaLabel("Type of Leave"));


        JPanel comboWrap = new JPanel(new BorderLayout());
        comboWrap.setOpaque(false);

        typeCombo.putClientProperty("FlatLaf.style", "arc: 12;");


        int fieldH = startChooser.getDateEditor().getUiComponent().getPreferredSize().height;
        Dimension pref = typeCombo.getPreferredSize();
        typeCombo.setPreferredSize(new Dimension(pref.width, fieldH));
        typeCombo.setMinimumSize(new Dimension(0, fieldH));


        typeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, fieldH));
        comboWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, fieldH));
        comboWrap.setPreferredSize(new Dimension(0, fieldH));

        comboWrap.add(typeCombo, BorderLayout.CENTER);
        typeRow.add(comboWrap);


        typeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, fieldH));
        JPanel reasonWrap = new JPanel(new BorderLayout(0, 8));
        reasonWrap.setOpaque(false);
        reasonWrap.add(metaLabel("Reason"), BorderLayout.NORTH);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        JScrollPane sp = new JScrollPane(reasonArea);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.putClientProperty("FlatLaf.style", "arc: 12;");
        reasonWrap.add(sp, BorderLayout.CENTER);

        submitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitBtn.putClientProperty("FlatLaf.style",
                "arc: 12; margin: 10,14,10,14; font: bold 13; background: fade(@accentColor,18%);");

        left.add(dates);
        left.add(Box.createVerticalStrut(12));
        left.add(typeRow);
        left.add(Box.createVerticalStrut(12));
        left.add(reasonWrap);
        left.add(Box.createVerticalStrut(12));
        left.add(submitBtn);

        card.add(left, BorderLayout.CENTER);
        return card;
    }

/**
 * Builds bottom card.
 * @return resulting value produced by this method.
 */
    private JComponent buildBottomCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.putClientProperty("FlatLaf.style",
                "background: @background; arc: 18; border: 1,1,1,1, fade(@foreground,10%);");

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("My Leave Requests");
        title.putClientProperty("FlatLaf.style", "font: bold 16;");
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.putClientProperty("FlatLaf.style", "arc: 12; margin: 8,12,8,12; font: bold 12;");
        header.add(title, BorderLayout.WEST);
        header.add(refreshBtn, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        cardsPanel.setOpaque(false);
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBorder(new EmptyBorder(6, 0, 0, 0));

        JScrollPane scroll = new JScrollPane(cardsPanel);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        emptyLabel.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,70%);");

        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

/**
 * Wires actions.
 */
    private void wireActions() {
        refreshBtn.addActionListener(e -> reload());
        submitBtn.addActionListener(e -> submit());
    }

/**
 * Reloads the required data.
 */
    public final void reload() {
        cardsPanel.removeAll();

        if (employee == null) {
            cardsPanel.add(emptyLabel);
            revalidate();
            repaint();
            return;
        }

        List<LeaveRequest> reqs = repository.listForEmployee(employee.getEmployeeNumber());
        reqs.sort(Comparator.comparing(LeaveRequest::getCreatedAt).reversed());

        if (reqs.isEmpty()) {
            cardsPanel.add(emptyLabel);
        } else {
            cardsPanel.add(buildHeaderRow());
            cardsPanel.add(Box.createVerticalStrut(6));
            for (LeaveRequest r : reqs) {
                cardsPanel.add(buildRowCard(r));
                cardsPanel.add(Box.createVerticalStrut(10));
            }
        }

        revalidate();
        repaint();
    }

/**
 * Handles submit.
 */
    private void submit() {
        if (employee == null) {
            JOptionPane.showMessageDialog(this, "No logged-in employee found.");
            return;
        }

        LocalDate start = toLocalDate(startChooser.getDate());
        LocalDate end = toLocalDate(endChooser.getDate());
        String type = (String) typeCombo.getSelectedItem();
        String reason = reasonArea.getText() == null ? "" : reasonArea.getText().trim();
        
        LeaveService leaveService = new LeaveService();

        try {
            Result result = leaveService.submitLeaveRequest(employee, start, end, type, reason);

            JOptionPane.showMessageDialog(this, result.getMessage(),
                    result.isOk() ? "Submitted" : "Error",
                    result.isOk() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

            if (result.isOk()) {
                reasonArea.setText("");
                reload();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to save leave request: " + ex.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

/**
 * Builds header row.
 * @return resulting value produced by this method.
 */
    private JComponent buildHeaderRow() {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(0, 12, 6, 12));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JPanel cols = new JPanel(new GridLayout(1, 5, 12, 0));
        cols.setOpaque(false);

        cols.add(headerLabel("Pay Period"));
        cols.add(headerLabel("Date Range"));
        cols.add(headerLabel("Type"));
        cols.add(headerLabel("Status"));
        cols.add(headerLabel("Created"));

        row.add(cols, BorderLayout.CENTER);

        JLabel spacer = headerLabel("");
        spacer.setPreferredSize(new Dimension(80, 1));
        row.add(spacer, BorderLayout.EAST);

        return row;
    }

/**
 * Builds row card.
 * @param r input value needed by this method.
 * @return resulting value produced by this method.
 */
    private JComponent buildRowCard(LeaveRequest r) {
        final int ROW_H = 64;

        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(true);
        row.setBorder(new EmptyBorder(10, 12, 10, 12));
        row.putClientProperty("FlatLaf.style",
                "background: darken(@background,3%); arc: 14; border: 1,1,1,1, fade(@foreground,8%);");

        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW_H));
        row.setPreferredSize(new Dimension(0, ROW_H));
        row.setMinimumSize(new Dimension(0, ROW_H));

        JPanel cols = new JPanel(new GridLayout(1, 5, 12, 0));
        cols.setOpaque(false);

        JLabel period = new JLabel(safe(r.getPayPeriod()));
        period.putClientProperty("FlatLaf.style", "font: bold 13;");

        String range = fmtDate(r.getStartDate()) + " - " + fmtDate(r.getEndDate());
        JLabel dateRange = new JLabel(range);
        dateRange.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,80%);");

        JLabel type = new JLabel(safe(r.getLeaveType()));
        type.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,80%);");

        JLabel status = new JLabel(r.getStatus() == null ? LeaveStatus.PENDING.display() : r.getStatus().display());
        status.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,80%);");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d, yyyy  HH:mm", Locale.ENGLISH);
        JLabel created = new JLabel(r.getCreatedAt() == null ? "" : r.getCreatedAt().toLocalDateTime().format(fmt));
        created.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,70%);");

        cols.add(period);
        cols.add(dateRange);
        cols.add(type);
        cols.add(status);
        cols.add(created);

        JButton viewBtn = new JButton("View");
        viewBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewBtn.putClientProperty("FlatLaf.style", "arc: 12; margin: 10,14,10,14; font: bold 13;");
        viewBtn.addActionListener(e -> showDetails(r));

        row.add(cols, BorderLayout.CENTER);
        row.add(viewBtn, BorderLayout.EAST);
        return row;
    }

/**
 * Shows details.
 * @param r input value needed by this method.
 */
    private void showDetails(LeaveRequest r) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 12, 8));

        panel.add(new JLabel("Employee ID:"));
        panel.add(new JLabel(safe(r.getEmployeeId())));
        panel.add(new JLabel("Name:"));
        panel.add(new JLabel(safe(r.getFirstName()) + " " + safe(r.getLastName())));
        panel.add(new JLabel("Position:"));
        panel.add(new JLabel(safe(r.getPosition())));
        panel.add(new JLabel("Hourly Rate:"));
        panel.add(new JLabel(money(r.getHourlyRate())));

        panel.add(new JLabel("Pay Period:"));
        panel.add(new JLabel(safe(r.getPayPeriod())));
        panel.add(new JLabel("Date Range:"));
        panel.add(new JLabel(fmtDate(r.getStartDate()) + " - " + fmtDate(r.getEndDate())));
        panel.add(new JLabel("Type:"));
        panel.add(new JLabel(safe(r.getLeaveType())));
        panel.add(new JLabel("Status:"));
        panel.add(new JLabel(r.getStatus() == null ? LeaveStatus.PENDING.display() : r.getStatus().display()));

        panel.add(new JLabel("Reason:"));
        panel.add(new JLabel("<html><body style='width:260px'>" + escapeHtml(safe(r.getReason())) + "</body></html>"));

        panel.add(new JLabel("HR Comments:"));
        String comments = safe(r.getHrComments());
        panel.add(new JLabel("<html><body style='width:260px'>" + (comments.isBlank() ? "-" : escapeHtml(comments)) + "</body></html>"));

        JOptionPane.showMessageDialog(
                this,
                panel,
                "Leave Request Details - " + safe(r.getPayPeriod()),
                JOptionPane.INFORMATION_MESSAGE
        );
    }

/**
 * Handles configure chooser.
 * @param chooser input value needed by this method.
 */
    private static void configureChooser(JDateChooser chooser) {
        chooser.setDateFormatString("MM/dd/yyyy");
        chooser.getCalendarButton().putClientProperty("FlatLaf.style", "arc: 12;");
        chooser.getDateEditor().getUiComponent().putClientProperty("FlatLaf.style", "arc: 12;");
    }

/**
 * Handles meta label.
 * @param s input value needed by this method.
 * @return resulting value produced by this method.
 */
    private static JLabel metaLabel(String s) {
        JLabel l = new JLabel(s);
        l.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,70%);");
        return l;
    }

/**
 * Handles header label.
 * @param s input value needed by this method.
 * @return resulting value produced by this method.
 */
    private static JLabel headerLabel(String s) {
        JLabel l = new JLabel(s);
        l.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,70%); font: bold 12;");
        return l;
    }

/**
 * Handles to local date.
 * @param d input value needed by this method.
 * @return resulting value produced by this method.
 */
    private static LocalDate toLocalDate(java.util.Date d) {
        if (d == null) return null;
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

/**
 * Handles fmt date.
 * @param d input value needed by this method.
 * @return resulting value produced by this method.
 */
    private static String fmtDate(LocalDate d) {
        if (d == null) return "";
        return d.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

/**
 * Handles safe.
 * @param s.trim( input value needed by this method.
 * @return resulting value produced by this method.
 */
    private static String safe(String s) { return s == null ? "" : s.trim(); }

/**
 * Handles money.
 * @param String.format(Locale.US input value needed by this method.
 * @param "₱% input value needed by this method.
 * @param .2f" input value needed by this method.
 * @param v input value needed by this method.
 * @return resulting value produced by this method.
 */
    private static String money(double v) { return String.format(Locale.US, "₱%,.2f", v); }

/**
 * Handles escape html.
 * @param s input value needed by this method.
 * @return resulting value produced by this method.
 */
    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
