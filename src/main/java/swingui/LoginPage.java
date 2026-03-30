package swingui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import dao.EmployeeFileManager;
import service.AuthService;
import service.LoginResult;
import service.AppSession;
import model.Employee;
import controller.DashboardController;
import service.AttendanceService;
import swingui.MotorPHMain;

/**
 * Represents the login page component used in the swingui layer.
 */
public class LoginPage extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
        java.util.logging.Logger.getLogger(LoginPage.class.getName());

    private final AuthService authService = new AuthService(new EmployeeFileManager());

    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox rememberMe;
    private JCheckBox showPassword;

    private JButton loginButton;
    private JButton forgotButton;
    private JButton createButton;

    private JLabel statusLabel;
    private JProgressBar loadingBar;

    private char defaultEchoChar;



/**
 * Creates a new LoginPage instance.
 */
    public LoginPage() {
        initComponents();
        initComponents();
        buildModernUI();
        setTitle("Sign in");
        setMinimumSize(new Dimension(980, 560));
        setLocationRelativeTo(null);
    }

/**
 * Handles sign in.
 */
    private void signIn() {
        String username = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            setError("Please enter both username and password.");
            return;
        }


        setLoading(true);
        setStatus("Signing in...");

        LoginResult result = authService.authenticate(username, password);

        if (result.isSuccess()) {
            Employee loggedInEmployee = result.getEmployee();
            String roleLabel = loggedInEmployee == null ? "" : loggedInEmployee.getPosition();


            JOptionPane.showMessageDialog(
                    this,
                    "Login successful! Welcome to MotorPH.\nRole/Position: " + roleLabel,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );


            emailField.setText("");
            passwordField.setText("");

            final LoginPage currentLoginPage = this;
            final Employee currentEmployee = loggedInEmployee;

            java.awt.EventQueue.invokeLater(() -> {
                try {

                    AppSession.setCurrentUser(currentEmployee);


                    MotorPHMain view = new MotorPHMain();
                    AttendanceService attendanceService = new AttendanceService("data/AttendanceRecord5.csv");
                    DashboardController controller = new DashboardController(view, attendanceService, currentEmployee);

                    controller.init();
                    currentLoginPage.dispose();
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error launching main application", e);
                    JOptionPane.showMessageDialog(null, "Error launching application: " + e.getMessage());

                    currentLoginPage.setVisible(true);
                    currentLoginPage.setLoading(false);
                    currentLoginPage.setError("Failed to launch application.");
                }
            });

        } else {
            setLoading(false);
            setError(result.getMessage());
            passwordField.setText("");
        }
    }

/**
 * Builds modern ui.
 */
    private void buildModernUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(24, 24, 24, 24));
        root.putClientProperty("FlatLaf.style", "background: darken(@background,2%);");

        JPanel grid = new JPanel(new GridLayout(1, 2, 18, 0));
        grid.setOpaque(false);

        grid.add(buildBrandPanel());
        grid.add(buildFormPanel());

        root.add(grid, BorderLayout.CENTER);


        setContentPane(root);
        revalidate();
        repaint();
        pack();
    }

/**
 * Builds brand panel.
 * @return resulting value produced by this method.
 */
    private JPanel buildBrandPanel() {
        JPanel p = new RoundedPanel(24);
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(28, 28, 28, 28));
        p.putClientProperty("FlatLaf.style", "background: darken(@background,5%);");

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel brand = new JLabel("MotorPH");
        brand.putClientProperty("FlatLaf.style", "font: bold 28;");

        JLabel subtitle = new JLabel("Payroll & HR Portal");
        subtitle.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,70%); font: 14;");

        top.add(brand);
        top.add(Box.createVerticalStrut(6));
        top.add(subtitle);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(16, 0, 0, 0));

        JLabel headline = new JLabel("<html><div style='line-height:1.15'>Welcome back.<br/>Let’s sign you in.</div></html>");
        headline.putClientProperty("FlatLaf.style", "font: semibold 22;");

        JLabel desc = new JLabel("<html><div style='width: 320px; line-height:1.35'>"
                + "Access your dashboard, employee records, and payroll tools in one place."
                + "</div></html>");
        desc.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,70%); font: 14;");

        center.add(headline);
        center.add(Box.createVerticalStrut(10));
        center.add(desc);

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        chips.setOpaque(false);
        chips.setBorder(new EmptyBorder(18, 0, 0, 0));
        chips.add(chip("Secure sign-in"));
        chips.add(chip("Role-based access"));
        chips.add(chip("Modern UI"));

        p.add(top, BorderLayout.NORTH);
        p.add(center, BorderLayout.CENTER);
        p.add(chips, BorderLayout.SOUTH);
        return p;
    }

/**
 * Handles chip.
 * @param text input value needed by this method.
 * @return resulting value produced by this method.
 */
    private JComponent chip(String text) {
        JLabel c = new JLabel(text);
        c.setBorder(new EmptyBorder(6, 10, 6, 10));
        c.putClientProperty("FlatLaf.style",
                "background: lighten(@background,3%); border: 1,1,1,1, fade(@foreground,12%);"
                        + "foreground: fade(@foreground,75%);");
        c.setOpaque(true);
        return c;
    }

/**
 * Builds form panel.
 * @return resulting value produced by this method.
 */
    private JPanel buildFormPanel() {
        JPanel p = new RoundedPanel(24);
        p.setLayout(new GridBagLayout());
        p.setBorder(new EmptyBorder(28, 28, 28, 28));
        p.putClientProperty("FlatLaf.style", "background: @background;");


        emailField = new JTextField();
        passwordField = new JPasswordField();
            passwordField.addActionListener(e -> signIn());
        rememberMe = new JCheckBox("Remember me");
        showPassword = new JCheckBox("Show");

        loginButton = new JButton("Sign in");
            loginButton.addActionListener(e -> signIn());
        forgotButton = new JButton("Forgot password?");
        createButton = new JButton("Create account");

        statusLabel = new JLabel(" ");
        loadingBar = new JProgressBar();


        styleField(emailField);
        emailField.putClientProperty("JTextField.placeholderText", "name@company.com");

        styleField(passwordField);
        passwordField.putClientProperty("JTextField.placeholderText", "••••••••");

        rememberMe.setOpaque(false);
        rememberMe.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,80%);");

        showPassword.setOpaque(false);
        showPassword.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,80%);");

        forgotButton.setBorderPainted(false);
        forgotButton.setContentAreaFilled(false);
        forgotButton.setFocusPainted(false);
        forgotButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotButton.putClientProperty("FlatLaf.style", "foreground: @accentColor; font: 13;");

        createButton.setBorderPainted(false);
        createButton.setContentAreaFilled(false);
        createButton.setFocusPainted(false);
        createButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createButton.putClientProperty("FlatLaf.style", "foreground: @accentColor; font: 13;");

        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.putClientProperty("FlatLaf.style",
                "arc: 12; font: 14; focusWidth: 1; background: @accentColor;");
        loginButton.setPreferredSize(new Dimension(0, 44));

        statusLabel.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,70%);");

        loadingBar.setIndeterminate(true);
        loadingBar.setVisible(false);
        loadingBar.putClientProperty("FlatLaf.style", "arc: 999;");


        showPassword.addActionListener(e -> setPasswordVisible(showPassword.isSelected()));


        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(0, 0, 12, 0);

        JLabel title = new JLabel("Sign in");
        title.putClientProperty("FlatLaf.style", "font: bold 22;");
        gc.gridy = 0;
        p.add(title, gc);

        JLabel hint = new JLabel("Use your company account to continue.");
        hint.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,70%);");
        gc.gridy = 1;
        p.add(hint, gc);

        gc.gridy = 2;
        p.add(Box.createVerticalStrut(6), gc);

        JLabel emailLbl = new JLabel("Email");
        emailLbl.putClientProperty("FlatLaf.style", "font: semibold 13;");
        gc.gridy = 3;
        p.add(emailLbl, gc);

        gc.gridy = 4;
        p.add(emailField, gc);

        JLabel passLbl = new JLabel("Password");
        passLbl.putClientProperty("FlatLaf.style", "font: 13;");
        gc.gridy = 5;
        p.add(passLbl, gc);

        JPanel passRow = new JPanel(new BorderLayout(10, 0));
        passRow.setOpaque(false);
        passRow.add(passwordField, BorderLayout.CENTER);
        passRow.add(showPassword, BorderLayout.EAST);

        gc.gridy = 6;
        p.add(passRow, gc);

        JPanel opts = new JPanel(new BorderLayout());
        opts.setOpaque(false);
        opts.add(rememberMe, BorderLayout.WEST);
        opts.add(forgotButton, BorderLayout.EAST);

        gc.gridy = 7;
        p.add(opts, gc);

        gc.gridy = 8;
        gc.insets = new Insets(6, 0, 10, 0);
        p.add(loginButton, gc);

        gc.gridy = 9;
        gc.insets = new Insets(0, 0, 10, 0);
        p.add(loadingBar, gc);

        gc.gridy = 10;
        gc.insets = new Insets(0, 0, 14, 0);
        p.add(statusLabel, gc);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottom.setOpaque(false);

        JLabel noAcc = new JLabel("New here? ");
        noAcc.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,70%);");

        bottom.add(noAcc);
        bottom.add(createButton);

        gc.gridy = 11;
        gc.weighty = 1;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.insets = new Insets(0, 0, 0, 0);
        p.add(bottom, gc);

        return p;
    }

/**
 * Handles style field.
 * @param field input value needed by this method.
 */
    private static void styleField(JComponent field) {
        field.putClientProperty("FlatLaf.style",
                "arc: 12; borderWidth: 1; focusWidth: 1; margin: 10,12,10,12;");
        if (field instanceof JTextField tf) tf.setColumns(10);
        if (field instanceof JPasswordField pf) pf.setColumns(10);
    }

/**
 * Updates password visible.
 * @param visible input value needed by this method.
 */
    private void setPasswordVisible(boolean visible) {
        if (defaultEchoChar == 0) {
            defaultEchoChar = passwordField.getEchoChar();
        }

        passwordField.setEchoChar(visible ? (char) 0 : defaultEchoChar);
        passwordField.requestFocusInWindow();
    }

/**
 * Updates loading.
 * @param loading input value needed by this method.
 */
    public void setLoading(boolean loading) {
    loadingBar.setVisible(loading);
    loginButton.setEnabled(!loading);
    emailField.setEnabled(!loading);
    passwordField.setEnabled(!loading);
    rememberMe.setEnabled(!loading);
    showPassword.setEnabled(!loading);
    }

/**
 * Updates status.
 * @param text input value needed by this method.
 */
    public void setStatus(String text) {
        statusLabel.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,70%);");
        statusLabel.setText(text == null || text.isBlank() ? " " : text);
    }

/**
 * Updates error.
 * @param text input value needed by this method.
 */
    public void setError(String text) {
        statusLabel.putClientProperty("FlatLaf.style", "foreground: #ff6b6b;");
        statusLabel.setText(text == null || text.isBlank() ? " " : text);
    }


/**
 * Handles on login.
 * @param l input value needed by this method.
 */
    public void onLogin(ActionListener l) {
        loginButton.addActionListener(l);
        passwordField.addActionListener(l);
    }

/**
 * Handles on forgot password.
 * @param l input value needed by this method.
 */
    public void onForgotPassword(ActionListener l) {
        forgotButton.addActionListener(l);
    }

/**
 * Handles on create account.
 * @param l input value needed by this method.
 */
    public void onCreateAccount(ActionListener l) {
        createButton.addActionListener(l);
    }

/**
 * Returns email.
 * @return resulting value produced by this method.
 */
    public String getEmail() {
        return emailField.getText().trim();
    }

/**
 * Returns password.
 * @return resulting value produced by this method.
 */
    public char[] getPassword() {
        return passwordField.getPassword();
    }

/**
 * Checks whether remember me checked is true.
 * @return resulting value produced by this method.
 */
    public boolean isRememberMeChecked() {
        return rememberMe.isSelected();
    }


/**
 * Represents the rounded panel component used in the swingui layer.
 */
    static class RoundedPanel extends JPanel {
        private final int arc;

/**
 * Creates a new RoundedPanel instance.
 * @param arc input value needed by this method.
 */
        RoundedPanel(int arc) {
            this.arc = arc;
            setOpaque(false);
        }

        @Override
/**
 * Handles paint component.
 * @param g input value needed by this method.
 */
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

                Color border = UIManager.getColor("Component.borderColor");
                if (border == null) border = new Color(255, 255, 255, 28);
                g2.setColor(new Color(border.getRed(), border.getGreen(), border.getBlue(), 60));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            } finally {
                g2.dispose();
            }
            super.paintComponent(g);
        }
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
