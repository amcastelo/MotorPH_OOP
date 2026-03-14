package swingui;

import dao.PayrollLedgerRepository;
import model.Employee;
import model.PayrollRecord;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;









/**
 * Represents the payroll history page component used in the swingui layer.
 */
public class PayrollHistoryPage extends JPanel {

    private final PayrollLedgerRepository ledgerRepository;
    private final Employee employee;

    private final JPanel cardsPanel = new JPanel();
    private final JLabel emptyLabel = new JLabel("No payroll records found yet.");
    private final JButton refreshBtn = new JButton("Refresh");

/**
 * Creates a new PayrollHistoryPage instance.
 * @param ledgerRepository input value needed by this method.
 * @param employee input value needed by this method.
 */
    public PayrollHistoryPage(PayrollLedgerRepository ledgerRepository, Employee employee) {
        this.ledgerRepository = (ledgerRepository == null) ? new PayrollLedgerRepository() : ledgerRepository;
        this.employee = employee;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        add(buildCard());
        add(Box.createVerticalGlue());

        reload();
    }

/**
 * Builds card.
 * @return resulting value produced by this method.
 */
    private JComponent buildCard() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.putClientProperty("FlatLaf.style",
                "background: @background; arc: 18; border: 1,1,1,1, fade(@foreground,10%);");

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("My Payroll Records");
        title.putClientProperty("FlatLaf.style", "font: bold 16;");
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.putClientProperty("FlatLaf.style", "arc: 12; margin: 8,12,8,12; font: bold 12;");
        refreshBtn.addActionListener(e -> reload());
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

        List<PayrollRecord> records = ledgerRepository.listForEmployee(employee.getEmployeeNumber());
        records.sort(Comparator.comparing(PayrollRecord::getCreatedAt).reversed());

        if (records.isEmpty()) {
            cardsPanel.add(emptyLabel);
        } else {
            cardsPanel.add(buildHeaderRow());
            cardsPanel.add(Box.createVerticalStrut(6));
            for (PayrollRecord r : records) {
                cardsPanel.add(buildRowCard(r));
                cardsPanel.add(Box.createVerticalStrut(10));
            }
        }

        revalidate();
        repaint();
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
    cols.add(headerLabel("Hours"));
    cols.add(headerLabel("Gross"));
    cols.add(headerLabel("Net"));
    cols.add(headerLabel("Created"));

    row.add(cols, BorderLayout.CENTER);

    JLabel spacer = headerLabel("");
    spacer.setPreferredSize(new Dimension(80, 1));
    row.add(spacer, BorderLayout.EAST);

    return row;
}

/**
 * Handles header label.
 * @param s input value needed by this method.
 * @return resulting value produced by this method.
 */
private JLabel headerLabel(String s) {
    JLabel l = new JLabel(s);
    l.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,70%); font: bold 12;");
    return l;
}

/**
 * Builds row card.
 * @param r input value needed by this method.
 * @return resulting value produced by this method.
 */
private JComponent buildRowCard(PayrollRecord r) {
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

    JLabel hours = new JLabel(String.format(Locale.US, "%.2f", r.getTotalHours()));
    hours.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,80%);");

    JLabel gross = new JLabel(money(r.getGross()));
    gross.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,80%);");

    JLabel net = new JLabel(money(r.getNet()));
    net.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,80%);");

    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d, yyyy  HH:mm", Locale.ENGLISH);
    JLabel created = new JLabel(r.getCreatedAt().toLocalDateTime().format(fmt));
    created.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,70%);");

    cols.add(period);
    cols.add(hours);
    cols.add(gross);
    cols.add(net);
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
    private void showDetails(PayrollRecord r) {
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
        panel.add(new JLabel("Total Hours:"));
        panel.add(new JLabel(String.format(Locale.US, "%.2f", r.getTotalHours())));
        panel.add(new JLabel("Gross:"));
        panel.add(new JLabel(money(r.getGross())));
        panel.add(new JLabel("Net:"));
        panel.add(new JLabel(money(r.getNet())));

        panel.add(new JLabel("SSS:"));
        panel.add(new JLabel(money(r.getSss())));
        panel.add(new JLabel("PhilHealth:"));
        panel.add(new JLabel(money(r.getPhilhealth())));
        panel.add(new JLabel("Pag-IBIG:"));
        panel.add(new JLabel(money(r.getPagibig())));
        panel.add(new JLabel("Late Penalty:"));
        panel.add(new JLabel(money(r.getLatePenalty())));
        panel.add(new JLabel("Taxable Income:"));
        panel.add(new JLabel(money(r.getTaxableIncome())));
        panel.add(new JLabel("Withholding Tax:"));
        panel.add(new JLabel(money(r.getWithholdingTax())));

        JOptionPane.showMessageDialog(
                this,
                panel,
                "Payroll Details - " + safe(r.getPayPeriod()),
                JOptionPane.INFORMATION_MESSAGE
        );
    }

/**
 * Handles safe.
 * @param s.trim( input value needed by this method.
 * @return resulting value produced by this method.
 */
    private static String safe(String s) { return s == null ? "" : s.trim(); }

/**
 * Handles money.
 * @param v input value needed by this method.
 * @return resulting value produced by this method.
 */
    private static String money(double v) {
        return String.format(Locale.US, "₱%,.2f", v);
    }
}
