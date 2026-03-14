package model;

import java.util.EnumSet;

/**
 * Represents the hremployee component used in the model layer.
 */
public class HREmployee extends Employee {

    /**
     * Creates a new HREmployee instance.
     * @param data input value needed by this method.
     */
    public HREmployee(String[] data) {
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
                Permission.ADD_EMPLOYEES,
                Permission.EDIT_EMPLOYEES,
                Permission.DELETE_EMPLOYEES,
                Permission.APPROVE_LEAVE
        );
    }
}
