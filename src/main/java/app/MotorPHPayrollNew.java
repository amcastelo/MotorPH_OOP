package app;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.UIManager;
import swingui.LoginPage;

/**
 * Represents the motor phpayroll new component used in the app layer.
 */
public class MotorPHPayrollNew {

/**
 * Handles main.
 * @param args input value needed by this method.
 */
    public static void main(String[] args) {
        uiManager();
        runLoginPage();
    }

/**
 * Handles run login page.
 */
    private static void runLoginPage() {
        java.awt.EventQueue.invokeLater(() -> {
            new LoginPage().setVisible(true);
        });
    }

/**
 * Handles ui manager.
 */
    private static void uiManager() {
        try {
            FlatDarkLaf.setup();
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
