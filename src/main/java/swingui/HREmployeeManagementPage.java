package swingui;

import dao.CredentialsRepository;
import dao.EmployeeFileManager;
import model.Employee;
import model.Permission;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Represents the hremployee management page component used in the swingui layer.
 */
public class HREmployeeManagementPage extends JPanel {

    private final EmployeeFileManager employeeFileManager;
    @SuppressWarnings("unused")
    private final Employee loggedUser;
    private final CredentialsRepository credentialsRepository;

    private final JPanel cardsPanel = new JPanel();
    private final JLabel emptyLabel = new JLabel("No employees loaded.");
    private final JButton addBtn = new JButton("Add Employee");
    private final JButton refreshBtn = new JButton("Refresh");

    private final JButton activeBtn = new JButton("Active");
    private final JButton inactiveBtn = new JButton("Inactive");
    private boolean showActive = true;

    /**
     * Creates a new HREmployeeManagementPage instance.
     * @param employeeFileManager input value needed by this method.
     * @param loggedUser input value needed by this method.
     */
    public HREmployeeManagementPage(EmployeeFileManager employeeFileManager, Employee loggedUser) {
        this.employeeFileManager = (employeeFileManager == null) ? new EmployeeFileManager() : employeeFileManager;
        this.loggedUser = loggedUser;
        this.credentialsRepository = new CredentialsRepository();

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        add(buildCard());
        add(Box.createVerticalGlue());

        wireActions();
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
        JLabel title = new JLabel("Employee Management");
        title.putClientProperty("FlatLaf.style", "font: bold 16;");

        activeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        inactiveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        activeBtn.putClientProperty("FlatLaf.style", "arc: 12; margin: 8,12,8,12; font: bold 12;");
        inactiveBtn.putClientProperty("FlatLaf.style", "arc: 12; margin: 8,12,8,12; font: bold 12;");

        addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addBtn.putClientProperty("FlatLaf.style", "arc: 12; margin: 8,12,8,12; font: bold 12; background: fade(@accentColor,10%);");
        addBtn.setVisible(canAddEmployees());

        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.putClientProperty("FlatLaf.style", "arc: 12; margin: 8,12,8,12; font: bold 12;");

        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightBtns.setOpaque(false);
        rightBtns.add(addBtn);
        rightBtns.add(refreshBtn);

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftHeader.setOpaque(false);
        leftHeader.add(title);
        leftHeader.add(activeBtn);
        leftHeader.add(inactiveBtn);

        header.add(leftHeader, BorderLayout.WEST);
        header.add(rightBtns, BorderLayout.EAST);
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
        addBtn.addActionListener(e -> openAddDialog());

        activeBtn.addActionListener(e -> {
            showActive = true;
            updateFilterButtons();
            reload();
        });

        inactiveBtn.addActionListener(e -> {
            showActive = false;
            updateFilterButtons();
            reload();
        });

        updateFilterButtons();
    }

    /**
     * Opens add dialog.
     */
    private void openAddDialog() {
        if (!canAddEmployees()) {
            JOptionPane.showMessageDialog(this,
                    "You do not have permission to add employees.",
                    "Access Denied",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nextId = employeeFileManager.getNextEmployeeId();

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Add Employee - " + nextId,
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 10));

        JTextField[] fields = new JTextField[19];
        String[] labels = new String[]{
                "Employee #", "Last Name", "First Name", "Birthday", "Address", "Phone Number",
                "SSS", "PhilHealth", "TIN", "Pag-IBIG", "Status", "Position", "Supervisor",
                "Basic Salary", "Rice Subsidy", "Phone Allowance", "Clothing Allowance",
                "Gross Semi-monthly", "Hourly Rate"
        };

        String[] defaults = new String[]{
                nextId,
                "N/A",
                "N/A",
                "N/A",
                "N/A",
                "N/A",
                "N/A",
                "N/A",
                "N/A",
                "N/A",
                "N/A",
                "N/A",
                "N/A",
                "0",
                "0",
                "0",
                "0",
                "0",
                "0"
        };

        for (int i = 0; i < fields.length; i++) {
            fields[i] = new JTextField();
            fields[i].putClientProperty("FlatLaf.style", "arc: 10;");
            fields[i].setText(defaults[i]);
            if (i == 0) fields[i].setEditable(false);
            form.add(metaLabel(labels[i]));
            form.add(fields[i]);
        }

        JScrollPane sp = new JScrollPane(form);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        root.add(sp, BorderLayout.CENTER);

        JButton submit = new JButton("Submit");
        submit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submit.putClientProperty("FlatLaf.style",
                "arc: 12; margin: 10,14,10,14; font: bold 12; background: fade(@accentColor,18%);");

        JButton cancel = new JButton("Cancel");
        cancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancel.putClientProperty("FlatLaf.style", "arc: 12; margin: 10,14,10,14; font: bold 12;");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(submit);
        buttons.add(cancel);

        root.add(buttons, BorderLayout.SOUTH);

        cancel.addActionListener(e -> dialog.dispose());

        submit.addActionListener(e -> {
            String[] newRec = new String[19];
            for (int i = 0; i < 19; i++) {
                String v = fields[i].getText();
                v = (v == null) ? "" : v.trim();
                if (v.isEmpty()) {
                    v = defaults[i];
                }
                newRec[i] = v;
            }

            newRec[0] = nextId;

            EmployeeFileManager.Result res = employeeFileManager.addRecord(newRec);
            JOptionPane.showMessageDialog(dialog, res.getMessage(), res.isOk() ? "Added" : "Add Error",
                    res.isOk() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            if (res.isOk()) {
                dialog.dispose();
                reload();
            }
        });

        dialog.setContentPane(root);
        dialog.setSize(new Dimension(720, 520));
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }

    /**
     * Reloads the required data.
     */
    public final void reload() {
        cardsPanel.removeAll();

        EmployeeFileManager.TableData table;
        if (showActive) {
            employeeFileManager.loadFile();
            table = employeeFileManager.getCachedTableData();
        } else {
            table = employeeFileManager.loadRemovedTableData();
        }
        List<List<String>> rows = table.getRows();

        if (rows == null || rows.isEmpty()) {
            cardsPanel.add(emptyLabel);
            revalidate();
            repaint();
            return;
        }

        cardsPanel.add(buildHeaderRow());
        cardsPanel.add(Box.createVerticalStrut(6));

        for (List<String> row : rows) {
            if (row == null || row.isEmpty()) continue;
            if ("Employee #".equalsIgnoreCase(safe(get(row, 0)))) continue;
            cardsPanel.add(buildRowCard(toRow(row)));
            cardsPanel.add(Box.createVerticalStrut(10));
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

        int colCount = isITEmailView() ? 5 : 4;

        JPanel cols = new JPanel(new GridLayout(1, colCount, 12, 0));
        cols.setOpaque(false);
        cols.add(headerLabel("Employee ID"));
        cols.add(headerLabel("Last, First"));
        cols.add(headerLabel("Status"));
        cols.add(headerLabel("Position"));
        if (isITEmailView()) {
            cols.add(headerLabel("Has Email?"));
        }

        row.add(cols, BorderLayout.CENTER);

        JLabel spacer = headerLabel("");
        spacer.setPreferredSize(new Dimension(90, 1));
        row.add(spacer, BorderLayout.EAST);
        return row;
    }

    /**
     * Builds row card.
     * @param r input value needed by this method.
     * @return resulting value produced by this method.
     */
    private JComponent buildRowCard(EmployeeRow r) {
        final int ROW_H = 64;

        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(true);
        row.setBorder(new EmptyBorder(10, 12, 10, 12));
        row.putClientProperty("FlatLaf.style",
                "background: darken(@background,3%); arc: 14; border: 1,1,1,1, fade(@foreground,8%);");

        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW_H));
        row.setPreferredSize(new Dimension(0, ROW_H));
        row.setMinimumSize(new Dimension(0, ROW_H));

        int colCount = isITEmailView() ? 5 : 4;

        JPanel cols = new JPanel(new GridLayout(1, colCount, 12, 0));
        cols.setOpaque(false);

        JLabel id = new JLabel(r.id);
        id.putClientProperty("FlatLaf.style", "font: bold 13;");

        JLabel name = new JLabel(r.lastName + ", " + r.firstName);
        name.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,80%);");

        JLabel status = new JLabel(r.status);
        status.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,80%);");

        JLabel position = new JLabel(r.position);
        position.putClientProperty("FlatLaf.style", "foreground: fade(@foreground,80%);");

        cols.add(id);
        cols.add(name);
        cols.add(status);
        cols.add(position);

        if (isITEmailView()) {
            JCheckBox hasEmailBox = new JCheckBox();
            hasEmailBox.setOpaque(false);
            hasEmailBox.setHorizontalAlignment(SwingConstants.CENTER);
            hasEmailBox.setSelected(r.hasEmail);
            hasEmailBox.setEnabled(false);
            cols.add(hasEmailBox);
        }

        JButton manageBtn = new JButton("Manage");
        manageBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        manageBtn.putClientProperty("FlatLaf.style", "arc: 12; margin: 10,14,10,14; font: bold 13;");
        manageBtn.setVisible(canManageAnyEmployeeAction());
        manageBtn.addActionListener(e -> {
            if (isAddEmailOnlyUser()) {
                openManageCredentialsDialog(r);
            } else {
                openManageDialog(r);
            }
        });

        row.add(cols, BorderLayout.CENTER);
        if (canManageAnyEmployeeAction()) {
            row.add(manageBtn, BorderLayout.EAST);
        }
        return row;
    }

    /**
     * Opens manage dialog.
     * @param r input value needed by this method.
     */
    private void openManageDialog(EmployeeRow r) {
        if (!canManageEmployeeRecord()) {
            if (isAddEmailOnlyUser()) {
                openManageCredentialsDialog(r);
                return;
            }
            JOptionPane.showMessageDialog(this,
                    "You do not have permission to manage employee records.",
                    "Access Denied",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Manage Employee - " + r.id,
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 10));

        JTextField[] fields = new JTextField[19];
        String[] labels = new String[]{
                "Employee #", "Last Name", "First Name", "Birthday", "Address", "Phone Number",
                "SSS", "PhilHealth", "TIN", "Pag-IBIG", "Status", "Position", "Supervisor",
                "Basic Salary", "Rice Subsidy", "Phone Allowance", "Clothing Allowance",
                "Gross Semi-monthly", "Hourly Rate"
        };

        for (int i = 0; i < fields.length; i++) {
            fields[i] = new JTextField();
            fields[i].putClientProperty("FlatLaf.style", "arc: 10;");
            fields[i].setText(r.data[i] == null ? "" : r.data[i]);
            if (i == 0) {
                fields[i].setEditable(false);
            } else {
                fields[i].setEditable(canEditEmployees());
            }

            form.add(metaLabel(labels[i]));
            form.add(fields[i]);
        }

        JScrollPane sp = new JScrollPane(form);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        root.add(sp, BorderLayout.CENTER);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteBtn.putClientProperty("FlatLaf.style",
                "arc: 12; margin: 10,14,10,14; font: bold 12; background: fade(#ff4d4d,18%);");
        deleteBtn.setVisible(canDeleteEmployees());

        JButton submit = new JButton("Submit");
        submit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submit.putClientProperty("FlatLaf.style",
                "arc: 12; margin: 10,14,10,14; font: bold 12; background: fade(@accentColor,18%);");
        submit.setVisible(canEditEmployees());

        JButton cancel = new JButton("Cancel");
        cancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancel.putClientProperty("FlatLaf.style", "arc: 12; margin: 10,14,10,14; font: bold 12;");

        JPanel buttons = new JPanel(new BorderLayout());
        buttons.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        if (canDeleteEmployees()) {
            left.add(deleteBtn);
        }

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        if (canEditEmployees()) {
            right.add(submit);
        }
        right.add(cancel);

        buttons.add(left, BorderLayout.WEST);
        buttons.add(right, BorderLayout.EAST);

        root.add(buttons, BorderLayout.SOUTH);

        cancel.addActionListener(e -> dialog.dispose());

        submit.addActionListener(e -> {
            if (!canEditEmployees()) {
                JOptionPane.showMessageDialog(dialog,
                        "You do not have permission to edit employees.",
                        "Access Denied",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] updated = new String[19];
            for (int i = 0; i < 19; i++) {
                updated[i] = fields[i].getText() == null ? "" : fields[i].getText().trim();
            }
            EmployeeFileManager.Result res = employeeFileManager.updateRecord(updated);
            JOptionPane.showMessageDialog(dialog, res.getMessage(), res.isOk() ? "Updated" : "Update Error",
                    res.isOk() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            if (res.isOk()) {
                dialog.dispose();
                reload();
            }
        });

        deleteBtn.addActionListener(e -> {
            if (!canDeleteEmployees()) {
                JOptionPane.showMessageDialog(dialog,
                        "You do not have permission to delete employees.",
                        "Access Denied",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Delete employee " + r.id + "? This will move the record to removedEmployees.txt.",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            EmployeeFileManager.Result res = employeeFileManager.deleteRecord(r.id);
            JOptionPane.showMessageDialog(dialog, res.getMessage(), res.isOk() ? "Deleted" : "Delete Error",
                    res.isOk() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            if (res.isOk()) {
                dialog.dispose();
                reload();
            }
        });

        dialog.setContentPane(root);
        dialog.setSize(new Dimension(720, 520));
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }

    private void openManageCredentialsDialog(EmployeeRow r) {
        if (!canAddEmail()) {
            JOptionPane.showMessageDialog(this,
                    "You do not have permission to add employee credentials.",
                    "Access Denied",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Manage Credentials - " + r.id,
                Dialog.ModalityType.APPLICATION_MODAL
        );
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 10));

        JTextField employeeNumberField = new JTextField(r.id);
        employeeNumberField.setEditable(false);
        employeeNumberField.putClientProperty("FlatLaf.style", "arc: 10;");

        String generatedEmail = buildEmail(r.firstName, r.lastName);
        JTextField emailField = new JTextField(generatedEmail);
        emailField.putClientProperty("FlatLaf.style", "arc: 10;");

        String temporaryPassword = generateRandomAlphaNumeric(8);
        System.out.println("TEMPORARY PASSWORD for Employee #" + r.id + ": " + temporaryPassword);
        JPasswordField passwordField = new JPasswordField(temporaryPassword);
        passwordField.putClientProperty("FlatLaf.style", "arc: 10;");
        passwordField.setEchoChar('•');

        JComboBox<String> roleBox = new JComboBox<>(new String[]{"HR", "IT", "FINANCE", "ADMIN", "EMPLOYEE"});
        roleBox.putClientProperty("FlatLaf.style", "arc: 10;");
        roleBox.setSelectedItem(suggestRole(r.position));

        form.add(metaLabel("Employee #"));
        form.add(employeeNumberField);
        form.add(metaLabel("First Name"));
        form.add(readOnlyField(r.firstName));
        form.add(metaLabel("Last Name"));
        form.add(readOnlyField(r.lastName));
        form.add(metaLabel("Email"));
        form.add(emailField);
        form.add(metaLabel("Temporary Password"));
        form.add(passwordField);
        form.add(metaLabel("Role"));
        form.add(roleBox);

        root.add(form, BorderLayout.CENTER);

        JButton submit = new JButton("Submit");
        submit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submit.putClientProperty("FlatLaf.style",
                "arc: 12; margin: 10,14,10,14; font: bold 12; background: fade(@accentColor,18%);");

        JButton cancel = new JButton("Cancel");
        cancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancel.putClientProperty("FlatLaf.style", "arc: 12; margin: 10,14,10,14; font: bold 12;");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(submit);
        buttons.add(cancel);
        root.add(buttons, BorderLayout.SOUTH);

        cancel.addActionListener(e -> dialog.dispose());
        submit.addActionListener(e -> {
            String employeeId = safe(employeeNumberField.getText());
            String email = safe(emailField.getText()).toLowerCase(Locale.ROOT);
            String password = new String(passwordField.getPassword());
            String role = safe((String) roleBox.getSelectedItem()).toUpperCase(Locale.ROOT);

            if (employeeId.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "All credential fields are required.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String generatedOtp = generateNumericCode(6);
            System.out.println("OTP for Employee #" + employeeId + ": " + generatedOtp);
            String enteredOtp = JOptionPane.showInputDialog(dialog,
                    "Enter OTP to confirm credential creation:",
                    "OTP Verification",
                    JOptionPane.PLAIN_MESSAGE);

            if (enteredOtp == null) {
                return;
            }
            if (!generatedOtp.equals(enteredOtp.trim())) {
                JOptionPane.showMessageDialog(dialog,
                        "Invalid OTP.",
                        "OTP Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            CredentialsRepository.Result result = credentialsRepository.saveCredential(employeeId, email, password, role);
            JOptionPane.showMessageDialog(dialog,
                    result.getMessage(),
                    result.isOk() ? "Credentials Saved" : "Credential Error",
                    result.isOk() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            if (result.isOk()) {
                reload();
                dialog.dispose();
            }
        });

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setSize(new Dimension(560, 320));
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }

    /**
     * Updates filter buttons.
     */
    private void updateFilterButtons() {
        if (showActive) {
            activeBtn.putClientProperty("FlatLaf.style", "arc: 12; font: bold 12; background: fade(@accentColor,18%);");
            inactiveBtn.putClientProperty("FlatLaf.style", "arc: 12; font: 12;");
        } else {
            inactiveBtn.putClientProperty("FlatLaf.style", "arc: 12; font: bold 12; background: fade(@accentColor,18%);");
            activeBtn.putClientProperty("FlatLaf.style", "arc: 12; font: 12;");
        }

        activeBtn.setEnabled(!showActive);
        inactiveBtn.setEnabled(showActive);
    }

    private boolean canAddEmployees() {
        return loggedUser != null && loggedUser.can(Permission.ADD_EMPLOYEES);
    }

    private boolean canEditEmployees() {
        return loggedUser != null && loggedUser.can(Permission.EDIT_EMPLOYEES);
    }

    private boolean canDeleteEmployees() {
        return loggedUser != null && loggedUser.can(Permission.DELETE_EMPLOYEES);
    }

    private boolean canAddEmail() {
        return loggedUser != null && loggedUser.can(Permission.ADD_EMAIL);
    }

    private boolean canManageEmployeeRecord() {
        return canEditEmployees() || canDeleteEmployees();
    }

    private boolean canManageAnyEmployeeAction() {
        return canManageEmployeeRecord() || canAddEmail();
    }

    private boolean isAddEmailOnlyUser() {
        return canAddEmail() && !canEditEmployees() && !canDeleteEmployees() && !canAddEmployees();
    }

    private boolean isITEmailView() {
        return canAddEmail();
    }

    private JTextField readOnlyField(String value) {
        JTextField field = new JTextField(value == null ? "" : value);
        field.putClientProperty("FlatLaf.style", "arc: 10;");
        field.setEditable(false);
        return field;
    }

    private String buildEmail(String firstName, String lastName) {
        String first = normalizeEmailToken(firstName);
        String last = normalizeEmailToken(lastName);
        if (first.isEmpty() && last.isEmpty()) {
            return "employee@motorph.local";
        }
        if (first.isEmpty()) {
            return last + "@motorph.local";
        }
        if (last.isEmpty()) {
            return first + "@motorph.local";
        }
        return first + "." + last + "@motorph.local";
    }

    private String normalizeEmailToken(String value) {
        return safe(value)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", ".")
                .replaceAll("^\\.+|\\.+$", "");
    }

    private String suggestRole(String position) {
        String p = safe(position).toUpperCase(Locale.ROOT);
        if (p.contains("HR")) return "HR";
        if (p.contains("IT")) return "IT";
        if (p.contains("FINANCE") || p.contains("ACCOUNTING") || p.contains("PAYROLL")) return "FINANCE";
        if (p.contains("ADMIN") || p.contains("MANAGER") || p.contains("CHIEF") || p.contains("TEAM LEADER")) return "ADMIN";
        return "EMPLOYEE";
    }

    private String generateRandomAlphaNumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String generateNumericCode(int length) {
        String digits = "0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(digits.charAt(random.nextInt(digits.length())));
        }
        return sb.toString();
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
     * Handles safe.
     * @param s input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String safe(String s) { return s == null ? "" : s.trim(); }

    /**
     * Returns the stored value.
     * @param row input value needed by this method.
     * @param idx input value needed by this method.
     * @return resulting value produced by this method.
     */
    private static String get(List<String> row, int idx) {
        if (row == null || idx < 0 || idx >= row.size()) return "";
        return row.get(idx);
    }

    /**
     * Handles to row.
     * @param row input value needed by this method.
     * @return resulting value produced by this method.
     */
    private EmployeeRow toRow(List<String> row) {
        String[] data = new String[19];
        for (int i = 0; i < 19; i++) data[i] = get(row, i);
        String employeeId = safe(data[0]);
        boolean hasEmail = credentialsRepository.hasCredentials(employeeId);
        return new EmployeeRow(
                employeeId,
                safe(data[1]),
                safe(data[2]),
                safe(data[10]),
                safe(data[11]),
                data,
                hasEmail
        );
    }

    /**
     * Handles employee row.
     * @param id input value needed by this method.
     * @param lastName input value needed by this method.
     * @param firstName input value needed by this method.
     * @param status input value needed by this method.
     * @param position input value needed by this method.
     * @param data input value needed by this method.
     * @return resulting value produced by this method.
     */
    private record EmployeeRow(
            String id,
            String lastName,
            String firstName,
            String status,
            String position,
            String[] data,
            boolean hasEmail
    ) {}
}
