package controller;

import dao.EmployeeFileManager;
import dao.PayrollLedgerRepository;
import dao.LeaveLedgerRepository;
import model.Employee;
import model.AttendanceEntry;
import model.Permission;
import service.AttendanceService;
import service.AppSession;
import swingui.PayrollProcessingPage;
import swingui.PayrollHistoryPage;
import swingui.LeaveRequestPage;
import swingui.LeaveApprovalPage;
import swingui.HREmployeeManagementPage;
import swingui.MotorPHMainView;
import swingui.LoginPage;

import javax.swing.*;
import java.util.List;

/**
 * Represents the dashboard controller component used in the controller layer.
 */
public class DashboardController {

    private final MotorPHMainView view;
    private final AttendanceService attendanceService;
    private final Employee employee;
    private final EmployeeFileManager employeeFileManager = new EmployeeFileManager();
    private final PayrollLedgerRepository payrollLedgerRepository = new PayrollLedgerRepository();
    private final LeaveLedgerRepository leaveLedgerRepository = new LeaveLedgerRepository();

    /**
     * Creates a new DashboardController instance.
     * @param view input value needed by this method.
     * @param attendanceService input value needed by this method.
     * @param employee input value needed by this method.
     */
    public DashboardController(MotorPHMainView view, AttendanceService attendanceService, Employee employee) {
        this.view = view;
        this.attendanceService = attendanceService;
        this.employee = employee;
    }

    /**
     * Initializes the required data.
     */
    public void init() {
        view.setEmployee(employee);

        view.onNavSelect(view::showPage);

        view.addNavItem("Dashboard");
        view.showPage("Dashboard");

        PayrollHistoryPage payrollHistoryPage = new PayrollHistoryPage(payrollLedgerRepository, employee);
        view.registerPage("My Payroll", payrollHistoryPage);
        view.addNavItem("My Payroll");

        if (employee.can(Permission.RUN_PAYROLL)) {
            view.registerPage(
                    "Payroll",
                    new PayrollProcessingPage(
                            attendanceService,
                            EmployeeFileManager.getEmployeeModelList(),
                            payrollLedgerRepository,
                            payrollHistoryPage::reload
                    )
            );
            view.addNavItem("Payroll");
        }

        LeaveRequestPage leaveRequestPage = new LeaveRequestPage(leaveLedgerRepository, employee);
        view.registerPage("Leave Request", leaveRequestPage);
        view.addNavItem("Leave Request");

        if (employee.can(Permission.APPROVE_LEAVE)) {
            view.registerPage("Leave Approval", new LeaveApprovalPage(leaveLedgerRepository, employee));
            view.addNavItem("Leave Approval");
        }

        if (employee.can(Permission.EDIT_EMPLOYEES)
                || employee.can(Permission.ADD_EMPLOYEES)
                || employee.can(Permission.DELETE_EMPLOYEES)
                || employee.can(Permission.ADD_EMAIL)) {

            employeeFileManager.loadFile();
            view.registerPage("Employee Management", new HREmployeeManagementPage(employeeFileManager, employee));
            view.addNavItem("Employee Management");
        }

        view.onTimeIn(e -> {
            attendanceService.timeIn(employee);
            view.setStatus("Time-in recorded.", false);
            reloadAttendance();
        });

        view.onTimeOut(e -> {
            boolean ok = attendanceService.timeOut(employee);
            if (ok) {
                view.setStatus("Time-out recorded.", false);
            } else {
                view.setStatus("No open time-in found today.", true);
            }
            reloadAttendance();
        });

        view.onLogout(e -> {
            AppSession.clear();
            view.closeWindow();
            SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
        });
        reloadAttendance();
        view.showWindow();
    }

    /**
     * Reloads attendance.
     */
    private void reloadAttendance() {
        SwingUtilities.invokeLater(() -> {
            List<AttendanceEntry> entries = attendanceService.getAllFor(employee);
            view.setAttendance(entries);
        });
    }
}
