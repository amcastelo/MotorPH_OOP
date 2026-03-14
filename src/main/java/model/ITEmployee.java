package model;

import java.util.EnumSet;

/**
 * Represents the itemployee component used in the model layer.
 */
public class ITEmployee extends Employee {

    /**
     * Creates a new ITEmployee instance.
     * @param data input value needed by this method.
     */
    public ITEmployee(String[] data) {
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
                Permission.ADD_EMAIL,
                Permission.ADMIN_SETTINGS
        );
    }
}
