package model;

import java.util.EnumSet;

/**
 * Represents the admin employee component used in the model layer.
 */
public class AdminEmployee extends Employee {

    /**
     * Creates a new AdminEmployee instance.
     * @param data input value needed by this method.
     */
    public AdminEmployee(String[] data) {
        super(data);
    }

    
    /**
     * Handles permissions.
     * @return resulting value produced by this method.
     */
    @Override
    protected EnumSet<Permission> permissions() {
        return EnumSet.allOf(Permission.class);
    }
}
