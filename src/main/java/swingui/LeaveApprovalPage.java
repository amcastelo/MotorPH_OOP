package swingui;

import dao.LeaveLedgerRepository;
import model.Employee;
import model.LeaveRequest;
import model.LeaveStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;




/**
 * Represents the leave approval page component used in the swingui layer.
 */
public class LeaveApprovalPage extends JPanel {

    private final LeaveLedgerRepository repository;
    private final Employee hrUser;

    private final JPanel cardsPanel = new JPanel();
    private final JLabel emptyLabel = new JLabel("No leave requests found.");
    private final JButton refreshBtn = new JButton("Refresh");


    private final JToggleButton pendingBtn = new JToggleButton("Pending");
    private final JToggleButton completedBtn = new JToggleButton("Completed");
    private final ButtonGroup filterGroup = new ButtonGroup();

/**
 * Represents the available filter mode values used in the swingui layer.
 */
    private enum FilterMode { PENDING, COMPLETED }
    private FilterMode filterMode = FilterMode.PENDING;


/**
 * Handles leave approval page.
 * @param repository input value needed by this method.
 * @param hrUser input value needed by this method.
 * @return resulting value produced by this method.
 */
    public LeaveApprovalPage(LeaveLedgerRepository repository, Employee hrUser) {
        this.repository = (repository == null) ? new LeaveLedgerRepository() : repository;
        this.hrUser = hrUser;

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


        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftHeader.setOpaque(false);

        JLabel title = new JLabel("Leave Approval");
        title.putClientProperty("FlatLaf.style", "font: bold 16;");


        setupToggle(pendingBtn, true);
        setupToggle(completedBtn, false);
        filterGroup.add(pendingBtn);
        filterGroup.add(completedBtn);
        pendingBtn.setSelected(true);

        pendingBtn.addActionListener(e -> {
            filterMode = FilterMode.PENDING;
            updateToggleStyles();
            reload();
        });
        completedBtn.addActionListener(e -> {
            filterMode = FilterMode.COMPLETED;
            updateToggleStyles();
            reload();
        });
        updateToggleStyles();

        leftHeader.add(title);
        leftHeader.add(pendingBtn);
        leftHeader.add(completedBtn);


        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.putClientProperty("FlatLaf.style", "arc: 12; margin: 8,12,8,12; font: bold 12;");
        refreshBtn.addActionListener(e -> reload());

        header.add(leftHeader, BorderLayout.WEST);
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

        List<LeaveRequest> reqs = repository.listAll();


        reqs.removeIf(r -> {
            LeaveStatus st = (r == null) ? null : r.getStatus();
            if (filterMode == FilterMode.PENDING) {
                return st != null && st != LeaveStatus.PENDING;
            } else {

                return st == null || st == LeaveStatus.PENDING;
            }
        });

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

        cols.add(headerLabel("Employee ID"));
        cols.add(headerLabel("Name"));
        cols.add(headerLabel("Date Period"));
        cols.add(headerLabel("Type"));
        cols.add(headerLabel("Status"));

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

        JLabel emp = new JLabel(safe(r.getEmployeeId()));
        emp.putClientProperty("FlatLaf.style", "font: bold 13;");

        JLabel name = new JLabel(safe(r.getLastName()) + ", " + safe(r.getFirstName()));
        name.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,80%);");

        JLabel period = new JLabel(fmtDateRange(r));
        period.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,80%);");

        JLabel type = new JLabel(safe(r.getLeaveType()));
        type.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,80%);");

        JLabel status = new JLabel(r.getStatus() == null ? LeaveStatus.PENDING.display() : r.getStatus().display());
        status.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,80%);");

        cols.add(emp);
        cols.add(name);
        cols.add(period);
        cols.add(type);
        cols.add(status);

        JButton viewBtn = new JButton("View");
        viewBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewBtn.putClientProperty("FlatLaf.style", "arc: 12; margin: 10,14,10,14; font: bold 13;");
        viewBtn.addActionListener(e -> showDetailsDialog(r));

        row.add(cols, BorderLayout.CENTER);
        row.add(viewBtn, BorderLayout.EAST);

        return row;
    }

/**
 * Shows details dialog.
 * @param r input value needed by this method.
 */
    private void showDetailsDialog(LeaveRequest r) {
        boolean isCompleted = r != null && r.getStatus() != null && r.getStatus() != LeaveStatus.PENDING;

        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this), "Leave Request", Dialog.ModalityType.APPLICATION_MODAL);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 8));
        grid.add(new JLabel("Employee ID:"));
        grid.add(new JLabel(safe(r.getEmployeeId())));
        grid.add(new JLabel("Name:"));
        grid.add(new JLabel(safe(r.getLastName()) + ", " + safe(r.getFirstName())));
        grid.add(new JLabel("Position:"));
        grid.add(new JLabel(safe(r.getPosition())));
        grid.add(new JLabel("Pay Period:"));
        grid.add(new JLabel(safe(r.getPayPeriod())));
        grid.add(new JLabel("Date Range:"));
        grid.add(new JLabel(fmtDateRange(r)));
        grid.add(new JLabel("Type:"));
        grid.add(new JLabel(safe(r.getLeaveType())));
        grid.add(new JLabel("Status:"));
        grid.add(new JLabel(r.getStatus() == null ? LeaveStatus.PENDING.display() : r.getStatus().display()));

        grid.add(new JLabel("Reason:"));
        grid.add(new JLabel("<html><body style='width:280px'>" + escapeHtml(safe(r.getReason())) + "</body></html>"));

        content.add(grid, BorderLayout.CENTER);


        JPanel commentsWrap = new JPanel(new BorderLayout(0, 6));
        JLabel commentsLbl = new JLabel("HR Comments (optional)");
        commentsLbl.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,70%);");
        JTextArea comments = new JTextArea(3, 30);
        comments.setLineWrap(true);
        comments.setWrapStyleWord(true);
        comments.setText(safe(r.getHrComments()));
        comments.setEditable(!isCompleted);
        comments.setEnabled(!isCompleted);

        JScrollPane sp = new JScrollPane(comments);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        commentsWrap.add(commentsLbl, BorderLayout.NORTH);
        commentsWrap.add(sp, BorderLayout.CENTER);


        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        if (isCompleted) {
            JButton close = new JButton("Close");
            close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            close.putClientProperty("FlatLaf.style", "arc: 12; margin: 10,14,10,14; font: bold 13;");
            close.addActionListener(e -> dlg.dispose());
            actions.add(close);
        } else {
            JButton deny = new JButton("Deny");
            JButton approve = new JButton("Accept");
            deny.putClientProperty("FlatLaf.style", "arc: 12; margin: 10,14,10,14; font: bold 13;");
            approve.putClientProperty("FlatLaf.style", "arc: 12; margin: 10,14,10,14; font: bold 13; background: fade(@accentColor,18%);");
            deny.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            approve.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            deny.addActionListener(e -> {
                try {
                    r.deny(comments.getText());
                    repository.upsert(r);
                    dlg.dispose();
                    reload();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to update request: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            approve.addActionListener(e -> {
                try {
                    r.approve(comments.getText());
                    repository.upsert(r);
                    dlg.dispose();
                    reload();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to update request: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            actions.add(deny);
            actions.add(approve);
        }
JPanel south = new JPanel(new BorderLayout(0, 12));
        south.setOpaque(false);
        south.add(commentsWrap, BorderLayout.CENTER);
        south.add(actions, BorderLayout.SOUTH);

        content.add(south, BorderLayout.SOUTH);

        dlg.setContentPane(content);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }


/**
 * Updates up toggle.
 * @param b input value needed by this method.
 * @param selected input value needed by this method.
 */
    private static void setupToggle(AbstractButton b, boolean selected) {
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        b.setContentAreaFilled(true);
        b.putClientProperty("FlatLaf.style", "arc: 12; margin: 6,12,6,12; font: bold 12;");
        b.setSelected(selected);
    }

/**
 * Updates toggle styles.
 */
    private void updateToggleStyles() {

        if (pendingBtn.isSelected()) {
            pendingBtn.putClientProperty("FlatLaf.style", "arc: 12; margin: 6,12,6,12; font: bold 12; background: fade(@accentColor,18%);");
            completedBtn.putClientProperty("FlatLaf.style", "arc: 12; margin: 6,12,6,12; font: bold 12;");
        } else {
            completedBtn.putClientProperty("FlatLaf.style", "arc: 12; margin: 6,12,6,12; font: bold 12; background: fade(@accentColor,18%);");
            pendingBtn.putClientProperty("FlatLaf.style", "arc: 12; margin: 6,12,6,12; font: bold 12;");
        }
        pendingBtn.repaint();
        completedBtn.repaint();
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
 * Handles fmt date range.
 * @param r input value needed by this method.
 * @return resulting value produced by this method.
 */
    private static String fmtDateRange(LeaveRequest r) {
        if (r == null) return "";
        String a = r.getStartDate() == null ? "" : r.getStartDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String b = r.getEndDate() == null ? "" : r.getEndDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        return a + " - " + b;
    }

/**
 * Handles safe.
 * @param s.trim( input value needed by this method.
 * @return resulting value produced by this method.
 */
    private static String safe(String s) { return s == null ? "" : s.trim(); }

/**
 * Handles escape html.
 * @param s input value needed by this method.
 * @return resulting value produced by this method.
 */
    private static String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
