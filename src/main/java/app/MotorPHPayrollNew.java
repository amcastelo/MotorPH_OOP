package app;

import com.formdev.flatlaf.FlatLightLaf;
import controller.LoginController;
import dao.CredentialsRepository;
import dao.EmployeeFileManager;
import service.LoginService;
import service.AuthService;
import swingui.LoginPage;

import javax.swing.UIManager;

/**
 * Application entry point.
 */
public class MotorPHPayrollNew {

    public static void main(String[] args) {
        uiManager();
        runLoginPage();
    }

    /**
     * Starts application using MVC wiring.
     */
    private static void runLoginPage() {
        java.awt.EventQueue.invokeLater(() -> {

            // =========================
            // 1. DATA / REPOSITORY LAYER
            // =========================
            EmployeeFileManager employeeFileManager =
                    new EmployeeFileManager();

            CredentialsRepository credentialsRepository =
                    new CredentialsRepository();

            // =========================
            // 2. SERVICE LAYER
            // =========================
            AuthService authService =
                    new AuthService(
                            employeeFileManager,
                            credentialsRepository
                    );

            LoginService loginService =
                    new LoginService(authService);

            // =========================
            // 3. VIEW
            // =========================
            LoginPage loginPage = new LoginPage();

            // =========================
            // 4. CONTROLLER (WIRING)
            // =========================
            new LoginController(loginPage, loginService);

            // =========================
            // 5. SHOW UI
            // =========================
            loginPage.setVisible(true);
        });
    }

    /**
     * Sets UI Look and Feel.
     */
    private static void uiManager() {
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}