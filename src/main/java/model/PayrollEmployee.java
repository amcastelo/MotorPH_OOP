package model;

import java.util.EnumSet;

/**
 * Represents the payroll employee component used in the model layer.
 */
public class PayrollEmployee extends Employee {

    /**
     * Creates a new PayrollEmployee instance.
     * @param data input value needed by this method.
     */
    public PayrollEmployee(String[] data) {
        super(data);
    }
    
    /**
     * Handles permissions.
     * @return resulting value produced by this method.
     */
    @Override
    protected EnumSet<Permission> permissions() {
        return EnumSet.of(
                Permission.VIEW_EMPLOYEES,
                Permission.VIEW_PAYROLL,
                Permission.RUN_PAYROLL
        );
    }
}
