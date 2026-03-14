



package swingui;

import model.AttendanceEntry;
import model.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;





/**
 * Represents the motor phmain component used in the swingui layer.
 */
public class MotorPHMain extends javax.swing.JFrame implements MotorPHMainView {


    private Employee employee;

    private final JPanel drawerButtons = new JPanel();
    private Consumer<String> navHandler = (s) -> {};

    private final JPanel content = new JPanel();
    private final CardLayout cardLayout = new CardLayout();


    private JLabel nameLabel;
    private JLabel positionLabel;
    private JLabel birthdayLabel;
    private JLabel empNoLabel;
    private JLabel statusLabel;

    private JButton timeInBtn;
    private JButton timeOutBtn;

    private JPanel attendanceCardsPanel;
    private JButton logoutBtn;




/**
 * Creates a new MotorPHMain instance.
 */
    public MotorPHMain() {
        super("MotorPH");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 650));
        setLocationRelativeTo(null);
        buildUI();
    }

/**
 * Builds ui.
 */
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(16, 16, 16, 16));


        JPanel drawer = new JPanel(new BorderLayout());
        drawer.setPreferredSize(new Dimension(240, 0));
        drawer.setBorder(new EmptyBorder(14, 14, 14, 14));
        drawer.putClientProperty("FlatLaf.style",
                "background: darken(@background,4%); arc: 18; border: 1,1,1,1, fade(@foreground,10%);");

        JLabel title = new JLabel("MotorPH");
        title.putClientProperty("FlatLaf.style", "font: bold 18;");
        title.setHorizontalAlignment(SwingConstants.CENTER);

        drawerButtons.setOpaque(false);
        drawerButtons.setLayout(new BoxLayout(drawerButtons, BoxLayout.Y_AXIS));
        drawerButtons.setBorder(new EmptyBorder(14, 0, 0, 0));

        logoutBtn = new JButton("Log Out");
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.putClientProperty("FlatLaf.style",
                "arc: 12; margin: 10,14,10,14; font: bold 13;");

        JPanel drawerFooter = new JPanel(new BorderLayout());
        drawerFooter.setOpaque(false);
        drawerFooter.setBorder(new EmptyBorder(10, 0, 0, 0));
        drawerFooter.add(logoutBtn, BorderLayout.CENTER);

        drawer.add(title, BorderLayout.NORTH);
        drawer.add(drawerButtons, BorderLayout.CENTER);
        drawer.add(drawerFooter, BorderLayout.SOUTH);


        content.setLayout(cardLayout);
        content.setOpaque(false);


        content.add(buildDashboardPage(), "Dashboard");

        root.add(drawer, BorderLayout.WEST);
        root.add(content, BorderLayout.CENTER);
        setContentPane(root);
    }

/**
 * Builds dashboard page.
 * @return resulting value produced by this method.
 */
    private JComponent buildDashboardPage() {
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setOpaque(false);


        JPanel topCard = new JPanel(new BorderLayout(16, 0));
        topCard.setBorder(new EmptyBorder(18, 18, 18, 18));
        topCard.putClientProperty("FlatLaf.style",
                "background: @background; arc: 18; border: 1,1,1,1, fade(@foreground,10%);");


        JLabel avatar = new JLabel();
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setVerticalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(96, 96));
        avatar.setOpaque(true);
        avatar.putClientProperty("FlatLaf.style",
                "background: darken(@background,6%); border: 1,1,1,1, fade(@foreground,10%);");

        ImageIcon icon = loadAvatarIcon("/icons/Employee.png", 72, 72);
        if (icon != null) avatar.setIcon(icon);
        else avatar.setText("Avatar");


        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        nameLabel = new JLabel("");
        nameLabel.putClientProperty("FlatLaf.style", "font: bold 20;");

        positionLabel = new JLabel("");
        positionLabel.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,75%);");

        birthdayLabel = new JLabel("");
        birthdayLabel.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,75%);");

        empNoLabel = new JLabel("");
        empNoLabel.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,75%);");

        details.add(nameLabel);
        details.add(Box.createVerticalStrut(6));
        details.add(positionLabel);
        details.add(birthdayLabel);
        details.add(empNoLabel);


        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));

        JLabel clock = new JLabel();
        clock.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,70%);");
        updateClock(clock);
        new Timer(1000, e -> updateClock(clock)).start();

        timeInBtn = new JButton("Time In");
        timeOutBtn = new JButton("Time Out");

        timeInBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        timeOutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        timeInBtn.putClientProperty("FlatLaf.style",
                "arc: 12; margin: 10,14,10,14; font: bold 13; background: fade(@accentColor,18%);");
        timeOutBtn.putClientProperty("FlatLaf.style",
                "arc: 12; margin: 10,14,10,14; font: bold 13;");

        statusLabel = new JLabel(" ");
        statusLabel.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,70%);");

        actions.add(clock);
        actions.add(Box.createVerticalStrut(10));
        actions.add(timeInBtn);
        actions.add(Box.createVerticalStrut(8));
        actions.add(timeOutBtn);
        actions.add(Box.createVerticalStrut(10));
        actions.add(statusLabel);

        topCard.add(avatar, BorderLayout.WEST);
        topCard.add(details, BorderLayout.CENTER);
        topCard.add(actions, BorderLayout.EAST);


        JPanel bottomCard = new JPanel(new BorderLayout(0, 12));
        bottomCard.setBorder(new EmptyBorder(18, 18, 18, 18));
        bottomCard.putClientProperty("FlatLaf.style",
                "background: @background; arc: 18; border: 1,1,1,1, fade(@foreground,10%);");

        JLabel recentLabel = new JLabel("Previous Attendance");
        recentLabel.putClientProperty("FlatLaf.style", "font: bold 16;");
        bottomCard.add(recentLabel, BorderLayout.NORTH);

        attendanceCardsPanel = new JPanel();
        attendanceCardsPanel.setOpaque(false);
        attendanceCardsPanel.setLayout(new BoxLayout(attendanceCardsPanel, BoxLayout.Y_AXIS));
        attendanceCardsPanel.setBorder(new EmptyBorder(6, 0, 0, 0));

        JScrollPane scroll = new JScrollPane(attendanceCardsPanel);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        bottomCard.add(scroll, BorderLayout.CENTER);

        page.add(topCard);
        page.add(Box.createVerticalStrut(16));
        page.add(bottomCard);
        page.add(Box.createVerticalGlue());

        return page;
    }



    @Override
/**
 * Updates employee.
 * @param e input value needed by this method.
 */
    public void setEmployee(Employee e) {
        this.employee = e;

        String first = safe(e == null ? "" : e.getFirstName());
        String last  = safe(e == null ? "" : e.getLastName());
        nameLabel.setText((first + " " + last).trim());

        String pos = e == null ? "" : safe(e.getPosition());
        positionLabel.setText("Position: " + pos);


        birthdayLabel.setText("Birthday: " + safe(getBirthdayOrBlank(e)));

        empNoLabel.setText("Employee #: " + (e == null ? "" : safe(e.getEmployeeNumber())));
    }

    @Override
/**
 * Updates status.
 * @param text input value needed by this method.
 * @param isError input value needed by this method.
 */
    public void setStatus(String text, boolean isError) {
        statusLabel.putClientProperty("FlatLaf.style",
                isError ? "foreground: #ff6b6b;" : "foreground: fade(@foreground,70%);");
        statusLabel.setText(text == null || text.isBlank() ? " " : text);
        statusLabel.repaint();
    }

    @Override
/**
 * Updates attendance.
 * @param entries input value needed by this method.
 */
    public void setAttendance(List<AttendanceEntry> entries) {
        attendanceCardsPanel.removeAll();


        attendanceCardsPanel.add(attendanceHeaderRow());
        attendanceCardsPanel.add(Box.createVerticalStrut(8));

        if (entries == null || entries.isEmpty()) {
            attendanceCardsPanel.add(emptyRow("No attendance records yet."));
        } else {
            for (AttendanceEntry r : entries) {
                attendanceCardsPanel.add(attendanceRow(r));
                attendanceCardsPanel.add(Box.createVerticalStrut(8));
            }
        }

        attendanceCardsPanel.revalidate();
        attendanceCardsPanel.repaint();
    }

    @Override
/**
 * Handles on time in.
 * @param l input value needed by this method.
 */
    public void onTimeIn(ActionListener l) {
        for (ActionListener old : timeInBtn.getActionListeners()) timeInBtn.removeActionListener(old);
        timeInBtn.addActionListener(l);
    }

    @Override
/**
 * Handles on logout.
 * @param l input value needed by this method.
 */
    public void onLogout(ActionListener l) {
        if (logoutBtn == null) return;
        for (ActionListener old : logoutBtn.getActionListeners()) logoutBtn.removeActionListener(old);
        logoutBtn.addActionListener(l);
    }

    @Override
/**
 * Handles on time out.
 * @param l input value needed by this method.
 */
    public void onTimeOut(ActionListener l) {
        for (ActionListener old : timeOutBtn.getActionListeners()) timeOutBtn.removeActionListener(old);
        timeOutBtn.addActionListener(l);
    }

    @Override
/**
 * Handles add nav item.
 * @param name input value needed by this method.
 */
    public void addNavItem(String name) {
        JButton btn = new JButton(name);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.putClientProperty("FlatLaf.style", "arc: 12; margin: 8,12,8,12; focusWidth: 0;");

        btn.addActionListener(e -> navHandler.accept(name));

        drawerButtons.add(btn);
        drawerButtons.add(Box.createVerticalStrut(8));
        drawerButtons.revalidate();
        drawerButtons.repaint();
    }

    @Override
/**
 * Handles on nav select.
 * @param handler input value needed by this method.
 */
    public void onNavSelect(Consumer<String> handler) {
        this.navHandler = handler == null ? (s) -> {} : handler;
    }

    @Override
/**
 * Shows page.
 * @param name input value needed by this method.
 */
    public void showPage(String name) {
        cardLayout.show(content, name);
    }

    @Override
/**
 * Handles register page.
 * @param name input value needed by this method.
 * @param page input value needed by this method.
 */
    public void registerPage(String name, JComponent page) {
        if (name == null || name.isBlank() || page == null) return;
        content.add(page, name);
        content.revalidate();
        content.repaint();
    }

    @Override
/**
 * Shows window.
 */
    public void showWindow() {
        setVisible(true);
    }

    @Override
/**
 * Closes window.
 */
    public void closeWindow() {
        dispose();
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
 * Updates clock.
 * @param clock input value needed by this method.
 */
    private void updateClock(JLabel clock) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE, MMM d yyyy  •  hh:mm:ss a");
        clock.setText(LocalDateTime.now().format(fmt));
    }

/**
 * Handles empty card.
 * @param text input value needed by this method.
 * @return resulting value produced by this method.
 */
    private JComponent emptyCard(String text) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(new EmptyBorder(14, 14, 14, 14));
        card.putClientProperty("FlatLaf.style",
                "background: lighten(@background,2%); arc: 16; border: 1,1,1,1, fade(@foreground,10%);");
        JLabel label = new JLabel(text);
        label.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,75%);");
        card.add(label, BorderLayout.CENTER);
        return card;
    }

/**
 * Handles attendance card.
 * @param r input value needed by this method.
 * @return resulting value produced by this method.
 */
    private JComponent attendanceCard(AttendanceEntry r) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 14, 14, 14));
        card.putClientProperty("FlatLaf.style",
                "background: lighten(@background,2%); arc: 16; border: 1,1,1,1, fade(@foreground,10%);");

        JLabel date = new JLabel(r.getDate());
        date.putClientProperty("FlatLaf.style", "font: bold 14;");

        JLabel in = new JLabel("Time-in:  " + (safe(r.getTimeIn()).isEmpty() ? "-" : r.getTimeIn()));
        in.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,78%);");

        JLabel out = new JLabel("Time-out: " + (safe(r.getTimeOut()).isEmpty() ? "-" : r.getTimeOut()));
        out.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,78%);");

        JLabel dur = new JLabel("Duration: " + (safe(r.getDuration()).isEmpty() ? "-" : r.getDuration()));
        dur.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,78%);");

        card.add(date);
        card.add(Box.createVerticalStrut(10));
        card.add(in);
        card.add(Box.createVerticalStrut(6));
        card.add(out);
        card.add(Box.createVerticalStrut(6));
        card.add(dur);

        return card;
    }

/**
 * Handles attendance header row.
 * @return resulting value produced by this method.
 */
    private JComponent attendanceHeaderRow() {
        JPanel row = baseRowPanel(true);
        row.add(colLabel("Date", true));
        row.add(colLabel("Time-in", true));
        row.add(colLabel("Time-out", true));
        row.add(colLabel("Duration", true));
        return row;
    }

/**
 * Handles attendance row.
 * @param r input value needed by this method.
 * @return resulting value produced by this method.
 */
    private JComponent attendanceRow(AttendanceEntry r) {
        JPanel row = baseRowPanel(false);

        row.add(colLabel(safe(r.getDate()), false));
        row.add(colLabel(safe(r.getTimeIn()).isEmpty() ? "-" : r.getTimeIn(), false));
        row.add(colLabel(safe(r.getTimeOut()).isEmpty() ? "-" : r.getTimeOut(), false));
        row.add(colLabel(safe(r.getDuration()).isEmpty() ? "-" : r.getDuration(), false));

        return row;
    }

/**
 * Handles base row panel.
 * @param header input value needed by this method.
 * @return resulting value produced by this method.
 */
    private JPanel baseRowPanel(boolean header) {
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setBorder(new EmptyBorder(12, 12, 12, 12));

        row.putClientProperty("FlatLaf.style",
                header
                        ? "background: darken(@background,4%); arc: 14; border: 1,1,1,1, fade(@foreground,10%);"
                        : "background: lighten(@background,2%); arc: 14; border: 1,1,1,1, fade(@foreground,10%);"
        );
        return row;
    }

/**
 * Handles col label.
 * @param text input value needed by this method.
 * @param header input value needed by this method.
 * @return resulting value produced by this method.
 */
    private JLabel colLabel(String text, boolean header) {
        JLabel l = new JLabel(text);
        l.putClientProperty("FlatLaf.style",
                header
                        ? "font: bold 13; foreground: fade(@foreground,85%);"
                        : "foreground: fade(@foreground,80%);"
        );
        return l;
    }

/**
 * Handles empty row.
 * @param text input value needed by this method.
 * @return resulting value produced by this method.
 */
    private JComponent emptyRow(String text) {
        JPanel row = baseRowPanel(false);
        row.setLayout(new BorderLayout());
        JLabel l = new JLabel(text);
        l.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,75%);");
        row.add(l, BorderLayout.WEST);
        return row;
    }

/**
 * Loads avatar icon.
 * @param resourcePath input value needed by this method.
 * @param w input value needed by this method.
 * @param h input value needed by this method.
 * @return resulting value produced by this method.
 */
    private ImageIcon loadAvatarIcon(String resourcePath, int w, int h) {
        java.net.URL url = getClass().getResource(resourcePath);
        if (url == null) return null;

        ImageIcon raw = new ImageIcon(url);
        Image scaled = raw.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

/**
 * Returns birthday or blank.
 * @param e input value needed by this method.
 * @return resulting value produced by this method.
 */
    private static String getBirthdayOrBlank(Employee e) {
        if (e == null) return "";
        try {
            Object v = e.getClass().getMethod("getBirthday").invoke(e);
            return v == null ? "" : String.valueOf(v);
        } catch (Exception ignored) { }

        try {
            Object v = e.getClass().getMethod("getDateOfBirth").invoke(e);
            return v == null ? "" : String.valueOf(v);
        } catch (Exception ignored) { }

        try {
            Object v = e.getClass().getMethod("getDob").invoke(e);
            return v == null ? "" : String.valueOf(v);
        } catch (Exception ignored) { }

        return "";
    }






    @SuppressWarnings("unchecked")

/**
 * Initializes components.
 */
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }




}
