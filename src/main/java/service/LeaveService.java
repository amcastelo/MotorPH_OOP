/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.EmployeeFileManager.Result;
import dao.LeaveLedgerRepository;
import java.time.LocalDate;
import model.Employee;
import model.LeaveRequest;

/**
 *
 * @author AlPC
 */
public class LeaveService {
    public Result submitLeaveRequest(Employee employee, LocalDate start, LocalDate end, String type, String reason) {
        if (employee == null) {
            return Result.fail("No logged-in employee found.");
        }

        if (start == null || end == null) {
            return Result.fail("Please select start and end dates.");
        }

        if (start.isBefore(LocalDate.now())) {
            return Result.fail("Start date must be today or a future date.");
        }

        if (end.isBefore(start)) {
            return Result.fail("End date must not be earlier than start date.");
        }

        if (type == null || type.isBlank()) {
            return Result.fail("Please select a leave type.");
        }

        if (reason == null || reason.trim().isBlank()) {
            return Result.fail("Please enter a reason for your leave request.");
        }

        LeaveRequest req = LeaveRequest.newPending(employee, start, end, type, reason.trim());

        LeaveLedgerRepository repository = new LeaveLedgerRepository();

        try {
            repository.upsert(req);
            return Result.ok("Leave request submitted.");
        } catch (Exception ex) {
            return Result.fail("Failed to save leave request: " + ex.getMessage());
        }
    }
}
