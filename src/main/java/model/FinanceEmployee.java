package model;

import java.util.EnumSet;

/**
 * Represents the finance employee component used in the model layer.
 */
public class FinanceEmployee extends Employee {

    /**
     * Creates a new FinanceEmployee instance.
     * @param data input value needed by this method.
     */
    public FinanceEmployee(String[] data) {
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
