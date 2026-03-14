package model;

import java.util.EnumSet;

/**
 * Represents the regular employee component used in the model layer.
 */
public class RegularEmployee extends Employee {

    /**
     * Creates a new RegularEmployee instance.
     * @param data input value needed by this method.
     */
    public RegularEmployee(String[] data) {
        super(data);
    }

    
    /**
     * Handles permissions.
     * @return resulting value produced by this method.
     */
    @Override
    protected EnumSet<Permission> permissions() {
        return EnumSet.of(Permission.VIEW_EMPLOYEES);
    }
}
