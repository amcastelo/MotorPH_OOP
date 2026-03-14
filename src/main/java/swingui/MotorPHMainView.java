



package swingui;

import model.AttendanceEntry;
import model.Employee;

import java.awt.event.ActionListener;
import java.util.List;





/**
 * Defines the motor phmain view contract used in the swingui layer.
 */
public interface MotorPHMainView {
    void setEmployee(Employee e);
    void setStatus(String text, boolean isError);

    void setAttendance(List<AttendanceEntry> entries);

    void onTimeIn(ActionListener l);
    void onTimeOut(ActionListener l);
    void onLogout(ActionListener l);

    void addNavItem(String name);
    void onNavSelect(java.util.function.Consumer<String> handler);
    void showPage(String name);





    void registerPage(String name, javax.swing.JComponent page);

    void showWindow();
    void closeWindow();
}
