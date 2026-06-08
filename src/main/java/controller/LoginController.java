package controller;

import model.Employee;
import service.LoginResult;
import service.LoginService;
import service.AttendanceService;
import swingui.MotorPHMain;

import javax.swing.*;
import service.AppSession;
import swingui.LoginPage;

public class LoginController {

    private final LoginPage view;
    private final LoginService loginService;

    public LoginController(LoginPage view,
                           LoginService loginService) {
        this.view = view;
        this.loginService = loginService;

        init();
    }

    private void init() {
        view.setLoginHandler(this::handleLogin);
    }

    private void handleLogin(String username, String password) {

        view.setLoading(true);
        view.setStatus("Signing in...");

        LoginResult result =
                loginService.authenticate(username, password);

        if (!result.isSuccess()) {
            view.setLoading(false);
            view.setError(result.getMessage());
            return;
        }

        Employee employee = result.getEmployee();

        AppSession.setCurrentUser(employee);

        JOptionPane.showMessageDialog(
                view,
                "Login successful! Welcome to MotorPH.\nRole/Position: "
                        + employee.getPosition(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

        openDashboard(employee);
    }

    private void openDashboard(Employee employee) {

        try {

            MotorPHMain mainView = new MotorPHMain();

            AttendanceService attendanceService =
                    new AttendanceService("data/AttendanceRecord5.csv");

            DashboardController controller =
                    new DashboardController(
                            mainView,
                            attendanceService,
                            employee
                    );

            controller.init();

            view.dispose();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    view,
                    "Failed to launch application: " + e.getMessage()
            );

            view.setLoading(false);
            view.setError("Application launch failed.");
        }
    }
}